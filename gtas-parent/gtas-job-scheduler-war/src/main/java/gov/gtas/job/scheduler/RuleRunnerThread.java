/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler;

import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.HitDetail;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.services.RuleHitPersistenceService;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.util.RuleResultsWithMessageStatus;
import gov.gtas.svc.util.TargetingResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static gov.gtas.repository.AppConfigurationRepository.FUZZY_MATCHING;

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

	public RuleRunnerThread(ErrorPersistenceService errorPersistenceService,
			AppConfigurationService appConfigurationService, ApplicationContext applicationContext) {
		this.errorPersistenceService = errorPersistenceService;
		this.appConfigurationService = appConfigurationService;
		this.applicationContext = applicationContext;
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
				for (Set<HitDetail> hitDetailSet : batchedTargetingServiceResults) {
					try {
						logger.debug("Saving rule hit details results batch " + count + " of "
								+ batchedTargetingServiceResults.size() + "...");
						persistenceService.persistToDatabase(hitDetailSet);
					} catch (Exception ignored) {
						ruleResults.getMessageStatusList()
								.forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.PARTIAL_ANALYZE));
						logger.error("Failed to save rules summary count " + count + " with following stacktrace: ",
								ignored);
					}
					count++;
				}
				logger.info("Rules and Watchlist ran in " + (System.nanoTime() - start) / 1000000 + "m/s.");
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

	void setMessageStatuses(List<MessageStatus> messageStatuses) {
		this.messageStatuses = messageStatuses;
	}
}
