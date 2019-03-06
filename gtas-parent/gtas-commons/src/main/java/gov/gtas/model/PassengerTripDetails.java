/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "passenger_trip_details")
public class PassengerTripDetails {
    private static final long serialVersionUID = 1L;
    
    public PassengerTripDetails() {
    }

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="passenger_id")
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
 
    @Column(name = "travel_frequency")
    private Integer travelFrequency=0;
    
    @Transient
    private String totalBagWeight;
 
    @Transient
    private String bagNum;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		//int result = super.hashCode();
		int result = 10;
		result = prime * result + ((bagNum == null) ? 0 : bagNum.hashCode());
		result = prime * result + ((debarkCountry == null) ? 0 : debarkCountry.hashCode());
		result = prime * result + ((debarkation == null) ? 0 : debarkation.hashCode());
		result = prime * result + ((embarkCountry == null) ? 0 : embarkCountry.hashCode());
		result = prime * result + ((embarkation == null) ? 0 : embarkation.hashCode());
		result = prime * result + ((numberOfDaysVisaValid == null) ? 0 : numberOfDaysVisaValid.hashCode());
		result = prime * result + ((passenger == null) ? 0 : passenger.hashCode());
		result = prime * result + ((reservationReferenceNumber == null) ? 0 : reservationReferenceNumber.hashCode());
		result = prime * result + ((totalBagWeight == null) ? 0 : totalBagWeight.hashCode());
		result = prime * result + ((travelFrequency == null) ? 0 : travelFrequency.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PassengerTripDetails other = (PassengerTripDetails) obj;
		if (bagNum == null) {
			if (other.bagNum != null)
				return false;
		} else if (!bagNum.equals(other.bagNum))
			return false;
		if (debarkCountry == null) {
			if (other.debarkCountry != null)
				return false;
		} else if (!debarkCountry.equals(other.debarkCountry))
			return false;
		if (debarkation == null) {
			if (other.debarkation != null)
				return false;
		} else if (!debarkation.equals(other.debarkation))
			return false;
		if (embarkCountry == null) {
			if (other.embarkCountry != null)
				return false;
		} else if (!embarkCountry.equals(other.embarkCountry))
			return false;
		if (embarkation == null) {
			if (other.embarkation != null)
				return false;
		} else if (!embarkation.equals(other.embarkation))
			return false;
		if (numberOfDaysVisaValid == null) {
			if (other.numberOfDaysVisaValid != null)
				return false;
		} else if (!numberOfDaysVisaValid.equals(other.numberOfDaysVisaValid))
			return false;
		if (passenger == null) {
			if (other.passenger != null)
				return false;
		} else if (!passenger.equals(other.passenger))
			return false;
		if (reservationReferenceNumber == null) {
			if (other.reservationReferenceNumber != null)
				return false;
		} else if (!reservationReferenceNumber.equals(other.reservationReferenceNumber))
			return false;
		if (totalBagWeight == null) {
			if (other.totalBagWeight != null)
				return false;
		} else if (!totalBagWeight.equals(other.totalBagWeight))
			return false;
		if (travelFrequency == null) {
			if (other.travelFrequency != null)
				return false;
		} else if (!travelFrequency.equals(other.travelFrequency))
			return false;
		return true;
	}

	
   
}