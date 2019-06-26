/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.validators.Validatable;

public class PassengerVo implements Validatable {

    /**
     * a unique passenger reference identifier (from PNR) used to cross
     * reference passenger information in a PNR
     */
    private String travelerReferenceNumber;

    private UUID passengerVoUUID = UUID.randomUUID();
    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String gender;
    private String nationality;
    private String residencyCountry;
    private String passengerType;
    private Integer age;
    private Date dob;
    private String embarkation;
    private String debarkation;
    private Boolean deleted = Boolean.FALSE;
    private List<DocumentVo> documents = new ArrayList<>();
    private List<SeatVo> seatAssignments = new ArrayList<>();
    private List<TicketFareVo> tickets = new ArrayList<>();
    private List<BagVo> bagVos = new ArrayList<>();
    private String address;
    private String bagId;
    private String bagNum;
    private String totalBagWeight;
    private String reservationReferenceNumber;

  	public String getTotalBagWeight() {
		return totalBagWeight;
	}

	public void setTotalBagWeight(String totalBagWeight) {
		this.totalBagWeight = totalBagWeight;
	}

	public List<TicketFareVo> getTickets() {
		return tickets;
	}

	public void setTickets(List<TicketFareVo> tickets) {
		this.tickets = tickets;
	}

	public String getReservationReferenceNumber() {
		return reservationReferenceNumber;
	}

	public void setReservationReferenceNumber(String reservationReferenceNumber) {
		this.reservationReferenceNumber = reservationReferenceNumber;
	}

	public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }

    public String getBagNum() {
        return bagNum;
    }

    public void setBagNum(String bagNum) {
        this.bagNum = bagNum;
    }

    private List<String> bags = new ArrayList<>();

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getBags() {
        return bags;
    }

    public void setBags(List<String> bags) {
        this.bags = bags;
    }

    public void addDocument(DocumentVo d) {
        documents.add(d);
    }

    public List<DocumentVo> getDocuments() {
        return documents;
    }

    public String getTravelerReferenceNumber() {
        return travelerReferenceNumber;
    }

    public void setTravelerReferenceNumber(String travelerReferenceNumber) {
        this.travelerReferenceNumber = travelerReferenceNumber;
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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getResidencyCountry() {
        return residencyCountry;
    }

    public void setResidencyCountry(String residencyCountry) {
        this.residencyCountry = residencyCountry;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
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

    public void setDocuments(List<DocumentVo> documents) {
        this.documents = documents;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<SeatVo> getSeatAssignments() {
        return seatAssignments;
    }

    public void setSeatAssignments(List<SeatVo> seatAssignments) {
        this.seatAssignments = seatAssignments;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(this.firstName) && StringUtils.isNotBlank(this.lastName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dob == null) ? 0 : dob.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
        result = prime * result + ((passengerType == null) ? 0 : passengerType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PassengerVo other = (PassengerVo) obj;
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
        if (gender == null) {
            if (other.gender != null)
                return false;
        } else if (!gender.equals(other.gender))
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
        if (passengerType == null) {
            if (other.passengerType != null)
                return false;
        } else if (!passengerType.equals(other.passengerType))
            return false;
        return true;
    }

    public List<BagVo> getBagVos() {
        return bagVos;
    }

    public void setBagVos(List<BagVo> bagVos) {
        this.bagVos = bagVos;
    }

    public UUID getPassengerVoUUID() {
        return passengerVoUUID;
    }

    public void setPassengerVoUUID(UUID passengerVoUUID) {
        this.passengerVoUUID = passengerVoUUID;
    }
}
