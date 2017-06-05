/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "passenger")
public class Passenger extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    public Passenger() {
    }

    @Column(name = "passenger_type", length = 3, nullable = false)
    private String passengerType;

    @ManyToMany(mappedBy = "passengers", targetEntity = Flight.class)
    private Set<Flight> flights = new HashSet<>();

    @ManyToMany(mappedBy = "passengers", targetEntity = ApisMessage.class)
    private Set<ApisMessage> apisMessage = new HashSet<>();

    @ManyToMany(mappedBy = "passengers", targetEntity = Pnr.class)
    private Set<Pnr> pnrs = new HashSet<>();

    private String title;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    private String suffix;

    @Column(length = 2)
    private String gender;

    @Column(name = "citizenship_country")
    private String citizenshipCountry;

    @Column(name = "residency_country")
    private String residencyCountry;

    @Temporal(TemporalType.DATE)
    private Date dob;

    /** calculated field */
    private Integer age;

    /** calculated field */
    @Column(name = "days_visa_valid")
    private Integer numberOfDaysVisaValid;
    
    private String embarkation;

    private String debarkation;

    @Column(name = "embark_country")
    private String embarkCountry;

    @Column(name = "debark_country")
    private String debarkCountry;

    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger", fetch = FetchType.EAGER)
    private Set<Document> documents = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger", fetch = FetchType.EAGER)
    private Set<Attachment> attachments = new HashSet<>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger", fetch = FetchType.EAGER)
    private Set<Seat> seatAssignments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger", fetch = FetchType.EAGER)
    private Set<HitsSummary> hits = new HashSet<>();

    @Column(name = "address")
    private String address;

    public void addDocument(Document d) {
        this.documents.add(d);
        d.setPassenger(this);
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public Set<Flight> getFlights() {
        return flights;
    }

    public void setFlights(Set<Flight> flights) {
        this.flights = flights;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(String citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public String getResidencyCountry() {
        return residencyCountry;
    }

    public void setResidencyCountry(String residencyCountry) {
        this.residencyCountry = residencyCountry;
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

    public String getEmbarkCountry() {
        return embarkCountry;
    }

    public void setEmbarkCountry(String embarkCountry) {
        this.embarkCountry = embarkCountry;
    }

    public String getDebarkCountry() {
        return debarkCountry;
    }

    public void setDebarkCountry(String debarkCountry) {
        this.debarkCountry = debarkCountry;
    }

    public Set<Document> getDocuments() {
        return documents;
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

    
    public Integer getNumberOfDaysVisaValid() {
		return numberOfDaysVisaValid;
	}

	public void setNumberOfDaysVisaValid(Integer numberOfDaysVisaValid) {
		this.numberOfDaysVisaValid = numberOfDaysVisaValid;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        // int result = super.hashCode();
        int result = 10;
        result = prime * result + ((age == null) ? 0 : age.hashCode());
        result = prime * result + ((dob == null) ? 0 : dob.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Passenger))
            return false;
        Passenger other = (Passenger) obj;
        if (age == null) {
            if (other.age != null)
                return false;
        } else if (!age.equals(other.age))
            return false;
        if (dob == null) {
            if (other.dob != null)
                return false;
        } else if (!dob.equals(other.dob))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (gender != other.gender)
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (middleName == null) {
            if (other.middleName != null)
                return false;
        } else if (!middleName.equals(other.middleName))
            return false;

        return true;
    }
}
