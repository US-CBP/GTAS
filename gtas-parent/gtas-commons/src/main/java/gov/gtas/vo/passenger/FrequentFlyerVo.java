/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.model.PIIObject;

public class FrequentFlyerVo implements PIIObject {
	private String carrier;
	private String number;

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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
