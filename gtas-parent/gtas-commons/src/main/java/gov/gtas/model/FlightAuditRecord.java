package gov.gtas.model;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue("Flight_Audit")
public class FlightAuditRecord extends AuditRecord{

    @ManyToOne
    @JoinColumn(name = "flight_id", referencedColumnName = "id", nullable = true,insertable = false, updatable = false)
    private Flight flight;


    @Column(name = "flight_id", nullable = true, columnDefinition = "bigint unsigned")
    private Long flightId;


    public FlightAuditRecord() {
    }
   public FlightAuditRecord(AuditActionType actionType, String target, Status status, String message, String data, User user,
                            Date timestamp, Long flightId ) {
        super();
        this.actionType = actionType;
        this.target = target;
        this.actionStatus = status;
        this.message = message;
        this.actionData = data;
        this.user = user;
        this.timestamp = timestamp;
        this.flightId = flightId;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
}
