/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.Set;

import gov.gtas.aws.HitNotificationConfig;

public interface NotificatonService {

	Set<String> sendHitNotifications(HitNotificationConfig config);
}
