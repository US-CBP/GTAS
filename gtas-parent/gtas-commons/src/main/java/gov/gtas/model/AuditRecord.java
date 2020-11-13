/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "audit_log")
public abstract class AuditRecord extends BaseEntity {
	private static final long serialVersionUID = 18887L;

	public AuditRecord() {
	}

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
	protected User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "action_type", nullable = false, length = 32)
	protected AuditActionType actionType;

	@Enumerated(EnumType.STRING)
	@Column(name = "actionStatus", nullable = false, length = 32)
	protected Status actionStatus;

	/*
	 * UDR Title, or WL Name, or loaded message file path, or userId
	 * created/updated, or ID for a batch job
	 */
	@Column(name = "action_target", nullable = false, length = 1024)
	protected String target;

	@Column(name = "action_message", nullable = true)
	protected String message;

	@Column(name = "action_data", nullable = true, length = 2000000)
	@Lob
	protected String actionData;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	protected Date timestamp;


	/**
	 * @return the time-stamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return the actionType
	 */
	public AuditActionType getActionType() {
		return actionType;
	}

	/**
	 * @return the actionStatus
	 */
	public Status getActionStatus() {
		return actionStatus;
	}

	/**
	 * @return the summary
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the actionData
	 */
	public String getActionData() {
		return actionData;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.timestamp, this.actionType, this.message);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AuditRecord other = (AuditRecord) obj;
		return Objects.equals(this.actionType, other.actionType) && Objects.equals(this.message, other.message)
				&& Objects.equals(this.timestamp, other.timestamp);
	}
}
