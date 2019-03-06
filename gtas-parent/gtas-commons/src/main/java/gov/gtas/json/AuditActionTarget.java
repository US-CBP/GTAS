/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.json;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.model.Passenger;
import gov.gtas.model.User;

import java.io.Serializable;

/**
 * JSON object class to convey audit action target information.
 */
public class AuditActionTarget implements Serializable {
	private static final long serialVersionUID = -4390502518498037033L;

	private String targetType;
	private String targetName;
	private String targetId;

	/**
	 * Instantiates a new audit action target.
	 *
	 * @param user
	 *            the user
	 */
	public AuditActionTarget(User user) {
		this.targetType = "USER";
		StringBuilder sb = new StringBuilder();
		if (user.getFirstName() != null) {
			sb.append(user.getFirstName());
		}
		if (user.getLastName() != null) {
			sb.append(" " + user.getLastName());
		}
		this.targetName = sb.toString();
		this.targetId = user.getUserId();
	}

	/**
	 * Instantiates a new audit action target.
	 *
	 * @param passenger
	 *            the passenger
	 */
	public AuditActionTarget(Passenger passenger) {
		this.targetType = "PASSENGER";
		StringBuilder sb = new StringBuilder();
		if (passenger.getPassengerDetails().getFirstName() != null) {
			sb.append(passenger.getPassengerDetails().getFirstName());
		}
		if (passenger.getPassengerDetails().getMiddleName() != null) {
			sb.append(" " + passenger.getPassengerDetails().getMiddleName());
		}
		if (passenger.getPassengerDetails().getLastName() != null) {
			sb.append(" " + passenger.getPassengerDetails().getLastName());
		}
		this.targetName = sb.toString();
		this.targetId = String.valueOf(passenger.getId());
	}

	/**
	 * Instantiates a new audit action target.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param id
	 *            the id
	 */
	public AuditActionTarget(AuditActionType type, String name, String id) {
		this.targetType = computeType(type);
		this.targetName = name;
		this.targetId = id;
	}

	/**
	 * Compute type.
	 *
	 * @param type
	 *            the type
	 * @return the string
	 */
	private String computeType(AuditActionType type) {
		switch (type) {
		case CREATE_UDR:
		case DELETE_UDR:
		case UPDATE_UDR:
			return "UDR";
		case UPDATE_UDR_META:
			return "UDR META DATA";
		case CREATE_WL:
		case DELETE_WL:
		case UPDATE_WL:
		case DELETE_ALL_WL:
			return "WATCHLIST";
		case LOADER_RUN:
		case LOAD_APIS:
		case LOAD_PNR:
			return "API/PNR LOADER";
		case UPDATE_USER:
		case SUSPEND_USER:
		case DELETE_USER:
		case CREATE_USER:
			return "USER";
		case TARGETING_RUN:
			return "RULE ENGINE";
		case UPDATE_DASHBOARD_RUN:
			return "DASHBOARD";
		case MESSAGE_INGEST_PARSING:
			return "API/PNR MESSAGE";
		case RULE_HIT_CASE_OPEN:
			return "RULE & CASE MANAGEMENT";
		case DISPOSITION_STATUS_CHANGE:
			return "DISPOSITION";
		default:
			return "UNKNOWN";
		}
	}

	/**
	 * @return the targetType
	 */
	public String getTargetType() {
		return targetType;
	}

	/**
	 * @param targetType
	 *            the targetType to set
	 */
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	/**
	 * @return the targetName
	 */
	public String getTargetName() {
		return targetName;
	}

	/**
	 * @param targetName
	 *            the targetName to set
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	/**
	 * @return the targetId
	 */
	public String getTargetId() {
		return targetId;
	}

	/**
	 * @param targetId
	 *            the targetId to set
	 */
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder();
		bldr.append("{\"type\":\"").append(this.targetType).append("\",");
		if (this.targetName != null) {
			bldr.append("\"name\":\"").append(this.targetName).append("\",");
		}
		if (this.targetId != null) {
			bldr.append("\"id\":\"").append(this.targetId).append("\"}");
		} else {
			bldr.append("\"id\":\"\"}");
		}
		return bldr.toString();
	}

}
