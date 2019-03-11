/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.model.Case;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.MessageStatusEnum;
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
		logger.info("Starting rule running scheduled task");
		long start = System.nanoTime();
		try {
			RuleResultsWithMessageStatus ruleResults = targetingService.runningRuleEngine();
			List<TargetingServiceResults> targetingServiceResultsList = targetingService.createHitsAndCases(ruleResults.getRuleResults());
			List<TargetingServiceResults> batchedTargetingServiceResults = batchResults(targetingServiceResultsList);
			int count = 1;
			ruleResults.getMessageStatusList().forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.ANALYZED));
			for (TargetingServiceResults targetingServiceResults : batchedTargetingServiceResults) {
				try {
					targetingService.saveEverything(targetingServiceResults);
					logger.info("Saved rules/summary targeting results " + count + " of " + batchedTargetingServiceResults.size() + ".");
				} catch (Exception ignored) {
					ruleResults.getMessageStatusList().forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.PARTIAL_ANALYZE));
					logger.error("Failed to save rules summary count " + count + " with following stacktrace: ", ignored);
				}
                count++;
            }
			targetingService.saveMessageStatuses(ruleResults.getMessageStatusList());
			logger.info("Rules and Watchlist ran in "+(System.nanoTime()-start)/1000000 + "m/s.");
			logger.debug("entering matching service portion of jobScheduling");
			long fuzzyStart = System.nanoTime();
			matchingService.findMatchesBasedOnTimeThreshold();
			logger.debug("exiting matching service portion of jobScheduling");
			logger.info("Fuzzy Matching Run in  "+(System.nanoTime()-fuzzyStart)/1000000 + "m/s.");
			logger.info("Total rule running scheduled task took  "+(System.nanoTime()-start)/1000000 + "m/s.");
		} catch (Exception exception) {
			String errorMessage = exception.getCause() != null && exception.getCause().getMessage() != null ? exception.getCause().getMessage(): "Error in rule runner";
			logger.error(errorMessage);
			ErrorDetailInfo errInfo = ErrorHandlerFactory
					.createErrorDetails(RuleServiceConstants.RULE_ENGINE_RUNNER_ERROR_CODE, exception);
			errorPersistenceService.create(errInfo);
		}
		logger.debug("exiting jobScheduling()");
	}

    private List<TargetingServiceResults> batchResults(List<TargetingServiceResults> targetingServiceResultsList) {

	    int BATCH_SIZE = 50;
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
