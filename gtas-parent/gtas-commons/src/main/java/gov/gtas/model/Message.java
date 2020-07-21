/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.sql.Clob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "message")
@Inheritance(strategy = InheritanceType.JOINED)
//@NamedEntityGraph(name = "messageRetention", attributeNodes = { @NamedAttributeNode("id"),
//		@NamedAttributeNode(("createDate")) })
public class Message extends BaseEntity implements MessageFields {
	private static final long serialVersionUID = 1L;

	public Message() {
	}

	@Column(name = "create_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Lob
	private Clob raw;

	@Column(name = "hash_code", unique = true)
	private String hashCode;

	@Column(name = "file_path", nullable = false)
	private String filePath;

	@OneToOne(cascade = CascadeType.PERSIST, targetEntity = MessageStatus.class, mappedBy = "message", fetch = FetchType.EAGER)
	@JoinColumn(name = "id", unique = true, referencedColumnName = "ms_message_id", insertable = false, updatable = false)
	private MessageStatus status;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "message")
	private List<FlightLeg> flightLegs = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = BookingDetail.class)
	@JoinTable(name = "message_booking", joinColumns = @JoinColumn(name = "message_id"), inverseJoinColumns = @JoinColumn(name = "booking_detail_id"))
	private Set<BookingDetail> bookingDetails = new HashSet<>();

	@Column(length = 4000)
	private String error;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "message")
	private Set<PassengerDetailFromMessage> passengerDetailFromMessages = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "message_document", joinColumns = @JoinColumn(name = "document_id"), inverseJoinColumns = @JoinColumn(name = "message_id"))
	private Set<Document> documents = new HashSet<>();

	@Column(name = "passenger_count")
	protected Integer passengerCount;

	@Transient
	private List<PendingHitDetails> pendingHitDetails = new ArrayList<>();

	public List<PendingHitDetails> getPendingHitDetails() {
		return pendingHitDetails;
	}

	public void setPendingHitDetails(List<PendingHitDetails> pendingHitDetails) {
		this.pendingHitDetails = pendingHitDetails;
	}
	public Integer getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(Integer passengerCount) {
		this.passengerCount = passengerCount;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Clob getRaw() {
		return raw;
	}

	public void setRaw(Clob raw) {
		this.raw = raw;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public MessageStatus getStatus() {
		return status;
	}

	public void setStatus(MessageStatus status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.hashCode);
	}

	public void addFlightLeg(FlightLeg leg) {
		flightLegs.add(leg);
		leg.setMessage(this);
	}

	public void removeFlightLeg(FlightLeg leg) {
		flightLegs.remove(leg);
		leg.setMessage(null);
	}

	public List<FlightLeg> getFlightLegs() {
		return flightLegs;
	}

	public void setFlightLegs(List<FlightLeg> flightLegs) {
		this.flightLegs = flightLegs;
	}

	public Set<BookingDetail> getBookingDetails() {
		return bookingDetails;
	}

	public void setBookingDetails(Set<BookingDetail> bookingDetails) {
		this.bookingDetails = bookingDetails;
	}

	// public void addPassenger(Passenger p) {
	// if (this.passengers == null) {
	// this.passengers = new HashSet<>();
	// }
	// this.passengers.add(p);
	// }
	//
	// public Set<Passenger> getPassengers() {
	// return passengers;
	// }
	//
	// public void setPassengers(Set<Passenger> passengers) {
	// this.passengers = passengers;
	// }
	//

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Message))
			return false;
		final Message other = (Message) obj;
		return Objects.equals(this.hashCode, other.hashCode);
	}

	public Set<PassengerDetailFromMessage> getPassengerDetailFromMessages() {
		return passengerDetailFromMessages;
	}

	public void setPassengerDetailFromMessages(Set<PassengerDetailFromMessage> passengerDetailFromMessages) {
		this.passengerDetailFromMessages = passengerDetailFromMessages;
	}

	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}

	@Override
	public EdifactMessage getEdifactMessage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEdifactMessage(EdifactMessage edifactMessage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Passenger> getPassengers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPassengers(Set<Passenger> passengers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Flight> getFlights() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFlights(Set<Flight> flights) {
		throw new UnsupportedOperationException();
	}
}
