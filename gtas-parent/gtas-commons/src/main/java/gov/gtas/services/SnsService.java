/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import com.amazonaws.services.sns.AmazonSNS;

public interface SnsService {

	public String sendNotification(AmazonSNS amazonSNS, String message, String subject, String arn);
}
