/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.vo.MessageVo;

public class PnrVo extends MessageVo {
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
    public Integer getTotalbagCount() {
		return totalbagCount;
	}

	public void setTotalbagCount(Integer totalbagCount) {
		this.totalbagCount = totalbagCount;
	}

	public float getTotalbagWeight() {
		return totalbagWeight;
	}

	public void setTotalbagWeight(float totalbagWeight) {
		this.totalbagWeight = totalbagWeight;
	}

	private Integer totalbagCount;
    private float totalbagWeight;
    private String formOfPayment;
    private String updateMode;
    private String raw;
    private List<String> rawList = new ArrayList<String>();
    private Integer daysBookedBeforeTravel;
    private boolean pnrRecordExists = false; 
    private List<FlightVo> flights = new ArrayList<>();
    private List<PassengerVo> passengers = new ArrayList<>();

    private List<AddressVo> addresses = new ArrayList<>();
    private List<PhoneVo> phoneNumbers = new ArrayList<>();
    private List<CreditCardVo> creditCards = new ArrayList<>();
    private List<FrequentFlyerVo> frequentFlyerDetails = new ArrayList<>();
    private List<EmailVo> emails = new ArrayList<>();
    private List<AgencyVo> agencies = new ArrayList<>();
    private List<FlightLegVo> flightLegs = new ArrayList<>();
    
    /** seat assignments in this pnr.  for display purposes only */
    private List<SeatVo> seatAssignments = new ArrayList<>();
    
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

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public List<String> getRawList() {
        return rawList;
    }

    public void setRawList(List<String> rawList) {
        this.rawList = rawList;
    }

    public boolean isPnrRecordExists() {
        return pnrRecordExists;
    }

    public void setPnrRecordExists(boolean pnrRecordExists) {
        this.pnrRecordExists = pnrRecordExists;
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

    public List<FlightLegVo> getFlightLegs() {
        return flightLegs;
    }

    public void setFlightLegs(List<FlightLegVo> flightLegs) {
        this.flightLegs = flightLegs;
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

    public String getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(String updateMode) {
        this.updateMode = updateMode;
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

    public List<SeatVo> getSeatAssignments() {
        return seatAssignments;
    }

    public void setSeatAssignments(List<SeatVo> seatAssignments) {
        this.seatAssignments = seatAssignments;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
