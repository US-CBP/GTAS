/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

public class DuplicateHashCodeException extends LoaderException {
    public DuplicateHashCodeException(String message) {
        super(message);
    }
}
