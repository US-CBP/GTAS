/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.validators.Validatable;

public class CreditCardVo implements Validatable {
	private String cardType;
	private String number;
	private Date expiration;
	private String accountHolder;
	private String accountHolderAddress;
	private String accountHolderPhone;

	public String getAccountHolderAddress() {
		return accountHolderAddress;
	}

	public void setAccountHolderAddress(String accountHolderAddress) {
		this.accountHolderAddress = accountHolderAddress;
	}

	public String getAccountHolderPhone() {
		return accountHolderPhone;
	}

	public void setAccountHolderPhone(String accountHolderPhone) {
		this.accountHolderPhone = accountHolderPhone;
	}

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
	public boolean isValid() {
		return StringUtils.isNotBlank(this.number);
	}
}
