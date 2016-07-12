/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.exception;

public class ParseException extends Exception {
    private static final long serialVersionUID = 1L;  
    public ParseException() {}
    public ParseException(String message) {
       super(message);
    }
}
