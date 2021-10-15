/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message_status", indexes = {
		@Index(name = "message_status_ms_status_index", columnList = "ms_status"),
		@Index(name = "ms_updated_at_index" , columnList = "updated_at")
		})
public class MessageStatus {

	@Id
	@Column(name = "ms_message_id", columnDefinition = "bigint unsigned")
	private Long messageId;

	@OneToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "ms_message_id", referencedColumnName = "id", updatable = false, insertable = false)
	private Message message;

	@Enumerated(EnumType.STRING)
	@Column(name = "ms_status")
	private MessageStatusEnum messageStatusEnum;

	@Column(name = "ms_analyzed_timestamp")
	private Date analyzedTimestamp;

	@Column(name = "flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@ManyToOne()
	@JoinColumn(name = "flight_id", insertable = false, updatable = false)
	private Flight flight;
	
	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	/**
	 * @return the updatedAt
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Sets createdAt before insert
	 */
	@PrePersist
	public void setCreationDate() {
		this.createdAt = new Date();
		this.updatedAt = new Date();
	}

	/**
	 * Sets updatedAt before update
	 */
	@PreUpdate
	public void setChangeDate() {
		this.updatedAt = new Date();
	}

	public boolean isNoLoadingError() {
		return noLoadingError;
	}

	public void setNoLoadingError(boolean noLoadingError) {
		this.noLoadingError = noLoadingError;
	}

	@Transient
	private boolean noLoadingError;

	public MessageStatus() {
	}

	public MessageStatus(Long message, MessageStatusEnum status) {
		this.messageId = message;
		this.messageStatusEnum = status;
	}

	public Date getAnalyzedTimestamp() {
		return analyzedTimestamp;
	}

	public void setAnalyzedTimestamp(Date analyzedTimestamp) {
		this.analyzedTimestamp = analyzedTimestamp;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public MessageStatusEnum getMessageStatusEnum() {
		return messageStatusEnum;
	}

	public void setMessageStatusEnum(MessageStatusEnum messageStatusEnum) {
		this.messageStatusEnum = messageStatusEnum;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}
}
