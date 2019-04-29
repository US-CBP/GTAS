/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.model.Case;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.svc.TargetingServiceResults;
import gov.gtas.svc.util.RuleResultsWithMessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.svc.TargetingService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Rule Runner Scheduler class. Using Spring's Scheduled annotation for
 * scheduling tasks. The class reads configuration values from an external file.
 *
 */
@Component
public class RuleRunnerScheduler {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(RuleRunnerScheduler.class);

	/** The targeting service. */
	private TargetingService targetingService;

	/** The error persistence service. */
	private ErrorPersistenceService errorPersistenceService;

	@Autowired
	private AppConfigurationService appConfigurationService;


	@Autowired
	private MatchingService matchingService;

	/**
	 * Instantiates a new rule runner scheduler.
	 *
	 * @param targetingService
	 *            the targeting service
	 * @param errorPersistenceService
	 *            the error persistence service
	 */
	@Autowired
	public RuleRunnerScheduler(TargetingService targetingService, ErrorPersistenceService errorPersistenceService) {
		this.targetingService = targetingService;
		this.errorPersistenceService = errorPersistenceService;
	}

	/**
	 * Job scheduling.
	 */
	//This is commented out as a scheduled task in order to remove concurrency issues in the DB involving loader and rule runner. This may not be the final solution to the problem
	//but it suffices for now. The rule running portion of the scheduler is now tacked into the loader portion at the bottom to insure sequential operation.
	@Scheduled(fixedDelayString = "${ruleRunner.fixedDelay.in.milliseconds}", initialDelayString = "${ruleRunner.initialDelay.in.milliseconds}")
	public void jobScheduling() {
		logger.debug("Starting rule running scheduled task");
		long start = System.nanoTime();
		RuleResultsWithMessageStatus ruleResults = null;
		try {
			ruleResults = targetingService.runningRuleEngine();
			List<TargetingServiceResults> targetingServiceResultsList = targetingService.createHitsAndCases(ruleResults.getRuleResults());
			logger.debug("About to batch");
			List<TargetingServiceResults> batchedTargetingServiceResults = batchResults(targetingServiceResultsList);
			logger.debug("done batching");
			int count = 1;
			if (ruleResults.getMessageStatusList() != null) {
				Date analyzedAt = new Date();
				ruleResults.getMessageStatusList().forEach(m -> {
					m.setMessageStatusEnum(MessageStatusEnum.ANALYZED);
					m.setAnalyzedTimestamp(analyzedAt);
				});
				for (TargetingServiceResults targetingServiceResults : batchedTargetingServiceResults) {
					try {
						logger.info("Saving rules/summary targeting results " + count + " of " + batchedTargetingServiceResults.size() + "...");
						targetingService.saveEverything(targetingServiceResults);
					} catch (Exception ignored) {
						ruleResults.getMessageStatusList().forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.PARTIAL_ANALYZE));
						logger.error("Failed to save rules summary count " + count + " with following stacktrace: ", ignored);
					}
					count++;
				}
				targetingService.saveMessageStatuses(ruleResults.getMessageStatusList());
				logger.info("Rules and Watchlist ran in "+(System.nanoTime()-start)/1000000 + "m/s.");
			}
			logger.debug("entering matching service portion of jobScheduling");
			long fuzzyStart = System.nanoTime();
			int passengersProcessed = matchingService.findMatchesBasedOnTimeThreshold();
			logger.debug("exiting matching service portion of jobScheduling");
			if (passengersProcessed > 0) {
				logger.info("Fuzzy Matching Run in  " + (System.nanoTime() - fuzzyStart) / 1000000 + "m/s.");
			}
			logger.debug("Total rule running scheduled task took  " + (System.nanoTime() - start) / 1000000 + "m/s.");
		} catch (Exception exception) {
			String errorMessage = exception.getCause() != null && exception.getCause().getMessage() != null ? exception.getCause().getMessage(): "Error in rule runner";
			logger.error(errorMessage);
			if (ruleResults != null) {
				ruleResults.getMessageStatusList().forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING));
				targetingService.saveMessageStatuses(ruleResults.getMessageStatusList());
			}
			ErrorDetailInfo errInfo = ErrorHandlerFactory
					.createErrorDetails(RuleServiceConstants.RULE_ENGINE_RUNNER_ERROR_CODE, exception);
			errorPersistenceService.create(errInfo);
		}
		logger.debug("exiting jobScheduling()");
	}

    private List<TargetingServiceResults> batchResults(List<TargetingServiceResults> targetingServiceResultsList) {

	    int BATCH_SIZE = Integer.parseInt(appConfigurationService.findByOption(AppConfigurationRepository.MAX_FLIGHTS_SAVED_PER_BATCH).getValue());
        List<TargetingServiceResults> batchedResults = new ArrayList<>();
        TargetingServiceResults conglomerateResults = new TargetingServiceResults();
        int counter = 0;
        while(!targetingServiceResultsList.isEmpty()) {
            TargetingServiceResults targetingServiceResults = targetingServiceResultsList.get(0);
            Set<Case> casesSet = conglomerateResults.getCaseSet();
            List<HitsSummary> hitsSummaries = conglomerateResults.getHitsSummaryList();
            if (casesSet == null) {
                conglomerateResults.setCaseSet(targetingServiceResults.getCaseSet());
            } else {
                conglomerateResults.getCaseSet().addAll(targetingServiceResults.getCaseSet());
            }
            if (hitsSummaries == null) {
                conglomerateResults.setHitsSummaryList(targetingServiceResults.getHitsSummaryList());
            } else {
                conglomerateResults.getHitsSummaryList().addAll(targetingServiceResults.getHitsSummaryList());
            }
            counter ++;
            if (targetingServiceResultsList.size() == 1) {
                batchedResults.add(conglomerateResults);
            } else {
                if (counter >= BATCH_SIZE) {
                    batchedResults.add(conglomerateResults);
                    conglomerateResults = new TargetingServiceResults();
                    counter = 1;
                }
            }
            targetingServiceResultsList.remove(0);
        }
        return batchedResults;
    }
}
