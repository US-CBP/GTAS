/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "passenger_trip_details")
public class PassengerTripDetails extends BaseEntityAudit {

	public PassengerTripDetails() {
	}

	@Column(name = "ptd_id", columnDefinition = "bigint unsigned", updatable = false, insertable = false)
	private Long paxId;

	public PassengerTripDetails(Passenger p) {
		this.passenger = p;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "ptd_id", referencedColumnName = "id")
	Passenger passenger;

	/** calculated field */
	@Column(name = "days_visa_valid")
	private Integer numberOfDaysVisaValid;

	private String embarkation;

	private String debarkation;

	@Column(name = "embark_country")
	private String embarkCountry;

	@Column(name = "debark_country")
	private String debarkCountry;

	@Column(name = "ref_number")
	private String reservationReferenceNumber;

	@Column(name = "pnr_ref_number")
	private String pnrReservationReferenceNumber;

	@Column(name = "travel_frequency")
	private Integer travelFrequency = 0;

	@Formula("( select (case when ref_number is not null then (select count(distinct pax_trip.ptd_id) "
			+ " from passenger_trip_details pax_trip "
			+ " where pax_trip.ptd_id in "
			+ " (select flightPax2.passenger_id from flight_passenger flightPax2 "
			+ " where  flightPax2.flight_id = "
			+ " (select flightPax1.flight_id from flight_passenger flightPax1 where "
			+ " flightPax1.passenger_id = 8)) and pax_trip.ref_number = ref_number) - 1  else 0 end))")
	private Integer coTravelerCount;

	@Transient
	private String totalBagWeight;

	@Transient
	private String bagNum;

	public String getPnrReservationReferenceNumber() {
		return pnrReservationReferenceNumber;
	}

	public void setPnrReservationReferenceNumber(String pnrReservationReferenceNumber) {
		this.pnrReservationReferenceNumber = pnrReservationReferenceNumber;
	}

	public Integer getTravelFrequency() {
		return travelFrequency;
	}

	public void setTravelFrequency(Integer travelFrequency) {
		this.travelFrequency = travelFrequency;
	}

	public String getBagNum() {
		return bagNum;
	}

	public void setBagNum(String bagNum) {
		this.bagNum = bagNum;
	}

	public String getTotalBagWeight() {
		return totalBagWeight;
	}

	public void setTotalBagWeight(String totalBagWeight) {
		this.totalBagWeight = totalBagWeight;
	}

	public String getReservationReferenceNumber() {
		return reservationReferenceNumber;
	}

	public void setReservationReferenceNumber(String reservationReferenceNumber) {
		this.reservationReferenceNumber = reservationReferenceNumber;
	}

	public void addApisMessage(ApisMessage apisMessage) {
	}

	public String getEmbarkation() {
		return embarkation;
	}

	public void setEmbarkation(String embarkation) {
		this.embarkation = embarkation;
	}

	public String getDebarkation() {
		return debarkation;
	}

	public void setDebarkation(String debarkation) {
		this.debarkation = debarkation;
	}

	public String getEmbarkCountry() {
		return embarkCountry;
	}

	public void setEmbarkCountry(String embarkCountry) {
		this.embarkCountry = embarkCountry;
	}

	public String getDebarkCountry() {
		return debarkCountry;
	}

	public void setDebarkCountry(String debarkCountry) {
		this.debarkCountry = debarkCountry;
	}

	public Integer getNumberOfDaysVisaValid() {
		return numberOfDaysVisaValid;
	}

	public void setNumberOfDaysVisaValid(Integer numberOfDaysVisaValid) {
		this.numberOfDaysVisaValid = numberOfDaysVisaValid;
	}

	public void setPaxId(Long paxId) {
		this.paxId = paxId;
	}

	public Long getPaxId() {
		return this.paxId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PassengerTripDetails))
			return false;
		PassengerTripDetails that = (PassengerTripDetails) o;
		return getPaxId().equals(that.getPaxId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPaxId());
	}

	public Integer getCoTravelerCount() {
		return coTravelerCount;
	}

	public void setCoTravelerCount(Integer coTravelerCount) {
		this.coTravelerCount = coTravelerCount;
	}
}