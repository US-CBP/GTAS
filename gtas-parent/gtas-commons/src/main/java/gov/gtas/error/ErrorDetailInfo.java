/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

public interface ErrorDetailInfo {
	Long getErrorId();

	String getErrorCode();

	String getErrorDescription();

	String getErrorTimestamp();

	String[] getErrorDetails();
}
