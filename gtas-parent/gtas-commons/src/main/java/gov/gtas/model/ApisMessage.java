/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


@Entity
@Table(name = "apis_message")
public class ApisMessage extends Message {
    private static final long serialVersionUID = 1L;

    public ApisMessage() {
    }

    @Embedded
    private EdifactMessage edifactMessage;

    @ManyToMany(targetEntity = ReportingParty.class, cascade = { CascadeType.ALL })
    @JoinTable(name = "apis_message_reporting_party", joinColumns = @JoinColumn(name = "apis_message_id"), inverseJoinColumns = @JoinColumn(name = "reporting_party_id"))
    private Set<ReportingParty> reportingParties = new HashSet<>();

    @ManyToMany(fetch=FetchType.LAZY, targetEntity = Flight.class)
    @JoinTable(name = "apis_message_flight", joinColumns = @JoinColumn(name = "apis_message_id"), inverseJoinColumns = @JoinColumn(name = "flight_id"))
    private Set<Flight> flights = new HashSet<>();

    @ManyToMany(fetch=FetchType.LAZY, targetEntity = Passenger.class)
    @JoinTable(name = "apis_message_passenger", joinColumns = @JoinColumn(name = "apis_message_id"), inverseJoinColumns = @JoinColumn(name = "passenger_id"))
    private Set<Passenger> passengers = new HashSet<>();
    
    @ManyToMany(fetch=FetchType.LAZY, targetEntity = FlightPax.class, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "apis_message_flight_pax", joinColumns = @JoinColumn(name = "apis_message_id"), inverseJoinColumns = @JoinColumn(name = "flight_pax_id"))
    private Set<FlightPax> flightPaxList = new HashSet<>();
    
    
    @ManyToMany(fetch=FetchType.LAZY, targetEntity = Phone.class, cascade = { CascadeType.ALL })
    @JoinTable(name = "apis_phone", joinColumns = @JoinColumn(name = "apis_message_id"), inverseJoinColumns = @JoinColumn(name = "phone_id"))
    private Set<Phone> phones = new HashSet<>();


    @SuppressWarnings("unused")
    public Set<FlightPax> getFlightPaxList() {
		return flightPaxList;
	}

	public void setFlightPaxList(Set<FlightPax> flightPaxList) {
		this.flightPaxList = flightPaxList;
	}


	public Set<Phone> getPhones() {
		return phones;
	}

	public void setPhones(Set<Phone> phones) {
		this.phones = phones;
	}


	public void addReportingParty(ReportingParty rp) {
        if (this.reportingParties == null) {
            this.reportingParties = new HashSet<>();
        }
        this.reportingParties.add(rp);
    }

	public void addToFlightPax(FlightPax fp) {
        if (this.flightPaxList == null) {
            this.flightPaxList = new HashSet<>();
        }
        this.flightPaxList.add(fp);
    }
	
    public Set<ReportingParty> getReportingParties() {
        return reportingParties;
    }

    @SuppressWarnings("unused")
    public void setReportingParties(Set<ReportingParty> reportingParties) {
        this.reportingParties = reportingParties;
    }

    public Set<Flight> getFlights() {
        return flights;
    }

    public void setFlights(Set<Flight> flights) {
        this.flights = flights;
    }

    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }

    public EdifactMessage getEdifactMessage() {
        return edifactMessage;
    }

    public void setEdifactMessage(EdifactMessage edifactMessage) {
        this.edifactMessage = edifactMessage;
    }
}
