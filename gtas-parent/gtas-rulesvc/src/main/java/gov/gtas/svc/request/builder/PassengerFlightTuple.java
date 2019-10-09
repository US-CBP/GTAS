/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.request.builder;

import gov.gtas.model.Flight;
import gov.gtas.model.MutableFlightDetails;
import gov.gtas.model.Passenger;

import java.util.Objects;

/**
 * Data structure class for passenger id flight id pairs.
 */
public class PassengerFlightTuple {
	private Passenger passenger;
	private Flight flight;
	private MutableFlightDetails mutableFlightDetails;

	public PassengerFlightTuple(Passenger passenger, Flight flight) {
		this.passenger = passenger;
		this.flight = flight;
		this.mutableFlightDetails = flight.getMutableFlightDetails();
	}

	public MutableFlightDetails getMutableFlightDetails() {
		return mutableFlightDetails;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public Flight getFlight() {
		return flight;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.passenger.getId(), this.flight.getId(), this.mutableFlightDetails.getFlightId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PassengerFlightTuple))
			return false;
		final PassengerFlightTuple other = (PassengerFlightTuple) obj;
		return Objects.equals(this.passenger.getId(), other.passenger.getId())
				&& Objects.equals(this.flight.getId(), other.flight.getId())
				&& Objects.equals(this.mutableFlightDetails.getFlightId(), other.mutableFlightDetails.getFlightId());
	}

}
