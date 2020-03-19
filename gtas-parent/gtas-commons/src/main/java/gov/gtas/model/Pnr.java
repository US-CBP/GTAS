/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "pnr")
public class Pnr extends Message {
	private static final long serialVersionUID = 1L;

	public Pnr() {
	}

	@Embedded
	private EdifactMessage edifactMessage;

	@Column(name = "record_locator", length = 20)
	private String recordLocator;

	private String carrier;

	private String origin;

	@Column(name = "origin_country", length = 3)
	private String originCountry;

	@Column(name = "date_booked")
	@Temporal(TemporalType.DATE)
	private Date dateBooked;

	@Column(name = "date_received")
	@Temporal(TemporalType.DATE)
	private Date dateReceived;

	@Column(name = "departure_date")
	@Temporal(TemporalType.DATE)
	private Date departureDate;

	@Column(name = "days_booked_before_travel")
	private Integer daysBookedBeforeTravel;

	@Column(name = "bag_count")
	private Integer bagCount;

	@Column(name = "baggage_weight")
	private Double baggageWeight;

	@Column(name = "baggage_unit")
	private String baggageUnit;

	@Column(name = "total_bag_count")
	private Integer total_bag_count;

	@Column(name = "total_bag_weight")
	private double total_bag_weight;

	@Column(name = "trip_type", length = 50)
	private String tripType;

	public double getTotal_bag_weight() {
		return total_bag_weight;
	}

	public void setTotal_bag_weight(double total_bag_weight) {
		this.total_bag_weight = total_bag_weight;
	}

	public Integer getBagCount() {
		return bagCount;
	}

	public void setBagCount(Integer bagCount) {
		this.bagCount = bagCount;
	}

	public Integer getTotal_bag_count() {
		return total_bag_count;
	}

	public void setTotal_bag_count(Integer total_bag_count) {
		this.total_bag_count = total_bag_count;
	}

	@Column(name = "form_of_payment")
	private String formOfPayment;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = Flight.class)
	@JoinTable(name = "pnr_flight", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "flight_id"))
	private Set<Flight> flights = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = Passenger.class)
	@JoinTable(name = "pnr_passenger", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "passenger_id"))
	private Set<Passenger> passengers = new HashSet<>();

	@ManyToMany(targetEntity = CreditCard.class, cascade = { CascadeType.ALL })
	@JoinTable(name = "pnr_credit_card", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "credit_card_id"))
	private Set<CreditCard> creditCards = new HashSet<>();

	@ManyToMany(targetEntity = FrequentFlyer.class, cascade = { CascadeType.ALL })
	@JoinTable(name = "pnr_frequent_flyer", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "ff_id"))
	private Set<FrequentFlyer> frequentFlyers = new HashSet<>();

	@ManyToMany(targetEntity = Address.class, cascade = { CascadeType.ALL })
	@JoinTable(name = "pnr_address", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
	private Set<Address> addresses = new HashSet<>();

	@ManyToMany(targetEntity = Phone.class, cascade = { CascadeType.ALL })
	@JoinTable(name = "pnr_phone", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "phone_id"))
	private Set<Phone> phones = new HashSet<>();

	@ManyToMany(targetEntity = Email.class, cascade = { CascadeType.ALL })
	@JoinTable(name = "pnr_email", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "email_id"))
	private Set<Email> emails = new HashSet<>();

	@ManyToMany(targetEntity = Agency.class, cascade = { CascadeType.ALL })
	@JoinTable(name = "pnr_agency", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "agency_id"))
	private Set<Agency> agencies = new HashSet<>();

	@ManyToMany(targetEntity = CodeShareFlight.class, cascade = { CascadeType.ALL })
	@JoinTable(name = "pnr_codeshares", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "codeshare_id"))
	private Set<CodeShareFlight> codeshares = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pnr")
	private List<PaymentForm> paymentForms = new ArrayList<>();

	@ManyToMany(targetEntity = DwellTime.class, cascade = { CascadeType.ALL })
	@JoinTable(name = "pnr_dwelltime", joinColumns = @JoinColumn(name = "pnr_id"), inverseJoinColumns = @JoinColumn(name = "dwell_id"))
	private Set<DwellTime> dwellTimes = new HashSet<>();

	@Column(name = "resrvation_create_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reservationCreateDate;

	@Column(name = "trip_duration")
	private Double tripDuration;

	public List<PaymentForm> getPaymentForms() {
		return paymentForms;
	}

	public void setPaymentForms(List<PaymentForm> paymentForms) {
		this.paymentForms = paymentForms;
	}

	public Set<CodeShareFlight> getCodeshares() {
		return codeshares;
	}

	public void setCodeshares(Set<CodeShareFlight> codeshares) {
		this.codeshares = codeshares;
	}

	public Double getTripDuration() {
		return tripDuration;
	}

	public void setTripDuration(Double duration) {
		this.tripDuration = duration;
	}

	public Date getReservationCreateDate() {
		return reservationCreateDate;
	}

	public void setReservationCreateDate(Date reservationCreateDate) {
		this.reservationCreateDate = reservationCreateDate;
	}

	public void addDwellTime(DwellTime dwellTime) {
		dwellTimes.add(dwellTime);
	}

	public Set<DwellTime> getDwellTimes() {
		return dwellTimes;
	}

	public void setDwellTimes(Set<DwellTime> dwellTimes) {
		this.dwellTimes = dwellTimes;
	}

	public void addPaymentForm(PaymentForm payment) {
		paymentForms.add(payment);
	}

	public void addFlight(Flight f) {
		if (this.flights == null) {
			this.flights = new HashSet<>();
		}
		this.flights.add(f);
	}

	public void addCreditCard(CreditCard cc) {
		if (this.creditCards == null) {
			this.creditCards = new HashSet<>();
		}
		this.creditCards.add(cc);
	}

	public void addFrequentFlyer(FrequentFlyer ff) {
		if (this.frequentFlyers == null) {
			this.frequentFlyers = new HashSet<>();
		}
		this.frequentFlyers.add(ff);
	}

	public void addAddress(Address address) {
		if (this.addresses == null) {
			this.addresses = new HashSet<>();
		}
		this.addresses.add(address);
	}

	public void addPhone(Phone phone) {
		if (this.phones == null) {
			this.phones = new HashSet<>();
		}
		this.phones.add(phone);
	}

	public void addEmail(Email email) {
		if (this.emails == null) {
			this.emails = new HashSet<>();
		}
		this.emails.add(email);
	}

	public void addAgency(Agency agency) {
		this.agencies.add(agency);
	}

	public EdifactMessage getEdifactMessage() {
		return edifactMessage;
	}

	public void setEdifactMessage(EdifactMessage edifactMessage) {
		this.edifactMessage = edifactMessage;
	}

	public Set<CreditCard> getCreditCards() {
		return creditCards;
	}

	public void setCreditCards(Set<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}

	public Set<FrequentFlyer> getFrequentFlyers() {
		return frequentFlyers;
	}

	public void setFrequentFlyers(Set<FrequentFlyer> frequentFlyers) {
		this.frequentFlyers = frequentFlyers;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	public Set<Phone> getPhones() {
		return phones;
	}

	public void setPhones(Set<Phone> phones) {
		this.phones = phones;
	}

	public Set<Email> getEmails() {
		return emails;
	}

	public void setEmails(Set<Email> emails) {
		this.emails = emails;
	}

	public String getRecordLocator() {
		return recordLocator;
	}

	public void setRecordLocator(String recordLocator) {
		this.recordLocator = recordLocator;
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

	public Date getDateBooked() {
		return dateBooked;
	}

	public void setDateBooked(Date dateBooked) {
		this.dateBooked = dateBooked;
	}

	public Date getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public Integer getDaysBookedBeforeTravel() {
		return daysBookedBeforeTravel;
	}

	public void setDaysBookedBeforeTravel(Integer daysBookedBeforeTravel) {
		this.daysBookedBeforeTravel = daysBookedBeforeTravel;
	}

	public String getFormOfPayment() {
		return formOfPayment;
	}

	public void setFormOfPayment(String formOfPayment) {
		this.formOfPayment = formOfPayment;
	}

	public Set<Flight> getFlights() {
		return flights;
	}

	public void setFlights(Set<Flight> flights) {
		this.flights = flights;
	}

	public Set<Agency> getAgencies() {
		return agencies;
	}

	public void setAgencies(Set<Agency> agencies) {
		this.agencies = agencies;
	}

	public Double getBaggageWeight() {
		return baggageWeight;
	}

	public void setBaggageWeight(Double bWeight) {
		this.baggageWeight = bWeight;
	}

	public String getBaggageUnit() {
		return baggageUnit;
	}

	public void setBaggageUnit(String baggageUnit) {
		this.baggageUnit = baggageUnit;
	}

	public String getTripType() {
		return tripType;
	}

	public void setTripType(String tripType) {
		this.tripType = tripType;
	}

	public void addPassenger(Passenger p) {
		if (this.passengers == null) {
			this.passengers = new HashSet<>();
		}
		this.passengers.add(p);
	}

	public Set<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(Set<Passenger> passengers) {
		this.passengers = passengers;
	}

}
