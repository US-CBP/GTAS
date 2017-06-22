/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import java.util.ArrayList;
import java.util.List;

import gov.gtas.vo.MessageVo;

public class ApisMessageVo extends MessageVo{
	private String travelerType;
	private String residenceCountry;
    private List<PhoneVo> phoneNumbers = new ArrayList<>();
    private AddressVo installationAddress;
    private String embarkation;
    private String debarkation;
    private String portOfFirstArrival; 
    private int bagCount;
    private List<BagVo> bags = new ArrayList<>();
    private boolean apisRecordExists = false;
    
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
	public AddressVo getInstallationAddress() {
		return installationAddress;
	}
	public void setInstallationAddress(AddressVo installationAddress) {
		this.installationAddress = installationAddress;
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
	public String getPortOfFirstArrival() {
		return portOfFirstArrival;
	}
	public void setPortOfFirstArrival(String portOfFirstArrival) {
		this.portOfFirstArrival = portOfFirstArrival;
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
}
