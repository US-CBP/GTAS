/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.MessageStatus;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.summary.MessageSummary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageInformation {

	MessageStatus messageStatus;

	private List<TamrPassenger> tamrPassengers;

	private MessageSummary messageSummary = new MessageSummary();

	private Set<PendingHitDetails> pendingHitDetailsSet = new HashSet<>();

	public MessageStatus getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(MessageStatus messageStatus) {
		this.messageStatus = messageStatus;
	}

	public void setTamrPassengers(List<TamrPassenger> tamrPassengerSendObjects) {
		this.tamrPassengers = tamrPassengerSendObjects;
	}

	public List<TamrPassenger> getTamrPassengers() {
		return tamrPassengers;
	}

	public MessageSummary getMessageSummary() {
		return messageSummary;
	}

	public void setMessageSummary(MessageSummary messageSummary) {
		this.messageSummary = messageSummary;
	}

	public Set<PendingHitDetails> getPendingHitDetailsSet() {
		return pendingHitDetailsSet;
	}

	public void setPendingHitDetailsSet(Set<PendingHitDetails> pendingHitDetailsSet) {
		this.pendingHitDetailsSet = pendingHitDetailsSet;
	}
}
