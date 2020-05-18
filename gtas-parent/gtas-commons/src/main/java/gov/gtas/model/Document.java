/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import gov.gtas.enumtype.MessageType;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "document")
public class Document extends BaseEntity implements PIIObject {
	private static final long serialVersionUID = 1L;

	public Document() {
	}

	public Document(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	@Column(name = "document_type", length = 3)
	private String documentType;

	@Column(name = "document_number")
	private String documentNumber;

	@Column(name = "expiration_date")
	@Temporal(TemporalType.DATE)
	private Date expirationDate;

	@Column(name = "issuance_date")
	@Temporal(TemporalType.DATE)
	private Date issuanceDate;

	@Column(name = "issuance_country")
	private String issuanceCountry;

	@Column(name = "message_type")
	@Enumerated(EnumType.STRING)
	private MessageType messageType;

	@JoinColumn(name = "passenger_id", columnDefinition = "bigint unsigned")
	@ManyToOne(fetch = FetchType.LAZY)
	private Passenger passenger;

	@ManyToMany(mappedBy = "documents")
	private Set<Message> messages = new HashSet<>();


	@Column(name = "flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	private Flight flight;

	@Column(name = "passenger_id", columnDefinition = "bigint unsigned", insertable = false, updatable = false)
	private Long passengerId;

	/** calculated field */
	@Column(name = "days_valid")
	private Integer numberOfDaysValid;

	@OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
	private Set<DocumentRetentionPolicyAudit> documentRetentionPolicyAudits = new HashSet<>();

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}
	public Integer getNumberOfDaysValid() {
		return numberOfDaysValid;
	}

	public void setNumberOfDaysValid(Integer numberOfDaysValid) {
		this.numberOfDaysValid = numberOfDaysValid;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public Date getIssuanceDate() {
		return issuanceDate;
	}

	public void setIssuanceDate(Date issuanceDate) {
		this.issuanceDate = issuanceDate;
	}

	public String getIssuanceCountry() {
		return issuanceCountry;
	}

	public void setIssuanceCountry(String issuanceCountry) {
		this.issuanceCountry = issuanceCountry;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long paxId) {
		this.passengerId = paxId;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.documentNumber, this.messageType, this.documentType, this.passengerId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Document))
			return false;
		final Document other = (Document) obj;
		return Objects.equals(this.documentNumber, other.documentNumber)
				&& Objects.equals(this.passengerId, other.passengerId)
				&& Objects.equals(this.messageType, other.messageType);
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public Set<DocumentRetentionPolicyAudit> getDocumentRetentionPolicyAudits() {
		return documentRetentionPolicyAudits;
	}

	public void setDocumentRetentionPolicyAudits(Set<DocumentRetentionPolicyAudit> documentRetentionPolicyAudits) {
		this.documentRetentionPolicyAudits = documentRetentionPolicyAudits;
	}

	@Override
	public PIIObject deletePII() {
		this.documentNumber  = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.documentNumber = "MASKED";
		return this;
	}
}
