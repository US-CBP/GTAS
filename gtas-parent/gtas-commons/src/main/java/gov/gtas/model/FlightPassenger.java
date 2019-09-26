/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "flight_passenger")
@IdClass(FlightPassengerId.class)
public class FlightPassenger {

	public FlightPassenger() {
	}

	@Id
	@Column(name = "flight_id", nullable = false, columnDefinition = "bigint unsigned")
	private Long flightId;

	@Id
	@Column(name = "passenger_id", nullable = false, columnDefinition = "bigint unsigned")
	private Long passengerId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	Flight flight;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "passenger_id", referencedColumnName = "id", updatable = false, insertable = false)
	Passenger passenger;

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FlightPassenger))
			return false;
		FlightPassenger that = (FlightPassenger) o;
		return getFlightId().equals(that.getFlightId()) && getPassengerId().equals(that.getPassengerId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFlightId(), getPassengerId());
	}
}
