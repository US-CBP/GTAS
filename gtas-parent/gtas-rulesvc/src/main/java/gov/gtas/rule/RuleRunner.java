/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule;

import gov.gtas.config.CommonServicesConfig;
import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.svc.TargetingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * A Java application for running the Rule Engine in stand alone mode.
 *
 */
public class RuleRunner {

	private static final Logger logger = LoggerFactory
			.getLogger(RuleRunner.class);

	private RuleRunner() {

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		logger.info("Entering main().");
		ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(
				CommonServicesConfig.class);
		TargetingService targetingService = (TargetingService) ctx
				.getBean("targetingServiceImpl");

		try {
			targetingService.preProcessing();
			targetingService.runningRuleEngine();
			logger.info("Exiting main().");
		} catch (Exception exception) {
			logger.debug(exception.getMessage());
			ErrorDetailInfo errInfo = ErrorHandlerFactory.createErrorDetails(
					RuleServiceConstants.RULE_ENGINE_RUNNER_ERROR_CODE,
					exception);
			ErrorPersistenceService errorService = (ErrorPersistenceService) ctx
					.getBean("errorPersistenceServiceImpl");
			errorService.create(errInfo);
		} finally {
			ctx.close();
		}
		System.exit(0);
	}
}
