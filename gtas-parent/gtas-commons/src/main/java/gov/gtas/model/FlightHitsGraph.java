/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "flight_hit_graph")
public class FlightHitsGraph {
	@Id
	@Column(name = "fhg_flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fhg_flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	Flight flight;

	@Column(name = "fhg_hit_count")
	private Integer hitCount;

	@SuppressWarnings("unused")
	public FlightHitsGraph() {
	}

	public FlightHitsGraph(Long flightId, Integer hitCount) {
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
