package gov.gtas.model;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@DiscriminatorValue("General_Audit")
public class GeneralAuditRecord extends  AuditRecord{

    /**
     * Instantiates a new audit record.
     *
     * @param actionType
     *            the action type
     * @param target
     *            the target
     * @param status
     *            the status
     * @param message
     *            the message
     * @param data
     *            the data
     * @param user
     *            the user
     */
    public GeneralAuditRecord(AuditActionType actionType, String target, Status status, String message, String data,
                       User user) {
        super();
        this.actionType = actionType;
        this.target = target;
        this.actionStatus = status;
        this.message = message;
        this.actionData = data;
        this.user = user;
        this.timestamp = new Date();
    }

    /**
     * Instantiates a new audit record.
     *
     * @param actionType
     *            the action type
     * @param target
     *            the target
     * @param status
     *            the status
     * @param message
     *            the message
     * @param data
     *            the data
     * @param user
     *            the user
     * @param timestamp
     *            the timestamp
     */
    public GeneralAuditRecord(AuditActionType actionType, String target, Status status, String message, String data, User user,
                       Date timestamp) {
        super();
        this.actionType = actionType;
        this.target = target;
        this.actionStatus = status;
        this.message = message;
        this.actionData = data;
        this.user = user;
        this.timestamp = timestamp;
    }
    public GeneralAuditRecord(AuditActionType actionType, String target, String message, User user) {
        this(actionType, target, Status.SUCCESS, message, null, user);
    }

    public GeneralAuditRecord(AuditActionType actionType, String target, User user) {
        this(actionType, target, Status.SUCCESS, null, null, user);
    }
    public GeneralAuditRecord() {
        super();
    }
}
