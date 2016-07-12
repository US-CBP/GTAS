/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.validators;

/**
 * Parser produces value objects which need validation before store them in
 * database to avoid database exceptions and failures.
 */
public interface Validatable {
    public boolean isValid();
}
