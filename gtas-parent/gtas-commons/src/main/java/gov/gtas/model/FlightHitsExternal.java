package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "flight_hit_external")
public class FlightHitsExternal {
    @Id
    @Column(name = "fhe_flight_id", columnDefinition = "bigint unsigned")
    private Long flightId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fhe_flight_id", referencedColumnName = "id", updatable = false, insertable = false)
    Flight flight;

    @Column(name = "fhe_hit_count")
    private Integer hitCount;

    @SuppressWarnings("unused")
    public FlightHitsExternal() {
    }

    public FlightHitsExternal(Long flightId, Integer hitCount) {
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
