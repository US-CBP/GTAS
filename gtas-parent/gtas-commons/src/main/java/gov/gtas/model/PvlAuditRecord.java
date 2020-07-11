package gov.gtas.model;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue("PVL_Audit")
public class PvlAuditRecord extends AuditRecord{

    public PvlAuditRecord() {
    }
   public PvlAuditRecord(AuditActionType actionType, String target, Status status, String message, String data, User user,
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
}
