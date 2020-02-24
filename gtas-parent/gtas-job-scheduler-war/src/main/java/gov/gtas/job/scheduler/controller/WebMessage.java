/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler.controller;

import java.io.Serializable;

//Message for endpoint.
public class WebMessage implements Serializable {

	public WebMessage() {
	}

	private String messagePayload;

	@SuppressWarnings("unused")
	public String getMessagePayload() {
		return messagePayload;
	}

	public void setMessagePayload(String messagePayload) {
		this.messagePayload = messagePayload;
	}
}
