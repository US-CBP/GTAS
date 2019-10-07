package gov.gtas.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "booking_detail")
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

	// IS NOT UTC TIME. IT WILL BE STORED IN THE DATABASE AS UTC
	// BUT IS ACTUALLY WHATEVER AIRPORT THE TIME CAME FROM
	// TO SEE ETD LOOK AT ETD
	@Column(name = "local_etd")
	@Temporal(TemporalType.TIMESTAMP)
	private Date localEtdDate;

	// IS NOT UTC TIME. IT WILL BE STORED IN THE DATABASE AS UTC
	// BUT IS ACTUALLY WHATEVER AIRPORT THE TIME CAME FROM
	// TO SEE ETA TIME LOOK AT ETA
	@Column(name = "local_eta")
	@Temporal(TemporalType.TIMESTAMP)
	private Date localEtaDate;

	@Column(name = "etd")
	@Temporal(TemporalType.TIMESTAMP)
	private Date etd;

	public void setEta(Date eta) {
		this.eta = eta;
	}

	@Column(name = "eta")
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

	@OneToMany(mappedBy = "bookingDetail")
	private Set<FlightLeg> flightLegs;

	@ManyToMany(mappedBy = "bookingDetails", targetEntity = Passenger.class)
	private Set<Passenger> passengers = new HashSet<>();

	@ManyToMany(mappedBy = "bookingDetails", targetEntity = Message.class)
	private Set<Message> messages = new HashSet<>();

	@Column(name = "full_flight_number")
	private String fullFlightNumber;

	@ManyToOne(optional = false)
	@JoinColumn(name = "flight", updatable = false, insertable = false)
	private Flight flight;

	@Column(name = "flight", columnDefinition = "bigint unsigned")
	private Long flightId;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = Bag.class, mappedBy = "bookingDetail")
	private Set<Bag> bags = new HashSet<>();

	/*
	 *
	 * Used to keep a referenced to FlightVO from parser. Only used in loader to
	 * help establish relationships.
	 */
	@Transient
	private UUID parserUUID;

	public UUID getParserUUID() {
		return parserUUID;
	}

	public void setParserUUID(UUID parserUUID) {
		this.parserUUID = parserUUID;
	}

	public Set<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(Set<Passenger> passengers) {
		this.passengers = passengers;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> pnrs) {
		this.messages = pnrs;
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

	public Date getLocalEtdDate() {
		return localEtdDate;
	}

	public void setLocalEtdDate(Date localEtdDate) {
		this.localEtdDate = localEtdDate;
	}

	public Date getLocalEtaDate() {
		return localEtaDate;
	}

	public void setLocalEtaDate(Date localEtaDate) {
		this.localEtaDate = localEtaDate;
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

	public Set<FlightLeg> getFlightLegs() {
		return flightLegs;
	}

	public void setFlightLegs(Set<FlightLeg> flightLegs) {
		this.flightLegs = flightLegs;
	}

	public String getFullFlightNumber() {
		return fullFlightNumber;
	}

	public void setFullFlightNumber(String fullFlightNumber) {
		this.fullFlightNumber = fullFlightNumber;
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

	public Set<Bag> getBags() {
		return bags;
	}

	public void setBags(Set<Bag> bags) {
		this.bags = bags;
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		BookingDetail that = (BookingDetail) o;

		if (!flightNumber.equals(that.flightNumber))
			return false;
		if (!etdDate.equals(that.etdDate))
			return false;
		if (!etaDate.equals(that.etaDate))
			return false;
		if (!origin.equals(that.origin))
			return false;
		return destination.equals(that.destination);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + flightNumber.hashCode();
		result = 31 * result + etdDate.hashCode();
		result = 31 * result + etaDate.hashCode();
		result = 31 * result + origin.hashCode();
		result = 31 * result + destination.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "BookingDetail{" + "flightNumber='" + flightNumber + '\'' + ", etdDate=" + etdDate + ", etaDate="
				+ etaDate + ", localEtd=" + localEtdDate + ", localEta=" + localEtaDate + ", etd=" + etd + ", eta="
				+ eta + ", origin='" + origin + '\'' + ", originCountry='" + originCountry + '\'' + ", destination='"
				+ destination + '\'' + ", destinationCountry='" + destinationCountry + '\'' + ", flightLegs="
				+ flightLegs + ", passengers=" + passengers + ", pnrs=" + messages + '}';
	}
}
