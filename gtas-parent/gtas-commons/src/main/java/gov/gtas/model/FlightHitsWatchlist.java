package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "flight_hit_watchlist")
public class FlightHitsWatchlist {

	@Id
	@Column(name = "fhw_flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fhw_flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	Flight flight;

	@Column(name = "fhw_hit_count")
	private Integer hitCount;

	@SuppressWarnings("unused")
	public FlightHitsWatchlist() {
	}

	public FlightHitsWatchlist(Long flightId, Integer hitCount) {
		this.flightId = flightId;
		this.hitCount = hitCount;
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
