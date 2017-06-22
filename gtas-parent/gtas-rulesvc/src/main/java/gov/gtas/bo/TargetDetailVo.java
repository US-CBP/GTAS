/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo;

import gov.gtas.enumtype.HitTypeEnum;

import java.io.Serializable;

public class TargetDetailVo implements Serializable, Cloneable {
	private static final long serialVersionUID = 2946626283174855377L;

	/** The title. */
	private String title;

	/** The description. */
	private String description;

	/** The udr rule id. */
	private Long udrRuleId;

	/*
	 * either the engine rule id or the watch list item id.
	 */
	private Long ruleId;

	/** The hit type. */
	private HitTypeEnum hitType;

	/** The hit reasons. */
	private String[] hitReasons;

	/**
	 * This constructor is used when creating a detail object as a result of a
	 * UDR rule hit.
	 * 
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
	public TargetDetailVo(final Long udrId, final Long ruleId,
			final HitTypeEnum hitType, final String ruleTitle,
			final String[] cause) {
		this.udrRuleId = udrId;
		this.ruleId = ruleId;
		this.hitType = hitType;
		switch (hitType) {
		case R:
			this.title = ruleTitle;
			this.description = String
					.format("There was a match for UDR '%s', with id=%d, and ruleId=%d",
							ruleTitle, udrId, ruleId);
			break;
		case D:
			this.title = ruleTitle;
			this.description = "Document List hit with Rule #" + ruleId;
			break;
		case P:
			this.title = ruleTitle;
			this.description = "Passenger List hit with Rule #" + ruleId;
			break;
		default:
			break;
		}
		this.hitReasons = cause;
	}

	/**
	 * @return the ruleId
	 */
	public Long getRuleId() {
		return ruleId;
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
	 * @return the udrRuleId
	 */
	public Long getUdrRuleId() {
		return udrRuleId;
	}

	/**
	 * @return the hitReasons
	 */
	public String[] getHitReasons() {
		return hitReasons;
	}

	/**
	 * @return the hitType
	 */
	public HitTypeEnum getHitType() {
		return hitType;
	}

	@Override
	public TargetDetailVo clone() throws CloneNotSupportedException {
		return (TargetDetailVo) super.clone();
	}
}
