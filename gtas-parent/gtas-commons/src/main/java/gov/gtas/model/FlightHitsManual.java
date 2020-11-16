package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "flight_hit_manual")
public class FlightHitsManual {
    @Id
    @Column(name = "fhm_flight_id", columnDefinition = "bigint unsigned")
    private Long flightId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fhm_flight_id", referencedColumnName = "id", updatable = false, insertable = false)
    Flight flight;

    @Column(name = "fhm_hit_count")
    private Integer hitCount;

    @SuppressWarnings("unused")
    public FlightHitsManual() {
    }

    public FlightHitsManual(Long flightId, Integer hitCount) {
        this.hitCount = hitCount;
        this.flightId = flightId;
    }

    public Integer getHitCount() {
        return hitCount;
    }

    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
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
