/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.request.builder;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;

import java.util.Objects;

/**
 * Data structure class for passenger id flight id pairs.
 */
public class PassengerFlightTuple {
	private Passenger passenger;
	private Flight flight;

	public PassengerFlightTuple(Passenger passenger, Flight flight) {
		this.passenger = passenger;
		this.flight = flight;
	}

	/**
	 * @return the passenger
	 */
	public Passenger getPassenger() {
		return passenger;
	}

	/**
	 * @return the flight
	 */
	public Flight getFlight() {
		return flight;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.passenger.getId(), this.flight.getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PassengerFlightTuple))
			return false;
		final PassengerFlightTuple other = (PassengerFlightTuple) obj;
		return Objects.equals(this.passenger.getId(), other.passenger.getId())
				&& Objects.equals(this.flight.getId(), other.flight.getId());
	}

}
