package gov.gtas.services;

import gov.gtas.model.MessageStatus;

import java.util.List;

public class ProcessedMessages {
	private int[] processed;
	private List<MessageStatus> messageStatusList;

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
}
