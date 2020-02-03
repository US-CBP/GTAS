package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "flight_hit_rule")
public class FlightHitsRule {

	@Id
	@Column(name = "fhr_flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fhr_flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	Flight flight;

	@Column(name = "fhr_hit_count")
	private Integer hitCount;

	@SuppressWarnings("unused")
	public FlightHitsRule() {
	}

	public FlightHitsRule(Long flightId, Integer hitCount) {
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
