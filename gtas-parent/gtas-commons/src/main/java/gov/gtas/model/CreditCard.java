/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "credit_card")
public class CreditCard extends BaseEntityAudit implements PIIObject{
	private static final long serialVersionUID = 1L;

	public CreditCard() {
	}

	@Column(name = "card_type")
	private String cardType;

	@Column(nullable = false)
	private String number;

	@Temporal(TemporalType.DATE)
	private Date expiration;

	@Column(name = "account_holder")
	private String accountHolder;

	@Column(name = "account_holder_address")
	private String accountHolderAddress;

	@Column(name = "account_holder_phone")
	private String accountHolderPhone;

	@Column(name = "flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	private Flight flight;

	@ManyToMany(mappedBy = "creditCards", targetEntity = Pnr.class)
	private Set<Pnr> pnrs = new HashSet<>();

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

	public Set<Pnr> getPnrs() {
		return pnrs;
	}

	public void setPnrs(Set<Pnr> pnrs) {
		this.pnrs = pnrs;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.cardType, this.number, this.expiration);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CreditCard other = (CreditCard) obj;
		return Objects.equals(this.cardType, other.cardType) && Objects.equals(this.number, other.number)
				&& Objects.equals(this.expiration, other.expiration) && Objects.equals(this.accountHolder, other.accountHolder)
				&& (Objects.equals(this.accountHolderAddress, other.accountHolderAddress)) && Objects.equals(this.accountHolderPhone, other.accountHolderPhone);
	}

	@Override
	public PIIObject deletePII() {
		this.number = "DELETED " + UUID.randomUUID().toString();
		this.accountHolder = "DELETED " + UUID.randomUUID().toString();
		this.accountHolderAddress = "DELETED " + UUID.randomUUID().toString();
		this.accountHolderPhone = "DELETED " + UUID.randomUUID().toString();
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.number = "XXXX";
		this.accountHolder = "XXXX";
		this.accountHolderAddress = "XXXX";
		this.accountHolderPhone = "XXXX";
		return this;	}
}
