/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.io.IOException;
import java.util.Set;

import freemarker.template.TemplateException;
import gov.gtas.aws.HitNotificationConfig;
import gov.gtas.model.Passenger;

import javax.mail.MessagingException;

public interface NotificatonService {

	Set<String> sendHitNotifications(HitNotificationConfig config);
	void sendAutomatedHitEmailNotifications(Set<Passenger> passengers) throws IOException, TemplateException;
	void sendManualNotificationEmail(String[] to, String note, Long paxId, String userId) throws IOException, TemplateException, MessagingException;
}
