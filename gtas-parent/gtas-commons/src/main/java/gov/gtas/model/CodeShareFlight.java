package gov.gtas.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "code_share_flight")
public class CodeShareFlight implements Serializable {

	private static final long serialVersionUID = 1L;

	public CodeShareFlight() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	@Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")
	private Long id;

	@Column(name = "operating_flight_id")
	private Long operatingFlightId;

	@Column(name = "marketing_flight_number")
	private String marketingFlightNumber;

	@Column(name = "operating_flight_number")
	private String operatingFlightNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToMany(mappedBy = "codeshares", targetEntity = Pnr.class)
	private Set<Pnr> pnrs = new HashSet<>();

	public String getOperatingFlightNumber() {
		return operatingFlightNumber;
	}

	public void setOperatingFlightNumber(String operatingFlightNumber) {
		this.operatingFlightNumber = operatingFlightNumber;
	}

	public Set<Pnr> getPnrs() {
		return pnrs;
	}

	public void setPnrs(Set<Pnr> pnrs) {
		this.pnrs = pnrs;
	}

	public String getMarketingFlightNumber() {
		return marketingFlightNumber;
	}

	public void setMarketingFlightNumber(String flightNumber) {
		this.marketingFlightNumber = flightNumber;
	}

	public Long getOperatingFlightId() {
		return operatingFlightId;
	}

	public void setOperatingFlightId(Long fid) {
		this.operatingFlightId = fid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getMarketingFlightNumber());
	}

	@Override
	public boolean equals(Object target) {

		if (this == target) {
			return true;
		}

		if (!(target instanceof CodeShareFlight)) {
			return false;
		}

		CodeShareFlight dataTarget = ((CodeShareFlight) target);

		return (this.getMarketingFlightNumber().equals(dataTarget.getMarketingFlightNumber()));
	}
}
