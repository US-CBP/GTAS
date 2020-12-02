package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "flight_priority_count")
public class FlightPriorityCount {

    @Id
    @Column(name = "prio_flight_id", columnDefinition = "bigint unsigned")
    private Long flightId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "prio_flight_id", referencedColumnName = "id", updatable = false, insertable = false)
    Flight flight;

    @Column(name = "high_prio_count")
    private int highPriorityCount;

    @Column(name = "med_prio_count")
    private int medPriorityCount;

    @Column(name = "low_prio_count")
    private int lowPriorityCount;

    @SuppressWarnings("unused")
    public FlightPriorityCount() {
    }

    public FlightPriorityCount(Long flightId, int highPriorityCount, int medPriorityCount, int lowPriorityCount){
        this.flightId = flightId;
        this.highPriorityCount = highPriorityCount;
        this.medPriorityCount = medPriorityCount;
        this.lowPriorityCount = lowPriorityCount;
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

    public int getHighPriorityCount() { return highPriorityCount; }

    public void setHighPriorityCount(int highPriorityCount) { this.highPriorityCount = highPriorityCount; }

    public int getMedPriorityCount() { return medPriorityCount; }

    public void setMedPriorityCount(int medPriorityCount) { this.medPriorityCount = medPriorityCount; }

    public int getLowPriorityCount() { return lowPriorityCount; }

    public void setLowPriorityCount(int lowPriorityCount) { this.lowPriorityCount = lowPriorityCount; }

}
