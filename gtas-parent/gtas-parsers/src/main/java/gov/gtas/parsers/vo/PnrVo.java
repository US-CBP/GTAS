/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.validators.Validatable;

public class PnrVo extends MessageVo implements Validatable {
    private String messageCode;
    
    private String recordLocator;
    private String carrier;
    private String origin;
    private String originCountry;
    private Date dateBooked;
    private Date dateReceived;
    private Date departureDate;
    private Integer passengerCount;
    private Integer bagCount;
    private Double baggageWeight;
    private String baggageUnit;
    private String formOfPayment;

    private Integer daysBookedBeforeTravel;
    
    private List<FlightVo> flights = new ArrayList<>();
    private List<PassengerVo> passengers = new ArrayList<>();
    private List<BagVo> bags = new ArrayList<>();
    private List<AddressVo> addresses = new ArrayList<>();
    private List<PhoneVo> phoneNumbers = new ArrayList<>();
    private List<CreditCardVo> creditCards = new ArrayList<>();
    private List<FrequentFlyerVo> frequentFlyerDetails = new ArrayList<>();
    private List<EmailVo> emails = new ArrayList<>();
    private List<AgencyVo> agencies = new ArrayList<>();
    private List<CodeShareVo> codeshares = new ArrayList<>();
    private Date reservationCreateDate;
    
    
    public List<CodeShareVo> getCodeshares() {
		return codeshares;
	}

	public void setCodeshares(List<CodeShareVo> codeshares) {
		this.codeshares = codeshares;
	}

	public List<BagVo> getBags() {
		return bags;
	}

	public void setBags(List<BagVo> bags) {
		this.bags = bags;
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

	public PnrVo() {
        this.bagCount = 0;
        this.passengerCount = 0;
    }
     
    public List<EmailVo> getEmails() {
        return emails;
    }

    public void setEmails(List<EmailVo> emails) {
        this.emails = emails;
    }

    public Integer getDaysBookedBeforeTravel() {
        return daysBookedBeforeTravel;
    }

    public void setDaysBookedBeforeTravel(Integer daysBookedBeforeTravel) {
        this.daysBookedBeforeTravel = daysBookedBeforeTravel;
    }
    
    public String getMessageCode() {
        return messageCode;
    }
    
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
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

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public Integer getBagCount() {
        return bagCount;
    }

    public void setBagCount(Integer bagCount) {
        this.bagCount = bagCount;
    }

    public String getFormOfPayment() {
        return formOfPayment;
    }

    public void setFormOfPayment(String formOfPayment) {
        this.formOfPayment = formOfPayment;
    }

    public List<FlightVo> getFlights() {
        return flights;
    }

    public void setFlights(List<FlightVo> flights) {
        this.flights = flights;
    }

    public List<PassengerVo> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerVo> passengers) {
        this.passengers = passengers;
    }

    public List<AgencyVo> getAgencies() {
        return agencies;
    }

    public void setAgencies(List<AgencyVo> agencies) {
        this.agencies = agencies;
    }

    public List<AddressVo> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressVo> addresses) {
        this.addresses = addresses;
    }

    public List<PhoneVo> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneVo> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<CreditCardVo> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCardVo> creditCards) {
        this.creditCards = creditCards;
    }

    public List<FrequentFlyerVo> getFrequentFlyerDetails() {
        return frequentFlyerDetails;
    }

    public void setFrequentFlyerDetails(List<FrequentFlyerVo> frequentFlyerDetails) {
        this.frequentFlyerDetails = frequentFlyerDetails;
    }

    public Date getReservationCreateDate() {
		return reservationCreateDate;
	}

	public void setReservationCreateDate(Date reservationCreateDate) {
		this.reservationCreateDate = reservationCreateDate;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean isValid() {
        if(StringUtils.isBlank(this.recordLocator) || StringUtils.isBlank(this.carrier)){
            return false;
        }
        return true;
    }

}
