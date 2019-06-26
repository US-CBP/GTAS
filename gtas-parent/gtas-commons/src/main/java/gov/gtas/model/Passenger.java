/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import java.util.*;
import javax.persistence.*;


@Entity
@Table(name = "passenger")
public class Passenger extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;
    
    public Passenger() {
    }

    // This is a convenience method to see the flight associated with the passenger.
    // This relationship is manually made in the loader.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name="flight_passenger",
            joinColumns={@JoinColumn(name="passenger_id")},
            inverseJoinColumns={@JoinColumn(name="flight_id")})
    @JsonIgnore
    private Flight flight;

    @OneToOne(cascade = {CascadeType.PERSIST}, targetEntity = PassengerDetails.class, fetch=FetchType.LAZY, mappedBy = "passenger", optional = false)
    private PassengerDetails passengerDetails;

    @OneToOne(cascade =  {CascadeType.PERSIST}, targetEntity = PassengerTripDetails.class, fetch=FetchType.LAZY, mappedBy = "passenger", optional = false)
    private PassengerTripDetails passengerTripDetails;

    @OneToOne(mappedBy = "passenger", targetEntity = PassengerWLTimestamp.class, fetch=FetchType.LAZY)
    private PassengerWLTimestamp passengerWLTimestamp;

    @OneToOne(mappedBy = "passenger", targetEntity = PassengerIDTag.class, fetch = FetchType.LAZY)
    private PassengerIDTag passengerIDTag;

    @ManyToMany(mappedBy = "passengers", targetEntity = ApisMessage.class)
    private Set<ApisMessage> apisMessage = new HashSet<>();

    @ManyToMany(mappedBy = "passengers", targetEntity = Pnr.class)
    private Set<Pnr> pnrs = new HashSet<>();

    @ManyToMany(targetEntity = BookingDetail.class)
    @JoinTable(name = "pax_booking", joinColumns = @JoinColumn(name = "pax_id"), inverseJoinColumns = @JoinColumn(name = "booking_detail_id"))
    private Set<BookingDetail> bookingDetails = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger", fetch = FetchType.LAZY)
    private Set<Document> documents = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "passenger", fetch = FetchType.LAZY)
    private Set<Attachment> attachments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger", fetch = FetchType.LAZY)
    private Set<Seat> seatAssignments = new HashSet<>();

    @OneToMany(mappedBy = "passenger", fetch = FetchType.LAZY)
    private Set<HitsSummary> hits = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger", fetch = FetchType.LAZY)
    private Set<PaxWatchlistLink> paxWatchlistLinks = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger", fetch = FetchType.LAZY)
    private Set<Bag> bags = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY, mappedBy="passenger")
    private Set<FlightPax> flightPaxList = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger", fetch = FetchType.LAZY)
    private Set<TicketFare> tickets = new HashSet<>();

    @Type(type = "uuid-char")
    @Column(name = "uuid", updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;

    /*
     * Used to keep a referenced to passengerVO from parser.
     * Only used in loader to help establish relationships.
     * This is *not* used
     * */
    @Transient
    private UUID parserUUID;

    public UUID getParserUUID() {
        return parserUUID;
    }

    public void setParserUUID(UUID parserUUID) {
        this.parserUUID = parserUUID;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Set<BookingDetail> getBookingDetails() {
		return bookingDetails;
	}

	public void setBookingDetails(Set<BookingDetail> bookingDetails) {
		this.bookingDetails = bookingDetails;
	}

	public Set<TicketFare> getTickets() {
		return tickets;
	}

	public void setTickets(Set<TicketFare> tickets) {
		this.tickets = tickets;
	}

	public Set<FlightPax> getFlightPaxList() {
		return flightPaxList;
	}

	public void setFlightPaxList(Set<FlightPax> flightPaxList) {
		this.flightPaxList = flightPaxList;
	}
	
    public Set<ApisMessage> getApisMessage() {
		return apisMessage;
	}

	public void setApisMessage(Set<ApisMessage> apisMessage) {
		this.apisMessage = apisMessage;
	}
	

	public void addDocument(Document d) {
        this.documents.add(d);
        d.setPassenger(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public Set<Bag> getBags() {
		return bags;
	}

	public void setBags(Set<Bag> bags) {
		this.bags = bags;
	}

	public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }
	
    public Set<Pnr> getPnrs() {
        return pnrs;
    }

    public void setPnrs(Set<Pnr> pnrs) {
        this.pnrs = pnrs;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Set<Seat> getSeatAssignments() {
        return seatAssignments;
    }

    public void setSeatAssignments(Set<Seat> seatAssignments) {
        this.seatAssignments = seatAssignments;
    }

    public Set<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Set<Attachment> attachments) {
		this.attachments = attachments;
	}

    public PassengerDetails getPassengerDetails() {
		return passengerDetails;
	}

	public void setPassengerDetails(PassengerDetails passengerDetails) {
		this.passengerDetails = passengerDetails;
	}

	public PassengerTripDetails getPassengerTripDetails() {
		return passengerTripDetails;
	}

	public void setPassengerTripDetails(PassengerTripDetails passengerTripDetails) {
		this.passengerTripDetails = passengerTripDetails;
	}

	public PassengerWLTimestamp getPassengerWLTimestamp() {
		return passengerWLTimestamp;
	}

	public void setPassengerWLTimestamp(PassengerWLTimestamp passengerWLTimestamp) {
		this.passengerWLTimestamp = passengerWLTimestamp;
	}

    public Set<PaxWatchlistLink> getPaxWatchlistLinks() {
        return paxWatchlistLinks;
    }

    public void setPaxWatchlistLinks(Set<PaxWatchlistLink> paxWatchlistLinks) {
        this.paxWatchlistLinks = paxWatchlistLinks;
    }

    public Set<HitsSummary> getHits() {
        return hits;
    }

    public void setHits(Set<HitsSummary> hits) {
        this.hits = hits;
    }

	@Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Passenger)) return false;
        Passenger passenger = (Passenger) o;
        return uuid.equals(passenger.getUuid());
    }

    public PassengerIDTag getPassengerIDTag() {
        return passengerIDTag;
    }

    public void setPassengerIDTag(PassengerIDTag passengerIDTag) {
        this.passengerIDTag = passengerIDTag;
    }

}