/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler.service;

import gov.gtas.job.scheduler.controller.WebMessage;

public interface MessageReceiverService {
	void putMessageOnQueue(WebMessage messagePayload);
}
