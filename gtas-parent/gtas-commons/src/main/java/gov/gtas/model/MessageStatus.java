/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

public enum MessageStatus {
    RECEIVED,  // 0
    PARSED,  // 1
    LOADED,  // 2
    ANALYZED,  // 3
    FAILED_PARSING,  // 4
    FAILED_LOADING,  // 5
    FAILED_ANALYZING;  // 6
}
