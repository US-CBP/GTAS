/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.tamr.model.TamrPassengerSendObject;

import java.util.List;

public class MessageInformation {

	MessageStatus messageStatus;

	private List<TamrPassengerSendObject> tamrPassengerSendObjects;

	public MessageStatus getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(MessageStatus messageStatus) {
		this.messageStatus = messageStatus;
	}

	public void setTamrPassengerSendObjects(List<TamrPassengerSendObject> tamrPassengerSendObjects) {
		this.tamrPassengerSendObjects = tamrPassengerSendObjects;
	}

	public List<TamrPassengerSendObject> getTamrPassengerSendObjects() {
		return tamrPassengerSendObjects;
	}
}
