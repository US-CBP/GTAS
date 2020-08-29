package gov.gtas.services;

import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.parsers.omni.model.OmniPassenger;
import gov.gtas.summary.MessageSummary;

import java.util.List;

public class ProcessedMessages {
	private int[] processed;
	private List<MessageStatus> messageStatusList;
	private List<TamrPassenger> tamrPassengers;
	private List<OmniPassenger> omniPassengers;
	private List<MessageSummary> messageSummaries;
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

	public List<OmniPassenger> getOmniPassengers() {
		return omniPassengers;
	}

	public void setOmniPassengers(List<OmniPassenger> omniPassengers) {
		this.omniPassengers = omniPassengers;
	}

	public List<MessageSummary> getMessageSummaries() {
		return messageSummaries;
	}

	public void setMessageSummaries(List<MessageSummary> messageSummaries) {
		this.messageSummaries = messageSummaries;
	}
}
