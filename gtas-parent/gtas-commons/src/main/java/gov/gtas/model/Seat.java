/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "seat", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "number", "apis", "passenger_id", "flight_id" }) })
public class Seat extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public Seat() {
	}

	@Column(nullable = false)
	private String number;

	@Column(name = "cabin_class")
	private String cabinClass;
	/** true if the seat number was derived from APIS data */
	@Column(nullable = false)
	private Boolean apis = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "passenger_id", nullable = false)
	private Passenger passenger;

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long paxId) {
		this.passengerId = paxId;
	}

	@Column(name = "passenger_id", updatable = false, insertable = false, columnDefinition = "bigint unsigned")
	private Long passengerId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight_id", referencedColumnName = "id", nullable = false)
	private Flight flight;

	@Column(name = "flight_id", updatable = false, insertable = false, columnDefinition = "bigint unsigned")
	private Long flightId;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Boolean getApis() {
		return apis;
	}

	public void setApis(Boolean apis) {
		this.apis = apis;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
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

	@Override
	public int hashCode() {
		String num = this.number == null ? null : this.number.toUpperCase();
		if (num != null) {
			num = num.replaceAll("\\\\", "");
		}
		return Objects.hash(num, this.apis, this.passengerId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Seat))
			return false;
		final Seat other = (Seat) obj;
		String num = this.number == null ? null : this.number.toUpperCase();
		if (num != null) {
			num = num.replaceAll("\\\\", "");
		}
		String num2 = other.getNumber() == null ? null : other.getNumber().toUpperCase();
		if (num2 != null) {
			num2 = num2.replaceAll("\\\\", "");
		}
		return Objects.equals(num, num2) && Objects.equals(this.apis, other.apis)
				&& Objects.equals(this.passengerId, other.passengerId);
	}
	public void setCabinClass(String cabinClass) {
		this.cabinClass = cabinClass;
	}

	public String getCabinClass() {
		return cabinClass;
	}
}
