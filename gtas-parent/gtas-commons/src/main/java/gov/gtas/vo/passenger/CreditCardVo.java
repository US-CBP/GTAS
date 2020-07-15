/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.model.PIIObject;

import java.util.Date;

public class CreditCardVo implements PIIObject {
	private String cardType;
	private String number;
	private Date expiration;
	private String accountHolder;

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public String getAccountHolder() {
		return accountHolder;
	}

	public void setAccountHolder(String accountHolder) {
		this.accountHolder = accountHolder;
	}

	@Override
	public PIIObject deletePII() {
		this.number = "DELETED";
		this.accountHolder = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.number = "MASKED";
		this.accountHolder = "MASKED";
		return this;
	}
}
