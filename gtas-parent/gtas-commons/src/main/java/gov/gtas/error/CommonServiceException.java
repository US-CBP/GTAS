/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;
/**
 * Exception class for errors generated during Rule Engine execution.
 */
public class CommonServiceException extends RuntimeException {
    private static final long serialVersionUID = -4115507029260625072L;

    /** The rule engine error code. */
    private String errorCode;
    
    /** If set to true then this error is logged in the database. */
    private boolean logable;
    
    public String getErrorCode() {
    	
        return errorCode;
    }
    
    /**
     * @return boolean the writeErrorLog
     * 
     */
    public boolean isLogable() {
        return logable;
    }

    /**
     * @param writeErrorLog the writeErrorLog to set
     */
    public void setLogable(boolean writeErrorLog) {
        this.logable = writeErrorLog;
    }

    /**
     * Constructor taking underlying exception as argument.
     * @param errCode the rule engine error code.
     * @param msg additional context dependent error message.
     * @param exception the causing exception.
     */
    public CommonServiceException(final String errCode, final String msg, final Throwable exception) {
        super(msg, exception);
        errorCode = errCode;
    }
    /**
     * Construction taking error code and context dependent message.
     * @param errCode the rule engine error code.
     * @param errMessage additional context dependent error message.
     */
    public CommonServiceException(final String errCode, final String errMessage) {
        super(errMessage);
        errorCode = errCode;
    }
    
}
