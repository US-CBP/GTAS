/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler;

import static gov.gtas.repository.AppConfigurationRepository.FUZZY_MATCHING;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import freemarker.template.TemplateException;
import gov.gtas.aws.HitNotificationConfig;
import gov.gtas.email.HitEmailNotificationService;
import gov.gtas.model.HitDetail;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.model.Passenger;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.services.NotificatonService;
import gov.gtas.services.RuleHitPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.util.RuleResultsWithMessageStatus;
import gov.gtas.svc.util.TargetingResultUtils;

@Component
@Scope("prototype")
public class RuleRunnerThread implements Callable<Boolean> {

	private Logger logger = LoggerFactory.getLogger(RuleRunnerThread.class);

	private final ApplicationContext applicationContext;
	/**
	 * The error persistence service.
	 */
	private ErrorPersistenceService errorPersistenceService;

	private final AppConfigurationService appConfigurationService;

	private List<MessageStatus> messageStatuses = new ArrayList<>();

	private NotificatonService notificationSerivce;

	private HitEmailNotificationService hitEmailNotificationService;

	@Value("${email.hit.notification.enabled}")
	private Boolean emailHitNotificationEnabled;

	public RuleRunnerThread(ErrorPersistenceService errorPersistenceService,
							AppConfigurationService appConfigurationService, ApplicationContext applicationContext,
							NotificatonService notificationSerivce, HitEmailNotificationService hitEmailNotificationService) {
		this.errorPersistenceService = errorPersistenceService;
		this.appConfigurationService = appConfigurationService;
		this.applicationContext = applicationContext;
		this.notificationSerivce = notificationSerivce;
		this.hitEmailNotificationService = hitEmailNotificationService;
	}

	public Boolean call() {
		logger.debug("Starting rule running scheduled task");
		boolean success = true;
		long start = System.nanoTime();
		RuleResultsWithMessageStatus ruleResults;
		TargetingService targetingService = applicationContext.getBean(TargetingService.class);
		RuleHitPersistenceService persistenceService = applicationContext.getBean(RuleHitPersistenceService.class);
		try {
			ruleResults = targetingService.analyzeLoadedMessages(messageStatuses);
			logger.debug("generating hit details");
			Set<HitDetail> hitDetails = targetingService.generateHitDetails(ruleResults.getRuleResults());
			logger.debug("About to batch");
			int BATCH_SIZE = Integer.parseInt(appConfigurationService
					.findByOption(AppConfigurationRepository.MAX_RULE_DETAILS_CREATED).getValue());
			List<Set<HitDetail>> batchedTargetingServiceResults = TargetingResultUtils.batchResults(hitDetails,
					BATCH_SIZE);
			logger.debug("done batching");
			if (ruleResults.getMessageStatusList() != null) {
				Date analyzedAt = new Date();
				ruleResults.getMessageStatusList().forEach(m -> {
					m.setMessageStatusEnum(MessageStatusEnum.ANALYZED);
					m.setAnalyzedTimestamp(analyzedAt);
				});
				int count = 1;
				Set<HitDetail> firstTimeHits = new HashSet<>();
				for (Set<HitDetail> hitDetailSet : batchedTargetingServiceResults) {
					try {
						logger.debug("Saving rule hit details results batch " + count + " of "
								+ batchedTargetingServiceResults.size() + "...");
						Iterable<HitDetail> hitDetailIterable = persistenceService.persistToDatabase(hitDetailSet);
						if (hitDetailIterable != null) {
							hitDetailIterable.forEach(firstTimeHits::add);
						}
					} catch (Exception ignored) {
						ruleResults.getMessageStatusList()
								.forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.PARTIAL_ANALYZE));
						logger.error("Failed to save rules summary count " + count + " with following stacktrace: ",
								ignored);
					}
					count++;
				}
				logger.info("Rules and Watchlist ran in {} m/s.", (System.nanoTime() - start) / 1000000);

				if (!firstTimeHits.isEmpty()) {
					// Send hit notifications using AWS SNS topic
					Set<Passenger> passengersWithFirstTimeHits = firstTimeHits.stream().map(HitDetail::getPassenger)
							.collect(Collectors.toSet());
					sendNotifications(passengersWithFirstTimeHits);
				}
			}
			logger.debug("entering matching service portion of jobScheduling");
			boolean fuzzyMatchingOn = false;
			try {
				fuzzyMatchingOn = Boolean.parseBoolean(appConfigurationService.findByOption(FUZZY_MATCHING).getValue());
			} catch (Exception e) {
				logger.warn("FUZZY MATCHING NOT CONFIGURED - DEFAULT WILL NOT RUN - FUZZY MATCHING IS OFF!"
						+ "\n SET FUZZY MATCHING IN APPLICATION CONFIG");
			}
			if (fuzzyMatchingOn) {
				long fuzzyStart = System.nanoTime();
				MatchingService matchingService = applicationContext.getBean(MatchingService.class);
				int fuzzyHits = matchingService.findMatchesBasedOnTimeThreshold(messageStatuses);
				logger.debug("exiting matching service portion of jobScheduling");
				if (fuzzyHits > 0) {
					logger.info("Fuzzy Matching had " + fuzzyHits + " hits and Ran in  "
							+ (System.nanoTime() - fuzzyStart) / 1000000 + "m/s.");
				}
			}
			if (ruleResults.getMessageStatusList() != null) {
				targetingService.saveMessageStatuses(ruleResults.getMessageStatusList());
			}
			logger.debug("Total rule running scheduled task took  " + (System.nanoTime() - start) / 1000000 + "m/s.");
		} catch (Exception exception) {
			String errorMessage = exception.getCause() != null && exception.getCause().getMessage() != null
					? exception.getCause().getMessage()
					: "Error in rule runner";
			logger.error(errorMessage);
			messageStatuses.forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING));
			targetingService.saveMessageStatuses(messageStatuses);
			ErrorDetailInfo errInfo = ErrorHandlerFactory
					.createErrorDetails(RuleServiceConstants.RULE_ENGINE_RUNNER_ERROR_CODE, exception);
			errorPersistenceService.create(errInfo);
			success = false;
		}
		logger.debug("exiting jobScheduling()");
		return success;
	}

	private void sendNotifications(Set<Passenger> passengersWithNewHits) {
		if (emailHitNotificationEnabled) {
			try {
				notificationSerivce.sendAutomatedHitEmailNotifications(passengersWithNewHits);
			} catch (IOException | TemplateException ignored) {
				//TODO: Add error handling. Do not propagate error up as partial matching still needs to happen.
				logger.error("There was an error within the email notification sender! ", ignored);
			}
		}

		boolean hitNotificationEnabled;
		try {
			hitNotificationEnabled = Boolean.parseBoolean(appConfigurationService
					.findByOption(AppConfigurationRepository.ENABLE_INTERPOL_HIT_NOTIFICATION).getValue());

			if (hitNotificationEnabled) {
				long notificationStart = System.nanoTime();
				HitNotificationConfig hitNotificationConfig = generateSnsHitNotificationConfig(passengersWithNewHits);
				notificationSerivce.sendHitNotifications(hitNotificationConfig);
				logger.info("Hit Notification sent, it took {} m/s", (System.nanoTime() - notificationStart) / 1000000);
			}
		} catch (Exception e) {
			logger.warn(
					"WATCHLIST HIT NOTIFICATION IS NOT CONFIGURED. SET NOTIFICATION IN DATABASE APP_CONFIGURATION TABLE.");
		}
	}

	private HitNotificationConfig generateSnsHitNotificationConfig(Set<Passenger> passengersWithNewHits) {
		String topicArn = appConfigurationService
				.findByOption(AppConfigurationRepository.INTERPOL_SNS_NOTIFICATION_ARN).getValue();
		String topicSubject = appConfigurationService
				.findByOption(AppConfigurationRepository.INTERPOL_SNS_NOTIFICATION_SUBJECT).getValue();
		// SET TO WATCH LIST CATEGORY ID FOR INTERPOL RED NOTICES
		Long interpolRedNoticesId = Long.parseLong(appConfigurationService
				.findByOption(AppConfigurationRepository.INTERPOL_WATCHLIST_ID).getValue());
		HitNotificationConfig hitNotificationConfig = new HitNotificationConfig(
				AmazonSNSClientBuilder.standard().build(), passengersWithNewHits, topicArn, topicSubject,
				interpolRedNoticesId);

		return hitNotificationConfig;
	}

	void setMessageStatuses(List<MessageStatus> messageStatuses) {
		this.messageStatuses = messageStatuses;
	}
}
