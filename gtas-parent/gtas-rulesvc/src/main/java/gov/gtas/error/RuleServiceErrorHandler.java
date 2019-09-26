/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import gov.gtas.constant.RuleServiceConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Error Handler for the Rule Engine related functionality.
 */
public class RuleServiceErrorHandler extends BasicErrorHandler {
	private static final Logger logger = LoggerFactory.getLogger(RuleServiceErrorHandler.class);

	/**
	 * Instantiates a new rule service error handler.
	 */
	public RuleServiceErrorHandler() {
		super();
		logger.info("RuleServiceErrorHandler - initializing handler map");
		super.addErrorCodeToHandlerMap(RuleServiceConstants.RULE_COMPILE_ERROR_CODE,
				RuleServiceConstants.RULE_COMPILE_ERROR_MESSAGE);
		super.addErrorCodeToHandlerMap(RuleServiceConstants.KB_CREATION_IO_ERROR_CODE,
				RuleServiceConstants.KB_CREATION_IO_ERROR_MESSAGE);
		super.addErrorCodeToHandlerMap(RuleServiceConstants.KB_NOT_FOUND_ERROR_CODE,
				RuleServiceConstants.KB_NOT_FOUND_ERROR_MESSAGE);
		super.addErrorCodeToHandlerMap(RuleServiceConstants.KB_INVALID_ERROR_CODE,
				RuleServiceConstants.KB_INVALID_ERROR_MESSAGE);
		super.addErrorCodeToHandlerMap(RuleServiceConstants.MESSAGE_NOT_FOUND_ERROR_CODE,
				RuleServiceConstants.MESSAGE_NOT_FOUND_ERROR_MESSAGE);
		super.addErrorCodeToHandlerMap(RuleServiceConstants.RULE_ENGINE_RUNNER_ERROR_CODE,
				RuleServiceConstants.RULE_ENGINE_RUNNER_ERROR_MESSAGE);
	}
}
