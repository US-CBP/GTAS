/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import org.apache.commons.lang3.StringUtils;


/**
 * Factory class for creating error handlers.
 */
public class ErrorHandlerFactory {
    /*
     * This is the first element in the error handler chain. It is expected that
     * different modules will create their own specialized error handler and
     * attach it to the chain by calling registerErrorHandler.
     */
    private static ErrorHandler errorHandler = new BasicErrorHandler();
    private static ErrorHandler errorHandlerDelegateChain;

    /**
     * Creates the error handler chain.<br>
     * Note: It is expected that different modules will create their own
     * specialized error handler by deriving from the BasicErrorHandler class
     * and adding error codes and exception processors. This method is expected
     * to be called in the @PostConstruct method of a spring bean. Otherwise
     * this method should be called from the static initializer of the calling class.
     * @see gov.gtas.controller.UdrManagementController#addErrorHandlerDelegate(gov.gtas.error.GtasErrorHandler) 
     * @param errorHandler
     *            the handler to register.
     */
    public static synchronized void registerErrorHandler(
            ErrorHandler errorHandler) {
            if(ErrorHandlerFactory.errorHandlerDelegateChain ==  null){
                ErrorHandlerFactory.errorHandlerDelegateChain = errorHandler;
            } else {
                ErrorHandlerFactory.errorHandlerDelegateChain.addErrorHandlerDelegate(errorHandler);
            }
            ErrorHandler newErrorHandler = new BasicErrorHandler();
            newErrorHandler.addErrorHandlerDelegate(ErrorHandlerFactory.errorHandlerDelegateChain);
            
            //reference assignment is atomic.
            ErrorHandlerFactory.errorHandler = newErrorHandler;
    }

    /**
     * Gets the error handler.
     * 
     * @return the error handler.
     */
    public static ErrorHandler getErrorHandler() {
        return ErrorHandlerFactory.errorHandler;
    }
    /**
     * Processes the exception provided and returns the resulting error detail object.
     * 
     * @param exception exception.
     * @return the error details.
     */
    public static ErrorDetailInfo createErrorDetails(String errorCode, Exception cause, Object...args) {
        if(StringUtils.isEmpty(errorCode)){
            return ErrorHandlerFactory.errorHandler.processError(cause);
        } else {
            Exception ex = ErrorHandlerFactory.errorHandler.createException(errorCode, cause, args);
            return ErrorHandlerFactory.createErrorDetails(ex);
        }
    }
    /**
     * Processes the exception provided and returns the resulting error detail object.
     * 
     * @param exception exception.
     * @return the error details.
     */
    public static ErrorDetailInfo createErrorDetails(Exception exception) {
        return ErrorHandlerFactory.errorHandler.processError(exception);
    }
    
    public static void createAndThrowException(final String errorCode,
            final Object... args){
        throw ErrorHandlerFactory.errorHandler.createException(errorCode, args);
    }
}
