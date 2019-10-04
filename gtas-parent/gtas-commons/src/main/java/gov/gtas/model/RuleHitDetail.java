/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */
package gov.gtas.model;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.lookup.PassengerTypeCode;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.NonNull;

/**
 * The Class RuleHitDetail corresponds to the entity HitDetail
 */
public final class RuleHitDetail implements Serializable, Cloneable {
	private static final long serialVersionUID = 2946626283174855377L;

	public static final String HIT_REASON_SEPARATOR = "///";

	private String hitRule;

	private Long hitMakerId;

	private Long ruleId;

	private Long flightId;

	private Long passengerId;

	private PassengerTypeCode passengerType;

	private String passengerName;

	private HitTypeEnum hitType;

	private int hitCount;

	private int ruleHitCount;

	private String[] hitReasons;

	@JsonIgnore
	private String title;

	@JsonIgnore
	private String description;

	@JsonIgnore
	private Passenger passenger;

	private String cipherQuery;

	public RuleHitDetail() {
	}

	public RuleHitDetail(@NonNull HitTypeEnum hitTypeEnum) {
		Objects.requireNonNull(hitTypeEnum);
		this.hitType = hitTypeEnum;
	}

	/**
	 * This constructor is used when creating a hit detail object as a result of a
	 * UDR rule hit.
	 *
	 * The RULE ENGINE will automatically generate this
	 * 
	 * @param hitMakerId
	 *            a udr rule Id (can be null)
	 * @param ruleId
	 *            a numeric rule Id (can be null)
	 * @param ruleTitle
	 *            the name of the DRL rule(Rule.getName()).
	 * @param passenger
	 *            the Passenger object that matched.
	 * @param flight
	 *            the flight object that matched.
	 * @param cause
	 *            the reason for the match.
	 */
	public RuleHitDetail(final Long hitMakerId, final Long ruleId, final String ruleTitle, final Passenger passenger,
						 final Flight flight, final String cause) {

		// THIS IS GENERATED FOR RULE HITS BY RULE ENGINE - SEE HOW RULES ARE MADE.
		// DO NOT CHANGE UNLESS UPDATING THE RULE GENERATION AS WELL
		this.hitType = HitTypeEnum.USER_DEFINED_RULE;
		this.hitMakerId = hitMakerId;
		this.ruleId = ruleId;
		this.title = ruleTitle;
		this.description = ruleTitle;
		this.hitRule = ruleTitle + "(" + hitMakerId + ")";
		this.passengerId = passenger.getId();
		this.passengerType = PassengerTypeCode.valueOf(passenger.getPassengerDetails().getPassengerType());
		this.passengerName = passenger.getPassengerDetails().getFirstName() + " "
				+ passenger.getPassengerDetails().getLastName();
		this.hitReasons = cause.split(HIT_REASON_SEPARATOR);
		if (flight != null) {
			this.flightId = flight.getId();
		}
		this.passenger = passenger;
		this.hitCount = 1;
		this.ruleHitCount = 1;
	}

	/**
	 * This constructor is used when creating a hit detail object as a result of a
	 * watch list hit.
	 * 
	 * @param hitMakerId
	 *            a watchlistItem id
	 * @param hitType
	 *            hit Type - corresponds to hit type enum
	 * @param passenger
	 *            the Passenger object that matched.
	 * @param cause
	 *            the reason for the match.
	 */
	public RuleHitDetail(final Long hitMakerId, final String hitType, final Passenger passenger, final String cause) {
		// THIS IS GENERATED FOR WATCHLIST HITS
		//
		this.hitMakerId = hitMakerId;
		this.ruleId = hitMakerId;
		switch (hitType) {
		case "D":
			this.title = "Document List Rule #" + hitMakerId;
			this.hitType = HitTypeEnum.WATCHLIST_DOCUMENT;

			break;
		case "P":
			this.title = "Passenger List Rule #" + hitMakerId;
			this.hitType = HitTypeEnum.WATCHLIST_PASSENGER;
			break;
		default:
			break;
		}
		this.description = this.title;
		this.hitRule = this.title + "(" + hitMakerId + ")";
		this.passengerId = passenger.getId();
		this.passengerType = PassengerTypeCode.valueOf(passenger.getPassengerDetails().getPassengerType());
		this.passengerName = passenger.getPassengerDetails().getFirstName() + " "
				+ passenger.getPassengerDetails().getLastName();
		this.hitReasons = cause.split(HIT_REASON_SEPARATOR);
		this.passenger = passenger;
		this.hitCount = 1;
	}

	/**
	 * @return the hitRule
	 */
	public String getHitRule() {
		return hitRule;
	}

	/**
	 * @return the udrRuleId
	 */
	public Long getHitMakerId() {
		return hitMakerId;
	}

	/**
	 * @return the passengerId
	 */
	public long getPassengerId() {
		return passengerId;
	}

	/**
	 * @return the passengerType
	 */
	public PassengerTypeCode getPassengerType() {
		return passengerType;
	}

	/**
	 * @return the passengerName
	 */
	public String getPassengerName() {
		return passengerName;
	}

	/**
	 * @return the hitReasons
	 */
	public String[] getHitReasons() {
		return hitReasons;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the flightId
	 */
	public Long getFlightId() {
		return flightId;
	}

	/**
	 * @param flightId
	 *            the flightId to set
	 */
	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	/**
	 * @return the hitType
	 */
	public HitTypeEnum getHitType() {
		return hitType;
	}

	/**
	 * @param hitType
	 *            the hitType to set
	 */
	public void setHitType(HitTypeEnum hitType) {
		this.hitType = hitType;
	}

	/**
	 * @return the ruleId
	 */
	public Long getRuleId() {
		return ruleId;
	}

	/**
	 * @return the hitCount
	 */
	public int getHitCount() {
		return hitCount;
	}

	/**
	 *             the hitCount to set
	 */
	public void incrementHitCount() {
		this.hitCount++;
	}

	/**
	 * @return the ruleHitCount
	 */
	public int getRuleHitCount() {
		return ruleHitCount;
	}

	/***            the ruleHitCount to set
	 */
	public void incrementRuleHitCount() {
		this.ruleHitCount++;
	}

	/**
	 * @return the passenger
	 */
	public Passenger getPassenger() {
		return passenger;
	}

	/**
	 * @param passenger
	 *            the passenger to set
	 */
	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public void setHitRule(String hitRule) {
		this.hitRule = hitRule;
	}

	public void setHitMakerId(Long hitMakerId) {
		this.hitMakerId = hitMakerId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public void setPassengerType(PassengerTypeCode passengerType) {
		this.passengerType = passengerType;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public void setRuleHitCount(int ruleHitCount) {
		this.ruleHitCount = ruleHitCount;
	}

	public void setHitReasons(String[] hitReasons) {
		this.hitReasons = hitReasons;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public RuleHitDetail clone() throws CloneNotSupportedException {
		return (RuleHitDetail) super.clone();
	}

	public String getCipherQuery() {
		return cipherQuery;
	}

	public void setCipherQuery(String cipherQuery) {
		this.cipherQuery = cipherQuery;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof RuleHitDetail))
			return false;
		RuleHitDetail hitDetail = (RuleHitDetail) o;
		return getPassengerId() == hitDetail.getPassengerId()
				&& getHitMakerId().equals(hitDetail.getHitMakerId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getHitMakerId(), getPassengerId());
	}
}
