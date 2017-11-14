package gov.gtas.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "flight_pax_pnr")
public class FlightPaxPNR implements Serializable {

    private static final long serialVersionUID = 1L;

    public FlightPaxPNR(){}

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

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

   /* @ManyToMany(targetEntity = Bag.class, cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "bag",
            joinColumns = @JoinColumn(name = "flight_pax_pnr_id"),
            inverseJoinColumns = @JoinColumn(name = "bag_id"))
    private Set<Bag> bagSet = new HashSet<Bag>();

*/
    @Column(name = "total_bag_count")
    private Integer total_bag_count;

    @Column(name = "total_bag_weight")
    private float total_bag_weight;

    @Column(name = "head_of_pool")
    private boolean headOfPool;

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

    /*public Set<Bag> getBagSet() {
        return bagSet;
    }

    public void setBagSet(Set<Bag> bagSet) {
        this.bagSet = bagSet;
    }*/

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

    public boolean isHeadOfPool() {
        return headOfPool;
    }

    public void setHeadOfPool(boolean headOfPool) {
        this.headOfPool = headOfPool;
    }

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

        return ((this.getFlight().equals(dataTarget.getFlight()) && (this.getPassenger().equals(dataTarget.getPassenger()))));
    }
}
