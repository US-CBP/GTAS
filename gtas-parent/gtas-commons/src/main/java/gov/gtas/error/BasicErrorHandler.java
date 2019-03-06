/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import static gov.gtas.constant.CommonErrorConstants.INPUT_JSON_FORMAT_ERROR_CODE;
import static gov.gtas.constant.CommonErrorConstants.INPUT_JSON_FORMAT_ERROR_MESSAGE;
import static gov.gtas.constant.CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE;
import static gov.gtas.constant.CommonErrorConstants.INVALID_ARGUMENT_ERROR_MESSAGE;
import static gov.gtas.constant.CommonErrorConstants.INVALID_USER_ID_ERROR_CODE;
import static gov.gtas.constant.CommonErrorConstants.INVALID_USER_ID_ERROR_MESSAGE;
import static gov.gtas.constant.CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE;
import static gov.gtas.constant.CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_MESSAGE;
import static gov.gtas.constant.CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE;
import static gov.gtas.constant.CommonErrorConstants.NULL_ARGUMENT_ERROR_MESSAGE;
import static gov.gtas.constant.CommonErrorConstants.QUERY_RESULT_EMPTY_ERROR_CODE;
import static gov.gtas.constant.CommonErrorConstants.QUERY_RESULT_EMPTY_ERROR_MESSAGE;
import static gov.gtas.constant.CommonErrorConstants.UPDATE_RECORD_MISSING_ERROR_CODE;
import static gov.gtas.constant.CommonErrorConstants.UPDATE_RECORD_MISSING_ERROR_MESSAGE;
import static gov.gtas.constant.GtasSecurityConstants.UNAUTHORIZED_ERROR_CODE;
import static gov.gtas.constant.GtasSecurityConstants.UNAUTHORIZED_ERROR_MESSAGE;
import static gov.gtas.constant.RuleServiceConstants.NO_ENABLED_RULE_ERROR_CODE;
import static gov.gtas.constant.RuleServiceConstants.NO_ENABLED_RULE_ERROR_MESSAGE;
import gov.gtas.constant.CommonErrorConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common Error Handler for the Rule Engine related functionality.
 */
public class BasicErrorHandler implements ErrorHandler {

	/**
	 * The logger for the Rule Engine Error Handler
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(BasicErrorHandler.class);

	/**
	 * The map of all error codes handled by this handler.
	 */
	private final Map<String, String> errorMap;

	/**
	 * The map of all exception processors used by this handler.
	 */
	private final Map<String, Function<Exception, ErrorDetailInfo>> exceptionProcessorMap;

	/**
	 * The first handler in the delegate chain for this error handler;
	 */
	private ErrorHandler delegate;

	public BasicErrorHandler() {
		errorMap = new HashMap<String, String>();
		errorMap.put(NULL_ARGUMENT_ERROR_CODE, NULL_ARGUMENT_ERROR_MESSAGE);
		errorMap.put(INVALID_ARGUMENT_ERROR_CODE,
				INVALID_ARGUMENT_ERROR_MESSAGE);
		errorMap.put(INVALID_USER_ID_ERROR_CODE, INVALID_USER_ID_ERROR_MESSAGE);
		errorMap.put(INPUT_JSON_FORMAT_ERROR_CODE,
				INPUT_JSON_FORMAT_ERROR_MESSAGE);
		errorMap.put(UPDATE_RECORD_MISSING_ERROR_CODE,
				UPDATE_RECORD_MISSING_ERROR_MESSAGE);
		errorMap.put(QUERY_RESULT_EMPTY_ERROR_CODE,
				QUERY_RESULT_EMPTY_ERROR_MESSAGE);
		errorMap.put(JSON_INPUT_VALIDATION_ERROR_CODE,
				JSON_INPUT_VALIDATION_ERROR_MESSAGE);
		errorMap.put(UNAUTHORIZED_ERROR_CODE, UNAUTHORIZED_ERROR_MESSAGE);
		errorMap.put(NO_ENABLED_RULE_ERROR_CODE, NO_ENABLED_RULE_ERROR_MESSAGE);
		exceptionProcessorMap = new HashMap<String, Function<Exception, ErrorDetailInfo>>();
	}

	@Override
	public void addErrorHandlerDelegate(ErrorHandler errorHandler) {
		if (this.delegate == null) {
			this.delegate = errorHandler;
		} else {
			this.delegate.addErrorHandlerDelegate(errorHandler);
		}
	}

	@Override
	public CommonServiceException createException(String errorCode,
			Exception cause, Object... args) {
		CommonServiceException ret = null;
		final String errorMessage = errorMap.get(errorCode);
		if (errorMessage != null) {
			ret = createExceptionAndLog(errorCode, cause, errorMessage, args);
		} else if (this.delegate != null) {
			ret = this.delegate.createException(errorCode, cause, args);
		}
		if (ret == null) {
			ret = createExceptionAndLog(
					CommonErrorConstants.UNKNOWN_ERROR_CODE, cause,
					CommonErrorConstants.UNKNOWN_ERROR_CODE_MESSAGE, errorCode);
		}
		return ret;
	}

	@Override
	public CommonServiceException createException(String errorCode,
			Object... args) {
		return createException(errorCode, null, args);
	}

	@Override
	public ErrorDetailInfo processError(final Exception exception) {
		ErrorDetailInfo ret = null;
		logger.error("",exception);
		Function<Exception, ErrorDetailInfo> processor = exceptionProcessorMap
				.get(exception.getClass().getName());
		if (processor != null) {
			ret = processor.apply(exception);
		} else if (this.delegate != null) {
			ret = delegate.processError(exception);
		} else {
			logger.error(exception.getMessage());
			if (exception instanceof CommonServiceException) {
				ret = ErrorUtils
						.createErrorDetails((CommonServiceException) exception);
			} else {
				ret = ErrorUtils.createErrorDetails(exception);
			}
		}
		return ret;
	}

	@Override
	public ErrorDetailInfo processError(String code, String description,
			List<String> details) {
		ErrorDetailInfo ret = null;
		CommonServiceException exception = null;
		final String errorMessage = errorMap.get(code);
		if (errorMessage != null) {
			exception = createExceptionAndLog(code, null, errorMessage,
					details.toArray());
		} else if (this.delegate != null) {
			exception = this.delegate.createException(code, null,
					details.toArray());
		}
		if (exception != null) {
			ret = processError(exception);
		} else {
			ret = new BasicErrorDetailInfo(null, code, new Date(), description,
					details);
		}
		return ret;
	}

	/**
	 * Adds a custom exception handler using a lambda.
	 * 
	 * @param exceptionClass
	 *            the exception class to handle.
	 * @param processor
	 *            the lambda.
	 */
	protected void addCustomErrorProcesssor(
			Class<? extends Exception> exceptionClass,
			Function<Exception, ErrorDetailInfo> processor) {
		this.exceptionProcessorMap.put(exceptionClass.getName(), processor);
	}

	/**
	 * Adds the error code to the list of errors managed by this handler.
	 * 
	 * @param errCode
	 *            the error code to add.
	 * @param errMessage
	 *            the corresponding error message.
	 */
	protected void addErrorCodeToHandlerMap(String errCode, String errMessage) {
		String msg = errorMap.get(errCode);
		if (msg == null) {
			errorMap.put(errCode, errMessage);
		} else {
			// message already exists - log error
			logger.error(String
					.format("BasicErrorHandler.addErrorCodeToHandlerMap() - Duplicate errorCode '%s' (messsage = '%s').",
							errCode, errMessage));
		}
	}

	/**
	 * Creates the exception for the indicated error and also logs the error.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessageTemplate
	 *            string template for the error message.
	 * @param errorMessageArgs
	 *            the arguments for the error message template.
	 * @return the exception object.
	 */
	protected CommonServiceException createExceptionAndLog(String errorCode,
			Exception cause, String errorMessageTemplate,
			Object... errorMessageArgs) {
		String message = String.format(errorMessageTemplate, errorMessageArgs);
		if (cause == null) {
			return new CommonServiceException(errorCode, message);
		} else {
			return new CommonServiceException(errorCode, message, cause);
		}

	}
}
