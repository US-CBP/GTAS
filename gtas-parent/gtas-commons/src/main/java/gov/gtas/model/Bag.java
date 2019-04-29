/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "bag")
public class Bag extends BaseEntity {
    private static final long serialVersionUID = 1L;

	public Bag() {
    }

    @Column(name = "bag_identification", nullable = false)
    private String bagId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight_id",referencedColumnName = "id", nullable = false)
	private Flight flight;

	@Column(name = "data_source")
    private String data_source;

	@Column(name = "destination_city")
	private String destination;

	@Column(name = "destination_country")
	private String country;

	@ManyToOne()
	@JoinColumn(name = "passenger_id", nullable = false, insertable = false, updatable = false)
	private Passenger passenger;

	@Column(name = "passenger_id", columnDefinition = "bigint unsigned")
	private Long passengerId;

	@Column(name = "destination_airport")
	private String destinationAirport;

    @Column(name = "airline")
	private String airline;
    
    @Column(name = "headpool")
	private boolean headPool=false;

	@Column(name = "memberpool")
	private boolean memberPool = false;

    @Column(name = "primeFlight")
	private boolean primeFlight;

    @ManyToOne()
	@JoinColumn(name = "bagMeasurements")
	private BagMeasurements bagMeasurements;

	@ManyToMany(fetch = FetchType.LAZY ,targetEntity = BookingDetail.class)
	@JoinTable(name = "bag_bd_join", joinColumns = @JoinColumn(name = "bag_id"), inverseJoinColumns = @JoinColumn(name = "bd_id"))
	private Set<BookingDetail> bookingDetail = new HashSet<>();

	@Column(name = "bag_serial_count")
	private String bagSerialCount;

	@Transient
	private UUID parserUUID;

	@Transient
	private Set<UUID> flightVoUUID = new HashSet<>();

	public Set<UUID> getFlightVoUUID() {
		return flightVoUUID;
	}

	public void setFlightVoUUID(Set<UUID> flightVoUUID) {
		this.flightVoUUID = flightVoUUID;
	}

	public boolean isMemberPool() {
		return memberPool;
	}

	public void setMemberPool(boolean memberPool) {
		this.memberPool = memberPool;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBagSerialCount() {
		return bagSerialCount;
	}

	public void setBagSerialCount(String bagSerialCount) {
		this.bagSerialCount = bagSerialCount;
	}
	public UUID getParserUUID() {
		return parserUUID;
	}

	public void setParserUUID(UUID parserUUID) {
		this.parserUUID = parserUUID;
	}
	public boolean isHeadPool() {
		return headPool;
	}

	public void setHeadPool(boolean headPool) {
		this.headPool = headPool;
	}

	public String getDestinationAirport() {
		return destinationAirport;
	}

	public void setDestinationAirport(String destinationAirport) {
		this.destinationAirport = destinationAirport;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getData_source() {
		return data_source;
	}

	public void setData_source(String data_source) {
		this.data_source = data_source;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }
    @Override
	public String toString() {

        return "\nbagid " +
                bagId +
                "\npass id " +
                passenger.getId() +
                "\nflight id " +
                flight.getId() +
                "\ndata source " +
                data_source +
                "\nDestination " +
                destination +
                "\nDestination airport " +
                destinationAirport +
                "\nAirline " +
                airline;
	}


    public void setBookingDetail(Set<BookingDetail> bookingDetail) {
        this.bookingDetail = bookingDetail;
    }

    public Set<BookingDetail> getBookingDetail() {
        return bookingDetail;
    }

	public boolean isPrimeFlight() {
		return primeFlight;
	}

	public void setPrimeFlight(boolean primeFlight) {
		this.primeFlight = primeFlight;
	}

	public BagMeasurements getBagMeasurements() {
		return bagMeasurements;
	}

	public void setBagMeasurements(BagMeasurements bagMeasurements) {
		this.bagMeasurements = bagMeasurements;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Bag)) return false;
		Bag bag = (Bag) o;
		return isHeadPool() == bag.isHeadPool() &&
				isMemberPool() == bag.isMemberPool() &&
				isPrimeFlight() == bag.isPrimeFlight() &&
				Objects.equals(getBagId(), bag.getBagId()) &&
				Objects.equals(getData_source(), bag.getData_source()) &&
				Objects.equals(getDestination(), bag.getDestination()) &&
				Objects.equals(getPassengerId(), bag.getPassengerId()) &&
				Objects.equals(getDestinationAirport(), bag.getDestinationAirport()) &&
				Objects.equals(getBagSerialCount(), bag.getBagSerialCount()) &&
				Objects.equals(getAirline(), bag.getAirline());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getBagId(),
				getData_source(),
				getPassengerId(),
				getBagSerialCount());
	}
}
