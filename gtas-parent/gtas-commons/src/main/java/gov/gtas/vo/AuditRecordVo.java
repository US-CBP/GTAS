/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

import gov.gtas.model.AuditRecord;
import gov.gtas.model.User;
import gov.gtas.util.DateCalendarUtils;

public class AuditRecordVo {
    private String actionType;
    private String status;
    private String message;
    private String user;
    private String userName;
    private String timestamp;
    private String target;
    private String actionData;
    
    public AuditRecordVo(){}
    
    public AuditRecordVo(AuditRecord auditRecord){
        this.actionType = auditRecord.getActionType().toString();
        this.status = auditRecord.getActionStatus().toString();
        this.message = auditRecord.getMessage();
        final User usr = auditRecord.getUser();
        this.user = usr.getUserId();
        this.userName = usr.getFirstName()+" "+usr.getLastName();
        this.timestamp = DateCalendarUtils.formatRuleEngineDateTime(auditRecord.getTimestamp());
        this.target = auditRecord.getTarget();
        this.actionData = auditRecord.getActionData();
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the actionData
     */
    public String getActionData() {
        return actionData;
    }

    /**
     * @param actionData the actionData to set
     */
    public void setActionData(String actionData) {
        this.actionData = actionData;
    }
    
}
