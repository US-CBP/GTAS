package gov.gtas.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "flight_pax")
public class FlightPax implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public FlightPax(){}
	
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

    @Column(name = "ref_number")
    private String reservationReferenceNumber;
 
    @Column(name = "emb_country")
    private String embarkationCountry;
    
    @Column(name = "deb_country")
    private String debarkationCountry;

	@Column(name = "msg_source")
	private String messageSource;

	@Column(name = "bag_count")
	private Integer bagCount;

	@Column(name = "bag_weight")
	private double bagWeight;

	@Column(name = "average_bag_weight")
	private double averageBagWeight;

	@Column(name = "head_of_pool", nullable = false)
	private boolean headOfPool=false;

	@ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;
    
    @ManyToMany(mappedBy = "flightPaxes",targetEntity = BookingDetail.class) 
    private Set<BookingDetail> bookingDetails = new HashSet<>();
    
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

	public String getReservationReferenceNumber() {
		return reservationReferenceNumber;
	}

	public void setReservationReferenceNumber(String reservationReferenceNumber) {
		this.reservationReferenceNumber = reservationReferenceNumber;
	}

	public String getEmbarkationCountry() {
		return embarkationCountry;
	}

	public void setEmbarkationCountry(String embarkationCountry) {
		this.embarkationCountry = embarkationCountry;
	}

	public String getDebarkationCountry() {
		return debarkationCountry;
	}

	public void setDebarkationCountry(String debarkationCountry) {
		this.debarkationCountry = debarkationCountry;
	}

	public Integer getBagCount() {
		return bagCount;
	}

	public void setBagCount(Integer bagCount) {
		this.bagCount = bagCount;
	}

	public double getBagWeight() {
		return bagWeight;
	}

	public void setBagWeight(double bagWeight) {
		this.bagWeight = bagWeight;
	}

	public boolean isHeadOfPool() {
		return headOfPool;
	}

	public void setHeadOfPool(boolean headOfPool) {
		this.headOfPool = headOfPool;
	}

	public String getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(String messageSource) {
		this.messageSource = messageSource;
	}

	public double getAverageBagWeight() {
		return averageBagWeight;
	}

	public void setAverageBagWeight(double averageBagWeight) {
		this.averageBagWeight = averageBagWeight;
	}
/**
	@Override
    public int hashCode() {
        return Objects.hash(this.debarkation,this.embarkation,this.portOfFirstArrival);
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

        return ((this.getFlight().getId().equals(dataTarget.getFlight().getId()) && 
        		(this.getPassenger().getId().equals(dataTarget.getPassenger().getId()))));
    }  
    **/  
}
