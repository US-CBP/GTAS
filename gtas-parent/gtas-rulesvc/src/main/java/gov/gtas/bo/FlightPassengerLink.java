/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.bo;

import java.io.Serializable;

public class FlightPassengerLink implements Serializable {
	private static final long serialVersionUID = -743254734221L;

	private long flightId;
	private long passengerId;

	public FlightPassengerLink(final long flightId, final long passengerId) {
		this.flightId = flightId;
		this.passengerId = passengerId;
	}

	public long getFlightId() {
		return flightId;
	}

	public void setFlightId(long flightId) {
		this.flightId = flightId;
	}

	public long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(long passengerId) {
		this.passengerId = passengerId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		FlightPassengerLink that = (FlightPassengerLink) o;

		if (flightId != that.flightId)
			return false;
		return passengerId == that.passengerId;
	}

	@Override
	public int hashCode() {
		int result = (int) (flightId ^ (flightId >>> 32));
		result = 31 * result + (int) (passengerId ^ (passengerId >>> 32));
		return result;
	}
}
