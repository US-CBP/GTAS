package gov.gtas.model;


import gov.gtas.enumtype.RetentionPolicyAction;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "passenger_details_retention_policy_audit")
public class PassengerDetailRetentionPolicyAudit extends BaseEntityRetention {
    @ManyToOne(optional = false)
    @JoinColumn(name = "pdrpa_doc_id", referencedColumnName = "id")
    private Passenger passenger;

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
}
