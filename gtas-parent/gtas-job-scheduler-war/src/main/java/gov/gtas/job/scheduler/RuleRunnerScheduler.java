/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.svc.TargetingService;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Rule Runner Scheduler class. Using Spring's Scheduled annotation for
 * scheduling tasks. The class reads configuration values from an external file.
 *
 */
@Component
public class RuleRunnerScheduler {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(RuleRunnerScheduler.class);

	/** The targeting service. */
	private TargetingService targetingService;

	/** The error persistence service. */
	private ErrorPersistenceService errorPersistenceService;

	/**
	 * Instantiates a new rule runner scheduler.
	 *
	 * @param targetingService
	 *            the targeting service
	 * @param errorPersistenceService
	 *            the error persistence service
	 */
	@Autowired
	public RuleRunnerScheduler(TargetingService targetingService,
			ErrorPersistenceService errorPersistenceService) {
		this.targetingService = targetingService;
		this.errorPersistenceService = errorPersistenceService;
	}

	/**
	 * Job scheduling.
	 */
	@Scheduled(fixedDelayString = "${ruleRunner.fixedDelay.in.milliseconds}", initialDelayString = "${ruleRunner.initialDelay.in.milliseconds}")
	public void jobScheduling() {
		logger.info("entering jobScheduling()");
		try {
			targetingService.preProcessing();
			Set<Long> uniqueFlights = targetingService.runningRuleEngine();
			targetingService.updateFlightHitCounts(uniqueFlights);
		} catch (Exception exception) {
			String msg = null;
			if (exception.getCause() != null) {
				msg = exception.getCause().getMessage();
			} else {
				msg = exception.getMessage();
			}
			logger.error(msg);
			ErrorDetailInfo errInfo = ErrorHandlerFactory.createErrorDetails(
					RuleServiceConstants.RULE_ENGINE_RUNNER_ERROR_CODE,
					exception);
			errorPersistenceService.create(errInfo);
		}
		logger.info("exiting jobScheduling()");
	}

}
