/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.FetchMode;

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
    Set<ReportingParty> reportingParties = new HashSet<>();

    @ManyToMany(fetch=FetchType.EAGER, targetEntity = Flight.class, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "apis_message_flight", joinColumns = @JoinColumn(name = "apis_message_id"), inverseJoinColumns = @JoinColumn(name = "flight_id"))
    private Set<Flight> flights = new HashSet<>();

    @ManyToMany(fetch=FetchType.EAGER, targetEntity = Passenger.class, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "apis_message_passenger", joinColumns = @JoinColumn(name = "apis_message_id"), inverseJoinColumns = @JoinColumn(name = "passenger_id"))
    private Set<Passenger> passengers = new HashSet<>();
    
    @Column(name= "traveler_type")
    private String travelerType;
    
    @Column(name= "residence_country")
    private String residenceCountry;
    
    @ManyToMany(fetch=FetchType.EAGER, targetEntity = Phone.class, cascade = { CascadeType.ALL })
    @JoinTable(name = "apis_phone", joinColumns = @JoinColumn(name = "apis_message_id"), inverseJoinColumns = @JoinColumn(name = "phone_id"))
    private Set<Phone> phones = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name="installation_address")
    private Address installationAddress;
    
    private String embarkation;
    private String debarkation;
    
    @Column(name="port_of_first_arrival")
    private String portOfFirstArrival; 
    
    @Column(name="bag_count")
    private int bagCount;    
    
    public int getBagCount() {
		return bagCount;
	}

	public void setBagCount(int bagCount) {
		this.bagCount = bagCount;
	}

	public String getTravelerType() {
		return travelerType;
	}

	public void setTravelerType(String travelerType) {
		this.travelerType = travelerType;
	}

	public String getResidenceCountry() {
		return residenceCountry;
	}

	public void setResidenceCountry(String residenceCountry) {
		this.residenceCountry = residenceCountry;
	}

	public Set<Phone> getPhones() {
		return phones;
	}

	public void setPhones(Set<Phone> phones) {
		this.phones = phones;
	}

	public Address getInstallationAddress() {
		return installationAddress;
	}

	public void setInstallationAddress(Address installationAddress) {
		this.installationAddress = installationAddress;
	}

	public String getEmbarkation() {
		return embarkation;
	}

	public void setEmbarkation(String embarkation) {
		this.embarkation = embarkation;
	}

	public String getDebarkation() {
		return debarkation;
	}

	public void setDebarkation(String debarkation) {
		this.debarkation = debarkation;
	}

	public String getPortOfFirstArrival() {
		return portOfFirstArrival;
	}

	public void setPortOfFirstArrival(String portOfFirstArrival) {
		this.portOfFirstArrival = portOfFirstArrival;
	}

	public void addReportingParty(ReportingParty rp) {
        if (this.reportingParties == null) {
            this.reportingParties = new HashSet<>();
        }
        this.reportingParties.add(rp);
    }

    public Set<ReportingParty> getReportingParties() {
        return reportingParties;
    }

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
