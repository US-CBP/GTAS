package gov.gtas.model;

import java.io.Serializable;
import java.util.Objects;

public class FlightPassengerId implements Serializable {
	private Long flightId;
	private Long passengerId;

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FlightPassengerId))
			return false;
		FlightPassengerId that = (FlightPassengerId) o;
		return getFlightId().equals(that.getFlightId()) && getPassengerId().equals(that.getPassengerId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFlightId(), getPassengerId());
	}
}
