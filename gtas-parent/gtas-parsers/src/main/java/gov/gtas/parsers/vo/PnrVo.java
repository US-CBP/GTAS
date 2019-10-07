/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import gov.gtas.parsers.pnrgov.segment.TVL_L0;
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
	private double baggageWeight = 0;
	private int total_bag_count = 0;
	private double total_bag_weight = 0;
	private String baggageUnit;
	private String formOfPayment;
	private TVL_L0 primeFlight;
	private Integer daysBookedBeforeTravel;

	private List<PassengerVo> passengers = new ArrayList<>();
	private List<BagMeasurementsVo> bagMeasurements = new ArrayList<>();
	private List<AddressVo> addresses = new ArrayList<>();
	private List<PhoneVo> phoneNumbers = new ArrayList<>();
	private List<CreditCardVo> creditCards = new ArrayList<>();
	private List<FrequentFlyerVo> frequentFlyerDetails = new ArrayList<>();
	private List<EmailVo> emails = new ArrayList<>();
	private List<AgencyVo> agencies = new ArrayList<>();
	private List<CodeShareVo> codeshares = new ArrayList<>();
	private List<PaymentFormVo> FormOfPayments = new ArrayList<>();
	private Date reservationCreateDate;
	private Boolean headPool = Boolean.FALSE;

	public double getTotal_bag_weight() {
		return total_bag_weight;
	}

	public void setTotal_bag_weight(double total_bag_weight) {
		this.total_bag_weight = total_bag_weight;
	}

	public Boolean getHeadPool() {
		return headPool;
	}

	public void setHeadPool(Boolean headPool) {
		this.headPool = headPool;
	}

	public List<PaymentFormVo> getFormOfPayments() {
		return FormOfPayments;
	}

	public void setFormOfPayments(List<PaymentFormVo> formOfPayments) {
		FormOfPayments = formOfPayments;
	}

	public Integer getTotal_bag_count() {
		return total_bag_count;
	}

	public void setTotal_bag_count(Integer total_bag_count) {
		this.total_bag_count = total_bag_count;
	}

	public List<CodeShareVo> getCodeshares() {
		return codeshares;
	}

	public void setCodeshares(List<CodeShareVo> codeshares) {
		this.codeshares = codeshares;
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

	public void addFormOfPayments(PaymentFormVo payment) {
		FormOfPayments.add(payment);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public boolean isValid() {
		if (StringUtils.isBlank(this.recordLocator) || StringUtils.isBlank(this.carrier)) {
			return false;
		}
		return true;
	}

	public TVL_L0 getPrimeFlight() {
		return primeFlight;
	}

	public void setPrimeFlight(TVL_L0 primeFlight) {
		this.primeFlight = primeFlight;
	}

	public List<BagMeasurementsVo> getBagMeasurements() {
		return bagMeasurements;
	}

	public void setBagMeasurements(List<BagMeasurementsVo> bagMeasurements) {
		this.bagMeasurements = bagMeasurements;
	}
}
