/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

public class LoaderException extends Exception {
    private static final long serialVersionUID = 1L;  
    public LoaderException() {}
    public LoaderException(String message) {
       super(message);
    }
}
