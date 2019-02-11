/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "seat", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"number", "apis", "passenger_id", "flight_id" }) })
public class Seat extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public Seat() {
	}

	@Column(nullable = false)
	private String number;

	/** true if the seat number was derived from APIS data */
	@Column(nullable = false)
	private Boolean apis = Boolean.valueOf(false);

	@ManyToOne
	@JoinColumn(nullable = false)
	private Passenger passenger;

	@ManyToOne
	@JoinColumn(name = "flight_id", referencedColumnName = "id", nullable = false)
	private Flight flight;

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

	@Override
	public int hashCode() {
		return Objects
				.hash(this.number, this.apis, this.passenger, this.flight);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Seat))
			return false;
		final Seat other = (Seat) obj;
		return Objects.equals(this.number, other.number)
				&& Objects.equals(this.apis, other.apis)
				&& Objects.equals(this.passenger, other.passenger)
				&& Objects.equals(this.flight, other.flight);
	}
}
