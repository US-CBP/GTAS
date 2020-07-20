/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Cacheable
@Entity
@Table(name = "flight", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "carrier", "flight_number", "etd_date", "origin", "destination" }),
		@UniqueConstraint(columnNames = {"id_tag"})}
		)
public class Flight extends BaseEntityAudit {
	private static final long serialVersionUID = 1L;

	public Flight() {
	}

	@Column(nullable = false)
	private String carrier;

	@Size(min = 4, max = 4)
	@Column(name = "flight_number", length = 4, nullable = false)
	private String flightNumber;

	/** combination of carrier and flight number used for reporting */
	@Column(name = "full_flight_number")
	private String fullFlightNumber;

	@Column(nullable = false)
	private String origin;

	@Column(name = "origin_country", length = 3)
	private String originCountry;

	@Column(nullable = false)
	private String destination;

	@Column(name = "destination_country", length = 3)
	private String destinationCountry;

	/**
	 * Application will strip the timestamp off and use this when making a flight.
	 * This is the date (**not** the time) that the flight will take place. THIS
	 * VALUE IS DERIVED FROM LOCAL TIME AND IS NOT IN UTC - E.G. A FLIGHT LANDING IN
	 * TZ UTC + 4 at 12:01AM 1/2 WILL BE SET TO 1/2 INSTEAD OF THE UTC DATE OF 1/1.
	 */
	@Column(name = "etd_date")
	@Temporal(TemporalType.DATE)
	private Date etdDate;

	@Column(length = 1, nullable = false)
	private String direction;

	@Column(name = "id_tag")
	private String idTag;

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<Phone> phone;

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<Email> emails;

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<FrequentFlyer> frequentFlyers;

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<Address> address;

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<CreditCard> creditCard;

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<HitsSummary> hits = new HashSet<>();

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<Agency> agencies = new HashSet<>();

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<Document> documents = new HashSet<>();

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<Bag> bags = new HashSet<>();

	@OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
	@JoinColumn(name = "id", unique = true, referencedColumnName = "fhr_flight_id", updatable = false, insertable = false)
	@JsonIgnore
	private FlightHitsRule flightHitsRule;

	@OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
	@JoinColumn(name = "id", unique = true, referencedColumnName = "fhf_flight_id", updatable = false, insertable = false)
	@JsonIgnore
	private FlightHitsFuzzy flightHitsFuzzy;

	@OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
	@JoinColumn(name = "id", unique = true, referencedColumnName = "fhg_flight_id", updatable = false, insertable = false)
	@JsonIgnore
	private FlightHitsGraph flightHitsGraph;

	@OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
	@JoinColumn(name = "id", unique = true, referencedColumnName = "fhw_flight_id", updatable = false, insertable = false)
	@JsonIgnore
	private FlightHitsWatchlist flightHitsWatchlist;

	@OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
	@JoinColumn(name = "id", unique = true, referencedColumnName = "fhe_flight_id", updatable = false, insertable = false)
	@JsonIgnore
	private FlightHitsExternal flightHitsExternal;

	@OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
	@JoinColumn(name = "id", unique = true, referencedColumnName = "fp_flight_id", updatable = false, insertable = false)
	@JsonIgnore
	private FlightPassengerCount flightPassengerCount;

	@OneToOne(mappedBy = "flight") // Mutable flight details are separated as a concurrency concern and therefore
									// are EAGER fetched.
	@JoinColumn(name = "id", unique = true, referencedColumnName = "flight_id", updatable = false, insertable = false)
	@JsonIgnore
	private MutableFlightDetails mutableFlightDetails;

	@OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
	@JoinColumn(name = "id", unique = true, referencedColumnName = "fcdv_flight_id", updatable = false, insertable = false)
	@JsonIgnore
	private FlightCountDownView flightCountDownView;

	@ManyToMany(mappedBy = "flights", targetEntity = Pnr.class)
	private Set<Pnr> pnrs = new HashSet<>();

	@ManyToMany(mappedBy = "flights", targetEntity = ApisMessage.class)
	private Set<ApisMessage> apis = new HashSet<>();

	@OneToMany(mappedBy = "flight", targetEntity = FlightAuditRecord.class)
	private Set<FlightAuditRecord> flightAuditRecords = new HashSet<>();

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<BookingDetail> bookingDetails = new HashSet<>();

	// This is a convenience method to see the passengers associated with the
	// flight.
	// Managing passengers this way is recommended against as flight passenger is
	// manually made in the
	// loader.
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "flight_passenger", joinColumns = { @JoinColumn(name = "flight_id") }, inverseJoinColumns = {
			@JoinColumn(name = "passenger_id") })
	@JsonIgnore
	private Set<Passenger> passengers;

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
	private Set<HitDetail> hitDetails;

	/*
	 * Used to keep a referenced to FlightVO from parser. Only used in loader to
	 * help establish relationships.
	 */
	@Transient
	private UUID parserUUID;

	public FlightCountDownView getFlightCountDownView() {
		return flightCountDownView;
	}

	public void setFlightCountDownView(FlightCountDownView flightCountDownView) {
		this.flightCountDownView = flightCountDownView;
	}

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

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getFullFlightNumber() {
		return fullFlightNumber;
	}

	public void setFullFlightNumber(String fullFlightNumber) {
		this.fullFlightNumber = fullFlightNumber;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getIdTag() {
		return idTag;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
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

	public Set<Pnr> getPnrs() {
		return pnrs;
	}

	public void setPnrs(Set<Pnr> pnrs) {
		this.pnrs = pnrs;
	}

	public Set<ApisMessage> getApis() {
		return apis;
	}

	public void setApis(Set<ApisMessage> apis) {
		this.apis = apis;
	}

	public Set<BookingDetail> getBookingDetails() {
		return bookingDetails;
	}

	public void setBookingDetails(Set<BookingDetail> bookingDetails) {
		this.bookingDetails = bookingDetails;
	}

	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.carrier, this.flightNumber, this.etdDate, this.origin, this.destination);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Flight))
			return false;
		final Flight other = (Flight) obj;
		return Objects.equals(this.carrier, other.carrier) && Objects.equals(this.flightNumber, other.flightNumber)
				&& Objects.equals(this.etdDate, other.etdDate) && Objects.equals(this.origin, other.origin)
				&& Objects.equals(this.destination, other.destination);
	}

	public FlightHitsRule getFlightHitsRule() {
		return flightHitsRule;
	}

	public FlightHitsWatchlist getFlightHitsWatchlist() {
		return flightHitsWatchlist;
	}

	public void setFlightHitsRule(FlightHitsRule flightHitsRule) {
		this.flightHitsRule = flightHitsRule;
	}

	public void setFlightHitsWatchlist(FlightHitsWatchlist flightHitsWatchlist) {
		this.flightHitsWatchlist = flightHitsWatchlist;
	}

	public FlightPassengerCount getFlightPassengerCount() {
		return flightPassengerCount;
	}

	public void setFlightPassengerCount(FlightPassengerCount flightPassengerCount) {
		this.flightPassengerCount = flightPassengerCount;
	}

	public MutableFlightDetails getMutableFlightDetails() {
		return mutableFlightDetails;
	}

	public void setMutableFlightDetails(MutableFlightDetails mutableFlightDetails) {
		this.mutableFlightDetails = mutableFlightDetails;
	}

	public Date getEtdDate() {
		return etdDate;
	}

	public void setEtdDate(Date etdDate) {
		this.etdDate = etdDate;
	}

	public Set<Phone> getPhone() {
		return phone;
	}

	public void setPhone(Set<Phone> phone) {
		this.phone = phone;
	}

	public Set<Address> getAddress() {
		return address;
	}

	public void setAddress(Set<Address> address) {
		this.address = address;
	}

	public Set<CreditCard> getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(Set<CreditCard> creditCard) {
		this.creditCard = creditCard;
	}

	public Set<Bag> getBags() {
		return bags;
	}

	public void setBags(Set<Bag> bags) {
		this.bags = bags;
	}

	public FlightHitsFuzzy getFlightHitsFuzzy() {
		return flightHitsFuzzy;
	}

	public void setFlightHitsFuzzy(FlightHitsFuzzy flightHitsFuzzy) {
		this.flightHitsFuzzy = flightHitsFuzzy;
	}

	public FlightHitsGraph getFlightHitsGraph() {
		return flightHitsGraph;
	}

	public void setFlightHitsGraph(FlightHitsGraph flightHitsGraph) {
		this.flightHitsGraph = flightHitsGraph;
	}

	public Set<HitDetail> getHitDetails() {
		return hitDetails;
	}

	public void setHitDetails(Set<HitDetail> hitDetails) {
		this.hitDetails = hitDetails;
	}

	public Set<Email> getEmails() {
		return emails;
	}

	public void setEmails(Set<Email> emails) {
		this.emails = emails;
	}

	public Set<FrequentFlyer> getFrequentFlyers() {
		return frequentFlyers;
	}

	public void setFrequentFlyers(Set<FrequentFlyer> frequentFlyers) {
		this.frequentFlyers = frequentFlyers;
	}

	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}

	public Set<Agency> getAgencies() {
		return agencies;
	}

	public void setAgencies(Set<Agency> agencies) {
		this.agencies = agencies;
	}

	public FlightHitsExternal getFlightHitsExternal() {
		return flightHitsExternal;
	}

	public void setFlightHitsExternal(FlightHitsExternal flightHitsExternal) {
		this.flightHitsExternal = flightHitsExternal;
	}

	public Set<FlightAuditRecord> getFlightAuditRecords() {
		return flightAuditRecords;
	}

	public void setFlightAuditRecords(Set<FlightAuditRecord> flightAuditRecords) {
		this.flightAuditRecords = flightAuditRecords;
	}
}
