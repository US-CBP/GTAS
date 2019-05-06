package gov.gtas.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mutable_flight_details")
public class MutableFlightDetails {

    @SuppressWarnings("unused")
    public MutableFlightDetails() {
    }

    public MutableFlightDetails(Long id) {
        this.flightId = id;
    }

    @Id
    @Column(name = "flight_id", columnDefinition = "bigint unsigned")
    private
    Long flightId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", referencedColumnName = "id", updatable = false, insertable = false)
    private
    Flight flight;

    /**
     * calculated field
     */
    @Column(name = "full_etd_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date etd;

    @Column(name = "full_eta_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eta;

    @Column(name = "eta_date")
    @Temporal(TemporalType.DATE)
    private Date etaDate;


    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public Date getEtaDate() {
        return etaDate;
    }

    public Date getEtd() {
        return etd;
    }

    public void setEtd(Date etd) {
        this.etd = etd;
    }

    public void setEtaDate(Date etaDate) {
        this.etaDate = etaDate;
    }
}
