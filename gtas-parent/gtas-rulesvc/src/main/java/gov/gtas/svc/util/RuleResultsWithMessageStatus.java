package gov.gtas.svc.util;

import gov.gtas.model.MessageStatus;

import java.util.List;


public class RuleResultsWithMessageStatus {

	private RuleResults ruleResults;
	private List<MessageStatus> messageStatusList;
	private int number;
	private String queueName;
	
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<MessageStatus> getMessageStatusList() {
		return messageStatusList;
	}

	public void setMessageStatusList(List<MessageStatus> messageStatusList) {
		this.messageStatusList = messageStatusList;
	}


	public RuleResults getRuleResults() {
		return ruleResults;
	}

	public void setRuleResults(RuleResults ruleResults) {
		this.ruleResults = ruleResults;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

}
