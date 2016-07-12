/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.exceptions;

public class InvalidUserRepositoryException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public InvalidUserRepositoryException(String message) {
        super(message);
    }
}
