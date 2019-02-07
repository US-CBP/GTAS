/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;

import gov.gtas.model.lookup.DispositionStatus;

@Entity
@Table(name = "disposition")
public class Disposition extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    @Column(name = "paxId", columnDefinition = "bigint unsigned")
    private Long paxId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paxId", referencedColumnName = "id", updatable = false, insertable = false)
    private Passenger passenger;

    @Column(name = "flightId", columnDefinition = "bigint unsigned")
    private Long flightId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "flightId", referencedColumnName = "id", updatable = false, insertable = false)
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private DispositionStatus status;

    private String comments;  

    public Long getPaxId() {
        return paxId;
    }

    public void setPaxId(Long paxId) {
        this.paxId = paxId;
    }

    public Flight getFlight() {
        return flight;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
    public DispositionStatus getStatus() {
        return status;
    }

    public void setStatus(DispositionStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
}
