/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "frequent_flyer")
public class FrequentFlyer extends BaseEntityAudit implements PIIObject{
	private static final long serialVersionUID = 1L;

	public FrequentFlyer() {
	}

	@Column(nullable = false)
	private String carrier;

	@Column(nullable = false)
	private String number;

	@ManyToMany(mappedBy = "frequentFlyers", targetEntity = Pnr.class)
	private Set<Pnr> pnrs = new HashSet<>();


	@Column(name = "flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	private Flight flight;

		public Set<Pnr> getPnrs() {
		return pnrs;
	}

	public void setPnrs(Set<Pnr> pnrs) {
		this.pnrs = pnrs;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.number, this.carrier);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FrequentFlyer other = (FrequentFlyer) obj;
		return Objects.equals(this.number, other.number) && Objects.equals(this.carrier, other.carrier);
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}
	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	@Override
	public PIIObject deletePII() {
		this.number = "DELETED";
		return this;
	}
}
