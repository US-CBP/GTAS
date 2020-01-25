package gov.gtas.services;

import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.tamr.model.TamrPassengerSendObject;

import java.util.List;

public class ProcessedMessages {
	private int[] processed;
	private List<MessageStatus> messageStatusList;
	private List<TamrPassengerSendObject> tamrPassengerSendObjectList;
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

	public List<TamrPassengerSendObject> getTamrPassengerSendObjectList() {
		return tamrPassengerSendObjectList;
	}

	public void setTamrPassengerSendObjectList(List<TamrPassengerSendObject> tamrPassengerSendObjectList) {
		this.tamrPassengerSendObjectList = tamrPassengerSendObjectList;
	}
}
