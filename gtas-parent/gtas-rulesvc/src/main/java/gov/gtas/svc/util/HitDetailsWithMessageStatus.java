package gov.gtas.svc.util;

import java.util.List;
import java.util.Set;

import gov.gtas.model.HitDetail;
import gov.gtas.model.MessageStatus;

public class HitDetailsWithMessageStatus {
	
	Set<HitDetail> hitDetails;
	List<MessageStatus> messageStatuses;
	
	public Set<HitDetail> getHitDetails() {
		return hitDetails;
	}
	public void setHitDetails(Set<HitDetail> hitDetails) {
		this.hitDetails = hitDetails;
	}
	public List<MessageStatus> getMessageStatuses() {
		return messageStatuses;
	}
	public void setMessageStatuses(List<MessageStatus> messageStatuses) {
		this.messageStatuses = messageStatuses;
	}
	
	
}
