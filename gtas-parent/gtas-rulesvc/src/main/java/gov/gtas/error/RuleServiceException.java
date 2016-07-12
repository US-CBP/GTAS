/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import java.util.LinkedList;
import java.util.List;

import org.kie.api.builder.Message;

/**
 * Exception class for errors generated during Rule Engine execution.
 */
public class RuleServiceException extends CommonServiceException {

	private final List<String> errorMessages;

	/**
	 * serial version UID.
	 */
	private static final long serialVersionUID = -4115507029260625072L;

	/**
	 * Constructor taking underlying exception as argument.
	 * 
	 * @param errCode
	 *            the rule engine error code.
	 * @param msg
	 *            additional context dependent error message.
	 * @param exception
	 *            the causing exception.
	 */
	public RuleServiceException(final String errCode, final String msg,
			final Throwable exception) {
		super(errCode, msg, exception);
		this.errorMessages = new LinkedList<>();
	}

	/**
	 * Construction taking error code and context dependent message.
	 * 
	 * @param errCode
	 *            the rule engine error code.
	 * @param errMessage
	 *            additional context dependent error message.
	 */
	public RuleServiceException(final String errCode, final String errMessage) {
		super(errCode, errMessage);
		this.errorMessages = new LinkedList<>();
	}

	/**
	 * Captures Rule Engine specific errors.
	 * 
	 * @param msg
	 *            rule engine error message.
	 */
	public void addRuleCompilationError(Message msg) {
		this.errorMessages.add(msg.getText());
	}

	@Override
	public String getMessage() {
		StringBuilder msgBuilder = new StringBuilder(super.getMessage());
		if (!errorMessages.isEmpty()) {
			msgBuilder.append("\nRule Engine errors:\n");
			for (String msg : errorMessages) {
				msgBuilder.append(msg).append("\n");
			}
		}
		return msgBuilder.toString();
	}

}
