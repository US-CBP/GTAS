/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Error processing utility functions.
 */
public final class ErrorUtils {
	private ErrorUtils() {
	}

	public static void constructExceptionDetails(Throwable exception, List<String> details) {
		details.add("Exception class:" + exception.getClass().getSimpleName());
		details.add("Exception messsage:" + exception.getMessage());
		for (StackTraceElement el : exception.getStackTrace()) {
			details.add(el.toString());
		}
		if (exception.getCause() != null) {
			details.add(">>>>>>>> Caused by:");
			constructExceptionDetails(exception.getCause(), details);
		}
	}

	private static final int MAX_ERROR_LENG = 4000;

	public static String getStacktrace(Throwable e) {
		String stacktrace = ExceptionUtils.getStackTrace(e);
		if (stacktrace.length() > MAX_ERROR_LENG) {
			stacktrace = stacktrace.substring(0, MAX_ERROR_LENG);
		}
		return stacktrace;
	}
}
