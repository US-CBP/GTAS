package gov.gtas.svc.util;

import gov.gtas.model.MessageStatus;

import java.util.List;

public class RuleResultsWithMessageStatus {

	private RuleResults ruleResults;

	public List<MessageStatus> getMessageStatusList() {
		return messageStatusList;
	}

	public void setMessageStatusList(List<MessageStatus> messageStatusList) {
		this.messageStatusList = messageStatusList;
	}

	private List<MessageStatus> messageStatusList;

	public RuleResults getRuleResults() {
		return ruleResults;
	}

	public void setRuleResults(RuleResults ruleResults) {
		this.ruleResults = ruleResults;
	}

}
