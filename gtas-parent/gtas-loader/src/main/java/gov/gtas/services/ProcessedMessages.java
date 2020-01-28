package gov.gtas.services;

import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.tamr.model.TamrPassenger;

import java.util.List;

public class ProcessedMessages {
	private int[] processed;
	private List<MessageStatus> messageStatusList;
	private List<TamrPassenger> tamrPassengers;
	public int[] getProcessed() {
		return processed;
	}

	public void setProcessed(int[] processed) {
		this.processed = processed;
	}

	public List<MessageStatus> getMessageStatusList() {
		return messageStatusList;
	}

	public void setMessageStatusList(List<MessageStatus> messageStatusList) {
		this.messageStatusList = messageStatusList;
	}

	public List<TamrPassenger> getTamrPassengers() {
		return tamrPassengers;
	}

	public void setTamrPassengers(List<TamrPassenger> tamrPassengers) {
		this.tamrPassengers = tamrPassengers;
	}
}
