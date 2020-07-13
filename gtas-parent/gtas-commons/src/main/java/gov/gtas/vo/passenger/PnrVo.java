/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.json.KeyValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.gtas.model.PIIObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.vo.MessageVo;

public class PnrVo extends MessageVo implements PIIObject {
    private Long id;
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
    private List<BagVo> bags = new ArrayList<>();
    private BagSummaryVo bagSummaryVo;

    private Integer total_bag_count;
    private double baggageWeight;
    private String formOfPayment;
    private String updateMode;
    private String raw;
    private List<String> rawList = new ArrayList<>();
    private List<KeyValue> segmentList = new ArrayList<>();
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
    private List<DocumentVo> documents = new ArrayList<>();
    private List<SeatVo> seatAssignments = new ArrayList<>();
    private String tripType;
    private double tripDuration;

    public double getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(double tripDuration) {
        this.tripDuration = tripDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DocumentVo> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentVo> documents) {
        this.documents = documents;
    }

    public void addBag(BagVo b) {
        bags.add(b);
    }

    public List<BagVo> getBags() {
        return bags;
    }

    public double getBaggageWeight() {
        return baggageWeight;
    }

    public void setBaggageWeight(double baggageWeight) {
        this.baggageWeight = baggageWeight;
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

    public void addSeat(SeatVo s) {
        this.seatAssignments.add(s);
    }

    public void setSeatAssignments(List<SeatVo> seatAssignments) {
        this.seatAssignments = seatAssignments;
    }

    public List<KeyValue> getSegmentList() {
        return segmentList;
    }

    public void setSegmentList(List<KeyValue> segmentList) {
        this.segmentList = segmentList;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public Integer getTotal_bag_count() {
        return total_bag_count;
    }

    public void setTotal_bag_count(Integer total_bag_count) {
        this.total_bag_count = total_bag_count;
    }

    public BagSummaryVo getBagSummaryVo() {
        return bagSummaryVo;
    }

    public void setBagSummaryVo(BagSummaryVo bagSummaryVo) {
        this.bagSummaryVo = bagSummaryVo;
    }

    @Override
    public PIIObject deletePII() {

        for (PassengerVo pVo : passengers) {
            pVo.deletePII();
        }
        for (CreditCardVo cVo : creditCards) {
            cVo.deletePII();
        }
        for (DocumentVo dVo : documents) {
            dVo.deletePII();
        }
        for (PhoneVo phones : phoneNumbers) {
            phones.deletePII();
        }
        for (AddressVo addressVo : addresses) {
            addressVo.deletePII();
        }
        for (FrequentFlyerVo frequentFlyer : frequentFlyerDetails) {
            frequentFlyer.deletePII();
        }
        for (EmailVo emailVo : emails) {
            emailVo.deletePII();
        }
        for (SeatVo seatVo : seatAssignments) {
            seatVo.deletePII();
        }
        this.raw = "DELETED";
        this.rawList = new ArrayList<>();
        return this;
    }

    @Override
    public PIIObject maskPII() {
        for (PassengerVo pVo : passengers) {
            pVo.maskPII();
        }
        for (CreditCardVo cVo : creditCards) {
            cVo.maskPII();
        }
        for (PhoneVo phones : phoneNumbers) {
            phones.maskPII();
        }
        for (DocumentVo dVo : documents) {
            dVo.maskPII();
        }
        for (AddressVo addressVo : addresses) {
            addressVo.maskPII();
        }
        for (FrequentFlyerVo frequentFlyer : frequentFlyerDetails) {
            frequentFlyer.maskPII();
        }
        for (EmailVo emailVo : emails) {
            emailVo.maskPII();
        }
        for (SeatVo seatVo : seatAssignments) {
            seatVo.maskPII();
        }
        this.raw = "MASKED";
        this.rawList = new ArrayList<>();
        return this;
    }
}
