/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
@Cacheable
@Entity
@Table(name = "flight",
uniqueConstraints={@UniqueConstraint(columnNames={"carrier", "flight_number", "flight_date", "origin", "destination"})})
public class Flight extends BaseEntityAudit {
    private static final long serialVersionUID = 1L; 
    public Flight() { }
    
    @Column(nullable = false)
    private String carrier;
    
    @Size(min = 4, max = 4)
    @Column(name = "flight_number", length = 4, nullable = false)
    private String flightNumber;
    
    @Column(name = "marketing_flight")
    private boolean isMarketingFlight=false;
    
    @Column(name = "operating_flight")
    private boolean isOperatingFlight=false;

	public boolean isOperatingFlight() {
		return isOperatingFlight;
	}

	public void setOperatingFlight(boolean isOperatingFlight) {
		this.isOperatingFlight = isOperatingFlight;
	}

	/** combination of carrier and flight number used for reporting */
    @Column(name = "full_flight_number")
    private String fullFlightNumber;   
    
    @Column(nullable = false)
    private String origin;
    
    @Column(name = "origin_country", length = 3)
    private String originCountry;
    
    @Column(nullable = false)
    private String destination;
    
    @Column(name = "destination_country", length = 3)
    private String destinationCountry;

    /** calculated field */
    @Column(name = "flight_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date flightDate;
    
    /** calculated field */
    @Column(name = "etd_date")
    @Temporal(TemporalType.DATE)
    private Date etdDate;
    
    /** calculated field */
    @Column(name = "eta_date")
    @Temporal(TemporalType.DATE)
    private Date etaDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date etd;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date eta;
    
    @Column(name = "utc_etd")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar utcEtd;
 
    @Column(name = "utc_eta")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar utcEta;
    
    @Column(length = 1, nullable = false)
    private String direction;
    
    /*@ManyToMany(
        targetEntity=Passenger.class,
        cascade={CascadeType.ALL}
    )
    	@JoinTable(
        name="flight_passenger",
        joinColumns=@JoinColumn(name="flight_id"),
        inverseJoinColumns=@JoinColumn(name="passenger_id")
    )    
    private Set<Passenger> passengers = new HashSet<>();*/

    @OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
    private Set<HitsSummary> hits = new HashSet<>();

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "flight", fetch = FetchType.EAGER)
//    private Set<FlightPax> flightPaxDetails = new HashSet<>();


    @OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
    @JoinColumn(name = "id", unique = true, referencedColumnName = "fhr_flight_id", updatable = false, insertable = false)
    @JsonIgnore
    private FlightHitsRule flightHitsRule;

    @OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
    @JoinColumn(name = "id", unique = true, referencedColumnName = "fhw_flight_id", updatable = false, insertable = false)
    @JsonIgnore
    private FlightHitsWatchlist flightHitsWatchlist;

    @OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
    @JoinColumn(name = "id", unique = true, referencedColumnName = "fp_flight_id", updatable = false, insertable = false)
    @JsonIgnore
    private FlightPassengerCount flightPassengerCount;

    @ManyToMany(
        mappedBy = "flights",
        targetEntity = Pnr.class
    )
    private Set<Pnr> pnrs = new HashSet<>();

    // This is a convenience method to see the passengers associated with the flight.
    // Managing passengers this way is recommended against as flight passenger is manually made in the
    // loader.
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name="flight_passenger",
            joinColumns={@JoinColumn(name="flight_id")},
            inverseJoinColumns={@JoinColumn(name="passenger_id")})
    @JsonIgnore
    private Set<Passenger> passengers;

    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }

    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    public String getFullFlightNumber() {
        return fullFlightNumber;
    }
    public void setFullFlightNumber(String fullFlightNumber) {
        this.fullFlightNumber = fullFlightNumber;
    }
    public Date getFlightDate() {
        return flightDate;
    }
    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }
    public Date getEtd() {
        return etd;
    }
    public void setEtd(Date etd) {
        this.etd = etd;
    }
    public Date getEta() {
        return eta;
    }
    public void setEta(Date eta) {
        this.eta = eta;
    }
    public String getDirection() {
        return direction;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getOriginCountry() {
        return originCountry;
    }
    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public String getDestinationCountry() {
        return destinationCountry;
    }
    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }
    public Set<Pnr> getPnrs() {
        return pnrs;
    }
    public void setPnrs(Set<Pnr> pnrs) {
        this.pnrs = pnrs;
    }

    /**
     * @return the etdDate
     */
    public Date getEtdDate() {
        return etdDate;
    }

    /**
     * @param etdDate the etdDate to set
     */
    public void setEtdDate(Date etdDate) {
        this.etdDate = etdDate;
    }

    /**
     * @return the etaDate
     */
    public Date getEtaDate() {
        return etaDate;
    }

    /**
     * @param etaDate the etaDate to set
     */
    public void setEtaDate(Date etaDate) {
        this.etaDate = etaDate;
    }

    
	public boolean isMarketingFlight() {
		return isMarketingFlight;
	}

	public void setMarketingFlight(boolean isMarketingFlight) {
		this.isMarketingFlight = isMarketingFlight;
	}

        public Long getId() {
            return id;
        }

	@Override
    public int hashCode() {
       return Objects.hash(this.carrier, this.flightNumber, this.flightDate, this.origin, this.destination);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Flight))
            return false;
        final Flight other = (Flight)obj;
        return Objects.equals(this.carrier, other.carrier)
                && Objects.equals(this.flightNumber, other.flightNumber)
                && Objects.equals(this.flightDate, other.flightDate)
                && Objects.equals(this.origin, other.origin)
                && Objects.equals(this.destination, other.destination);
    }

    public FlightHitsRule getFlightHitsRule() {
        return flightHitsRule;
    }

    public FlightHitsWatchlist getFlightHitsWatchlist() {
        return flightHitsWatchlist;
    }

    public void setFlightHitsRule(FlightHitsRule flightHitsRule) {
        this.flightHitsRule = flightHitsRule;
    }

    public void setFlightHitsWatchlist(FlightHitsWatchlist flightHitsWatchlist) {
        this.flightHitsWatchlist = flightHitsWatchlist;
    }

    public FlightPassengerCount getFlightPassengerCount() {
        return flightPassengerCount;
    }

    public void setFlightPassengerCount(FlightPassengerCount flightPassengerCount) {
        this.flightPassengerCount = flightPassengerCount;
    }
}
