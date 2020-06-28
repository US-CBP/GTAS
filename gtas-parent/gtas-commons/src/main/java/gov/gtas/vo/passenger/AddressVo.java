/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.model.PIIObject;

public class AddressVo implements PIIObject {
	private String type;
	private String line1;
	private String line2;
	private String line3;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String phoneNumber;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public PIIObject deletePII() {
		this.line1 = "DELETED";
		this.line2 = "DELETED";
		this.line3 = "DELETED";
		this.phoneNumber = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.line1 = "MASKED";
		this.line2 = "MASKED";
		this.line3 = "MASKED";
		this.phoneNumber = "MASKED";
		return this;	}
}
