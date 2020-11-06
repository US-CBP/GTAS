/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.MessageStatus;
import gov.gtas.model.PassengerNote;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.parsers.omni.model.OmniPassenger;
import gov.gtas.summary.MessageSummary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageInformation {

	MessageStatus messageStatus;

	private List<TamrPassenger> tamrPassengers;
	private List<OmniPassenger> omniPassengers;

	private MessageSummary messageSummary = new MessageSummary();

	private Set<PendingHitDetails> pendingHitDetailsSet = new HashSet<>();
	private Set<PassengerNote> passengerNotes = new HashSet<>();

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

	public void setOmniPassengers(List<OmniPassenger> omniPassengers) {
		this.omniPassengers = omniPassengers;
	}

	public List<OmniPassenger> getOmniPassengers() { return omniPassengers;}

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

	public Set<PassengerNote> getPassengerNotes() {
		return passengerNotes;
	}

	public void setPassengerNotes(Set<PassengerNote> passengerNotes) {
		this.passengerNotes = passengerNotes;
	}
}
