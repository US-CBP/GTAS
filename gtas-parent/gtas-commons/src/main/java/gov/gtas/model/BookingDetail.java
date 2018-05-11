package gov.gtas.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "BookingDetail")
public class BookingDetail extends BaseEntityAudit {


	@Size(min = 4, max = 4)
	@Column(name = "flight_number", length = 4, nullable = false)
	private String flightNumber;

	/** calculated field */
	@Column(name = "etd_date")
	@Temporal(TemporalType.DATE)
	private Date etdDate;

	/** calculated field */
	@Column(name = "eta_date")
	@Temporal(TemporalType.DATE)
	private Date etaDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date etd;

	@Temporal(TemporalType.TIMESTAMP)
	private Date eta;

	@Column(nullable = false)
	private String origin;

	@Column(name = "origin_country", length = 3)
	private String originCountry;

	@Column(nullable = false)
	private String destination;

	@Column(name = "destination_country", length = 3)
	private String destinationCountry;

	@Column(name = "processed")
	private Boolean processed = Boolean.FALSE;
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy ="bookingDetail", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Set<FlightLeg> flightLegs;

    @ManyToMany(fetch=FetchType.EAGER, targetEntity = Passenger.class, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "pax_booking", joinColumns = @JoinColumn(name = "booking_detail_id"), inverseJoinColumns = @JoinColumn(name = "pax_id"))
    private Set<Passenger> passengers = new HashSet<>();
   
    @ManyToMany(fetch=FetchType.EAGER, targetEntity = Pnr.class, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "pnr_booking", joinColumns = @JoinColumn(name = "booking_detail_id"), inverseJoinColumns = @JoinColumn(name = "pnr_id"))     
    private Set<Pnr> pnrs = new HashSet<>();

	public Set<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(Set<Passenger> passengers) {
		this.passengers = passengers;
	}

	public Set<Pnr> getPnrs() {
		return pnrs;
	}

	public void setPnrs(Set<Pnr> pnrs) {
		this.pnrs = pnrs;
	}


	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public Date getEtdDate() {
		return etdDate;
	}

	public void setEtdDate(Date etdDate) {
		this.etdDate = etdDate;
	}

	public Date getEtaDate() {
		return etaDate;
	}

	public void setEtaDate(Date etaDate) {
		this.etaDate = etaDate;
	}

	public Date getEtd() {
		return etd;
	}

	public void setEtd(Date etd) {
		this.etd = etd;
	}

	public Date getEta() {
		return eta;
	}

	public void setEta(Date eta) {
		this.eta = eta;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getOriginCountry() {
		return originCountry;
	}

	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDestinationCountry() {
		return destinationCountry;
	}

	public void setDestinationCountry(String destinationCountry) {
		this.destinationCountry = destinationCountry;
	}

	public Boolean getProcessed() {
		return processed;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	public Set<FlightLeg> getFlightLegs() {
		return flightLegs;
	}

	public void setFlightLegs(Set<FlightLeg> flightLegs) {
		this.flightLegs = flightLegs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		BookingDetail that = (BookingDetail) o;

		if (!flightNumber.equals(that.flightNumber)) return false;
		if (!etdDate.equals(that.etdDate)) return false;
		if (!etaDate.equals(that.etaDate)) return false;
		if (!origin.equals(that.origin)) return false;
		return destination.equals(that.destination);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + flightNumber.hashCode();
		result = 31 * result + etdDate.hashCode();
		result = 31 * result + etaDate.hashCode();
		result = 31 * result + origin.hashCode();
		result = 31 * result + destination.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "BookingDetail{" +
				"flightNumber='" + flightNumber + '\'' +
				", etdDate=" + etdDate +
				", etaDate=" + etaDate +
				", etd=" + etd +
				", eta=" + eta +
				", origin='" + origin + '\'' +
				", originCountry='" + originCountry + '\'' +
				", destination='" + destination + '\'' +
				", destinationCountry='" + destinationCountry + '\'' +
				", processed=" + processed +
				", flightLegs=" + flightLegs +
				", passengers=" + passengers +
				", pnrs=" + pnrs +
				'}';
	}
}
