/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.lookup.PassengerTypeCode;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

/**
 * The Class TargetSummary Value Object.
 */
public class TargetSummaryVo implements Serializable, Cloneable {
	private static final long serialVersionUID = 2946626283174855377L;

	/** The flight id. */
	private Long flightId;

	/** The passenger id. */
	private Long passengerId;

	/** The passenger type. */
	private String passengerType;

	/** The passenger name. */
	private String passengerName;

	/** The hit type. */
	private HitTypeEnum hitType;

	/** The watchlist hit count. */
	private int watchlistHitCount;

	/** The rule hit count. */
	private int ruleHitCount;

	/** The hit details. */
	private Collection<TargetDetailVo> hitDetails;

	/**
	 * This constructor is used when creating a hit summary object.
	 *
	 * @param hitType the hit type
	 * @param flightId the flight id
	 * @param passengerType the passenger type
	 * @param passengerId the passenger id
	 * @param passengerName the passenger name
	 */
	public TargetSummaryVo(final HitTypeEnum hitType, final Long flightId,
			final PassengerTypeCode passengerType, final Long passengerId,
			final String passengerName) {
		this.passengerId = passengerId;
		this.passengerType = decodePassengerTypeName(passengerType.toString());
		this.passengerName = passengerName;
		this.flightId = flightId;
		this.hitType = hitType;
		this.hitDetails = new LinkedList<>();
	}

	/**
	 * Converts the passenger type code to a friendly name.
	 * 
	 * @param typ
	 *            the type code.
	 * @return the decoded type name.
	 */
	private String decodePassengerTypeName(String typ) {
		String ret = typ;
		for (PassengerTypeCode typeEnum : PassengerTypeCode.values()) {
			if (typ.equalsIgnoreCase(typeEnum.name())) {
				ret = typeEnum.getPassengerTypeName();
			}
		}
		return ret;
	}

	/**
	 * @return the flightId
	 */
	public Long getFlightId() {
		return flightId;
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
	public String getPassengerType() {
		return passengerType;
	}

	/**
	 * @return the passengerName
	 */
	public String getPassengerName() {
		return passengerName;
	}

	/**
	 * @return the watchlistHitCount
	 */
	public int getWatchlistHitCount() {
		return watchlistHitCount;
	}

	/**
	 * @return the ruleHitCount
	 */
	public int getRuleHitCount() {
		return ruleHitCount;
	}

	/**
	 * @return the hitType
	 */
	public HitTypeEnum getHitType() {
		return hitType;
	}

	/**
	 * @return the hitDetails
	 */
	public Collection<TargetDetailVo> getHitDetails() {
		return hitDetails;
	}

	/**
	 * Adds the hit detail.
	 *
	 * @param detail the detail
	 */
	public void addHitDetail(TargetDetailVo detail) {
		if (detail.getHitType() == HitTypeEnum.R) {
			this.ruleHitCount++;
		} else {
			this.watchlistHitCount++;
		}
		this.hitType = this.hitType.addHitType(detail.getHitType());
		hitDetails.add(detail);
	}

	@Override
	public TargetSummaryVo clone() throws CloneNotSupportedException {
		return (TargetSummaryVo) super.clone();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.passengerId, this.flightId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TargetSummaryVo))
			return false;
		final TargetSummaryVo other = (TargetSummaryVo) obj;
		return Objects.equals(this.passengerId, other.passengerId)
				&& Objects.equals(this.flightId, other.flightId);
	}
}
