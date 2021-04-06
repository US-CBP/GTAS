package gov.gtas.job.scheduler;

import static gov.gtas.repository.AppConfigurationRepository.FUZZY_MATCHING;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.HitDetail;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.rule.KIEAndLastUpdate;
import gov.gtas.services.AdditionalProcessingService;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.services.NotificatonService;
import gov.gtas.services.RuleHitPersistenceService;
import gov.gtas.services.email.HitEmailNotificationService;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.TargetingResultUtils;

@Component
@Scope("prototype")
public class HitPersistenceThread extends RuleThread implements Callable<Boolean> {

	private Logger logger = LoggerFactory.getLogger(HitPersistenceThread.class);

	private Map<String, KIEAndLastUpdate> rules = new HashMap<>();

	private final ApplicationContext applicationContext;
	/**
	 * The error persistence service.
	 */
	private ErrorPersistenceService errorPersistenceService;

	private final AppConfigurationService appConfigurationService;

	private List<MessageStatus> messageStatuses = new ArrayList<>();

	private RuleExecutionContext ruleExecutionContext;

	private HitEmailNotificationService hitEmailNotificationService;
	
	private Set<HitDetail> hitDetails; 
	

	public HitPersistenceThread(ErrorPersistenceService errorPersistenceService,
			AppConfigurationService appConfigurationService, ApplicationContext applicationContext,
			NotificatonService notificationSerivce, HitEmailNotificationService hitEmailNotificationService) {
		this.errorPersistenceService = errorPersistenceService;
		this.appConfigurationService = appConfigurationService;
		this.applicationContext = applicationContext;
		this.hitEmailNotificationService = hitEmailNotificationService;
	}

	@Override
	public Boolean call() throws Exception {
		Boolean success = false;
		TargetingService targetingService = applicationContext.getBean(TargetingService.class);
		RuleHitPersistenceService persistenceService = applicationContext.getBean(RuleHitPersistenceService.class);
		AdditionalProcessingService additionalProcessingService = applicationContext.getBean(AdditionalProcessingService.class);
		long start = System.nanoTime();
		try {
			logger.debug("About to batch");
			int batchSize = Integer.parseInt(appConfigurationService
				.findByOption(AppConfigurationRepository.MAX_RULE_DETAILS_CREATED).getValue());
		List<Set<HitDetail>> batchedTargetingServiceResults = TargetingResultUtils.batchResults(hitDetails,
				batchSize);
		logger.debug("done batching");
		if (messageStatuses != null) {
			Date analyzedAt = new Date();
			messageStatuses.forEach(m -> {
				// status set by other tasks
				m.setAnalyzedTimestamp(analyzedAt);
			});
			processHits(messageStatuses, persistenceService, batchedTargetingServiceResults, additionalProcessingService);
			logger.info("Persisted hit deatils ran in {} m/s.", (System.nanoTime() - start) / 1000000);
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
		if (messageStatuses != null) {
			targetingService.saveMessageStatuses(messageStatuses);
		}
		logger.debug("Total rule running scheduled task took  " + (System.nanoTime() - start) / 1000000 + "m/s.");
	} catch (Exception exception) {
		String errorMessage = exception.getCause() != null && exception.getCause().getMessage() != null
				? exception.getCause().getMessage()
				: "Error in rule runner";
		logger.error(errorMessage);
		if (messageStatuses == null) {
			logger.error("Message status list in hit persistence is null!");
		} else {
			messageStatuses.forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING));
			targetingService.saveMessageStatuses(messageStatuses);
		}
		ErrorDetailInfo errInfo = ErrorHandlerFactory
				.createErrorDetails(RuleServiceConstants.RULE_ENGINE_RUNNER_ERROR_CODE, exception);
		errorPersistenceService.create(errInfo);
		success = false;
	}
	logger.debug("exiting jobScheduling()");
	return success;
	}

	public RuleExecutionContext getRuleExecutionContext() {
		return ruleExecutionContext;
	}

	public void setRuleExecutionContext(RuleExecutionContext ruleExecutionContext) {
		this.ruleExecutionContext = ruleExecutionContext;
	}

	public List<MessageStatus> getMessageStatuses() {
		return messageStatuses;
	}

	public void setMessageStatuses(List<MessageStatus> messageStatuses) {
		this.messageStatuses = messageStatuses;
	}

	public Set<HitDetail> getHitDetails() {
		return hitDetails;
	}

	public void setHitDetails(Set<HitDetail> hitDetails) {
		this.hitDetails = hitDetails;
	}

}
