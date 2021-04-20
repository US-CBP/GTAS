/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

import gov.gtas.model.AuditRecord;
import gov.gtas.model.User;

public class AuditRecordVo {
	private String actionType;
	private String status;
	private String message;
	private String user;
	private String userName;
	private Long timestamp;	

	public AuditRecordVo() {
	}

	public AuditRecordVo(AuditRecord auditRecord) {
		this.actionType = auditRecord.getActionType().toString();
		this.status = auditRecord.getActionStatus().toString();
		this.message = auditRecord.getMessage();
		final User usr = auditRecord.getUser();
		this.user = usr.getUserId();
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String userId) {
		this.user = userId;
	}
	
	public static AuditRecordVo from(AuditRecord auditRecord) {
		AuditRecordVo vo = new AuditRecordVo();
		final User user = auditRecord.getUser();
		
		vo.setActionType(auditRecord.getActionType().toString());
		vo.setStatus(auditRecord.getActionStatus().toString());
		vo.setMessage(auditRecord.getMessage());
		vo.setUser(user.getUserId());
		vo.setTimestamp(auditRecord.getTimestamp().getTime());
		
		return vo;
	}

}
