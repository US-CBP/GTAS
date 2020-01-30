package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "flight_passenger_count")
public class FlightPassengerCount {

	@Id
	@Column(name = "fp_flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fp_flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	private Flight flight;

	@Column(name = "fp_count")
	private Integer passengerCount;

	public FlightPassengerCount(Long id, Integer passengerCount) {
		this.flightId = id;
		this.passengerCount = passengerCount;
	}

	@SuppressWarnings("unused")
	public FlightPassengerCount() {
	}

	public Integer getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(Integer passengerCount) {
		this.passengerCount = passengerCount;
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
