package gov.gtas.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "flight_pax_apis")
public class FlightPaxAPIs implements Serializable {

    private static final long serialVersionUID = 1L;

    public FlightPaxAPIs(){}

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


    @Column(name = "total_bag_count")
    private Integer total_bag_count;

    @Column(name = "total_bag_weight")
    private float total_bag_weight;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @OneToMany(targetEntity = Bag.class, cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "flight_pax_apis_bag",
            joinColumns = @JoinColumn(name = "flight_pax_apis_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private Set<Bag> bagSet = new HashSet<Bag>();


    public Integer getTotal_bag_count() {
        return total_bag_count;
    }

    public void setTotal_bag_count(Integer total_bag_count) {
        this.total_bag_count = total_bag_count;
    }

    public float getTotal_bag_weight() {
        return total_bag_weight;
    }

    public void setTotal_bag_weight(float total_bag_weight) {
        this.total_bag_weight = total_bag_weight;
    }

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

/*
    public Set<Bag> getBagSet() {
        return bagSet;
    }

    public void setBagSet(Set<Bag> bagSet) {
        this.bagSet = bagSet;
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FlightPaxAPIs that = (FlightPaxAPIs) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(embarkation, that.embarkation)
                .append(debarkation, that.debarkation)
                .append(flight, that.flight)
                .append(passenger, that.passenger)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(embarkation)
                .append(debarkation)
                .append(flight)
                .append(passenger)
                .toHashCode();
    }
}
