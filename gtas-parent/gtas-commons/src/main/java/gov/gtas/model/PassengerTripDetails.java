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

	private static final long serialVersionUID = 1980994941299623326L;
	public PassengerTripDetails() {
	}

	@Column(name = "ptd_id", columnDefinition = "bigint unsigned", updatable = false, insertable = false)
	private Long paxId;

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	@Column(name = "ptd_id", columnDefinition = "bigint unsigned", updatable = false, insertable = false)
	private Long passengerId;

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
	@JoinColumn(name = "ptd_id")
	Passenger passenger;

	/** calculated field */
	@Column(name = "days_visa_valid")
	private Integer numberOfDaysVisaValid;

	@Column(name = "embarkation")
	private String embarkation;

	@Column(name = "debarkation")
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

	@Column(name = "apis_co_traveler_count")
	private Integer coTravelerCount;

	@Column(name = "hours_before_takeoff")
	private Integer hoursBeforeTakeOff;

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

	public Integer getHoursBeforeTakeOff() {
		return hoursBeforeTakeOff;
	}

	public void setHoursBeforeTakeOff(Integer hoursBeforeTakeOff) {
		this.hoursBeforeTakeOff = hoursBeforeTakeOff;
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

		if (passengerId == null) {
			if (that.passengerId != null)
				return false;
		} else if (!passengerId.equals(that.getPassengerId())) {
			return false;
		}
		if (!paxId.equals(that.paxId))
			return false;

		if (numberOfDaysVisaValid != null ? !numberOfDaysVisaValid.equals(that.numberOfDaysVisaValid)
				: that.numberOfDaysVisaValid != null)
			return false;
		if (embarkation != null ? !embarkation.equals(that.embarkation) : that.embarkation != null)
			return false;
		if (debarkation != null ? !debarkation.equals(that.debarkation) : that.debarkation != null)
			return false;
		if (embarkCountry != null ? !embarkCountry.equals(that.embarkCountry) : that.embarkCountry != null)
			return false;
		if (debarkCountry != null ? !debarkCountry.equals(that.debarkCountry) : that.debarkCountry != null)
			return false;
		if (reservationReferenceNumber != null ? !reservationReferenceNumber.equals(that.reservationReferenceNumber)
				: that.reservationReferenceNumber != null)
			return false;
		if (pnrReservationReferenceNumber != null
				? !pnrReservationReferenceNumber.equals(that.pnrReservationReferenceNumber)
				: that.pnrReservationReferenceNumber != null)
			return false;
		if (travelFrequency != null ? !travelFrequency.equals(that.travelFrequency) : that.travelFrequency != null)
			return false;
		return coTravelerCount != null ? coTravelerCount.equals(that.coTravelerCount) : that.coTravelerCount == null;
	}

	@Override
	public int hashCode() {
		int result = 10;
		result = 31 * result + paxId.hashCode();
		result = 31 * result + passengerId.hashCode();
		result = 31 * result + (numberOfDaysVisaValid != null ? numberOfDaysVisaValid.hashCode() : 0);
		result = 31 * result + (embarkation != null ? embarkation.hashCode() : 0);
		result = 31 * result + (debarkation != null ? debarkation.hashCode() : 0);
		result = 31 * result + (embarkCountry != null ? embarkCountry.hashCode() : 0);
		result = 31 * result + (debarkCountry != null ? debarkCountry.hashCode() : 0);
		result = 31 * result + (reservationReferenceNumber != null ? reservationReferenceNumber.hashCode() : 0);
		result = 31 * result + (pnrReservationReferenceNumber != null ? pnrReservationReferenceNumber.hashCode() : 0);
		result = 31 * result + (travelFrequency != null ? travelFrequency.hashCode() : 0);
		result = 31 * result + (coTravelerCount != null ? coTravelerCount.hashCode() : 0);
		result = 31 * result + (hoursBeforeTakeOff != null ? hoursBeforeTakeOff.hashCode() : 0);
		return result;
	}

	public Integer getCoTravelerCount() {
		return coTravelerCount;
	}

	public void setCoTravelerCount(Integer coTravelerCount) {
		this.coTravelerCount = coTravelerCount;
	}
}