package gov.gtas.model;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue("Passenger_Audit")
public class PassengerAuditRecord extends AuditRecord{

    @ManyToOne
    @JoinColumn(name = "passenger_id", referencedColumnName = "id", nullable = true,insertable = false, updatable = false)
    private Passenger passenger;

    @Column(name = "passenger_id", nullable = true, columnDefinition = "bigint unsigned")
    private Long passengerId;


    public PassengerAuditRecord() {
    }
   public PassengerAuditRecord(AuditActionType actionType, String target, Status status, String message, String data, User user,
                               Date timestamp, Long passengerId ) {
        super();
        this.actionType = actionType;
        this.target = target;
        this.actionStatus = status;
        this.message = message;
        this.actionData = data;
        this.user = user;
        this.timestamp = timestamp;
        this.passengerId = passengerId;
    }
    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
}
