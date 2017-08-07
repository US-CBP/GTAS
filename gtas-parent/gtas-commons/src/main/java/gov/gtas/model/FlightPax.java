package gov.gtas.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "flight_pax")
public class FlightPax implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public FlightPax(){
		
	}
	
    @Id  
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Basic(optional = false)  
    @Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")  
    private Long id;  
    
    @Column(name= "traveler_type")
    private String travelerType;
    
    @Column(name= "residence_country")
    private String residenceCountry;
    
    @ManyToOne
    @JoinColumn(name="install_address_id",nullable = true,referencedColumnName = "id")
    private Address installationAddress;
    
    @Column(name="embarkation")
    private String embarkation;
    
    @Column(name="debarkation")
    private String debarkation;
    
    @Column(name="first_arrival_port")
    private String portOfFirstArrival;

    @ManyToMany(mappedBy = "flightPaxList", targetEntity = ApisMessage.class)
    private Set<ApisMessage> apisMessage = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;
    
    
	public Set<ApisMessage> getApisMessage() {
		return apisMessage;
	}

	public void setApisMessage(Set<ApisMessage> apisMessage) {
		this.apisMessage = apisMessage;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	@Override
    public int hashCode() {
        return Objects.hash(this.debarkation,this.debarkation,this.portOfFirstArrival);
    }

    @Override
    public boolean equals(Object target) {

        if (this == target) {
            return true;
        }

        if (!(target instanceof FlightPax)) {
            return false;
        }

        FlightPax dataTarget = ((FlightPax) target);

        return ((this.getFlight().equals(dataTarget.getFlight()) && (this.getPassenger().equals(dataTarget.getPassenger()))));
    }    
}
