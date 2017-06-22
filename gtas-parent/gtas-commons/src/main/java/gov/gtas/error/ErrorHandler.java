/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import java.util.List;

public interface ErrorHandler {
    /**
     * Creates the exception message for the indicated error.
     * 
     * @param errorCode
     *            the error code.
     * @param args
     *            the error arguments providing context for the error.
     * @return the error exception object.
     */
    CommonServiceException createException(final String errorCode,
            final Object... args);

    /**
     * Creates the exception message for the indicated error.
     * 
     * @param errorCode
     *            the error code.
     * @param cause
     *            The underlying exception.
     * @param args
     *            the error arguments providing context for the error.
     * @return the error exception object.
     */
    CommonServiceException createException(final String errorCode,
            final Exception cause, final Object... args);

    /**
     * Adds a error handling delegate to the error handler.
     * 
     * @param errorHandler
     *            the delegate.
     */
    void addErrorHandlerDelegate(ErrorHandler errorHandler);

    /**
     * Analyzes the error and produces detailed diagnostics.
     * 
     * @param exception
     *            the error.
     * @return the diagnostics.
     */
    ErrorDetailInfo processError(Exception exception);
    
    /**
     * Records and logs error.
     * 
     * @param code
     *            the error code.
     * @param description
     *            the error description.
     * @param details
     *            the error details.
     * @return the diagnostics.
     */
    ErrorDetailInfo processError(String code, String description, List<String> details);
}
