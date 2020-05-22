/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.model.PIIObject;

public class PhoneVo implements PIIObject {
	private String number;
	private String city;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public PIIObject deletePII() {
		this.number = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.number = "MASKED";
		return this;
	}
}
