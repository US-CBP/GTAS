/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.model.PIIObject;

public class EmailVo implements PIIObject {
	private String address;
	private String domain;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public PIIObject deletePII() {
		this.address = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.address = "MASKED";
		return this;
	}
}
