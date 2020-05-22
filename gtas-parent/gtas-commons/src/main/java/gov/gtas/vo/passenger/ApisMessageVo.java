/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.gtas.model.PIIObject;
import gov.gtas.services.search.FlightPassengerVo;
import gov.gtas.vo.MessageVo;

public class ApisMessageVo extends MessageVo implements PIIObject {
	private String travelerType;
	private String residenceCountry;
	private List<PhoneVo> phoneNumbers = new ArrayList<>();
	private int bagCount;
	private double bagWeight;
	private List<BagVo> bags = new ArrayList<>();
	private boolean apisRecordExists = false;

	private Set<FlightPassengerVo> flightpaxs = new HashSet<>();

	public boolean isApisRecordExists() {
		return apisRecordExists;
	}

	public void setApisRecordExists(boolean apisRecordExists) {
		this.apisRecordExists = apisRecordExists;
	}

	public String getTravelerType() {
		return travelerType;
	}

	public void setTravelerType(String travelerType) {
		this.travelerType = travelerType;
	}

	public String getResidenceCountry() {
		return residenceCountry;
	}

	public void setResidenceCountry(String residenceCountry) {
		this.residenceCountry = residenceCountry;
	}

	public List<PhoneVo> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneVo> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public void addPhoneNumber(PhoneVo phoneNumber) {
		this.phoneNumbers.add(phoneNumber);
	}

	public int getBagCount() {
		return bagCount;
	}

	public void setBagCount(int bagCount) {
		this.bagCount = bagCount;
	}

	public List<BagVo> getBags() {
		return bags;
	}

	public void setBags(List<BagVo> bags) {
		this.bags = bags;
	}

	public void addBag(BagVo b) {
		bags.add(b);
	}

	public void addFlightpax(FlightPassengerVo flightpax) {
		this.flightpaxs.add(flightpax);
	}

	public double getBagWeight() {
		return bagWeight;
	}

	public void setBagWeight(double bagWeight) {
		this.bagWeight = bagWeight;
	}

	public Set<FlightPassengerVo> getFlightpaxs() {
		return flightpaxs;
	}

	public void setFlightpaxs(Set<FlightPassengerVo> flightpaxs) {
		this.flightpaxs = flightpaxs;
	}


	@Override
	public PIIObject deletePII() {
		for (PhoneVo pVo : this.phoneNumbers) {
			pVo.deletePII();
		}
		for (FlightPassengerVo passengerVo : flightpaxs) {
			passengerVo.deletePII();
		}
		return this;
	}

	@Override
	public PIIObject maskPII() {
		for (PhoneVo pVo : this.phoneNumbers) {
			pVo.maskPII();
		}
		for (FlightPassengerVo passengerVo : flightpaxs) {
			passengerVo.maskPII();
		}
		return this;
	}
}
