/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "passenger_details")
public class PassengerDetails extends BaseEntityAudit {

	@SuppressWarnings("unused")
	public PassengerDetails() {
	}

	public PassengerDetails(Passenger passenger) {
		this.passenger = passenger;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	@Column(name = "pd_passenger_id", columnDefinition = "bigint unsigned", updatable = false, insertable = false)
	private Long passengerId;

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "pd_passenger_id")
	Passenger passenger;

	@Column(name = "pd_passenger_type", length = 3, nullable = false)
	private String passengerType;

	@Column(name = "pd_title")
	private String title;

	@Column(name = "pd_first_name")
	private String firstName;

	@Column(name = "pd_middle_name")
	private String middleName;

	@Column(name = "pd_last_name")
	private String lastName;

	@Column(name = "pd_suffix")
	private String suffix;

	@Column(name = "pd_gender", length = 2)
	private String gender;

	@Column(name = "pd_nationality")
	private String nationality;

	@Column(name = "pd_residency_country")
	private String residencyCountry;

	@Temporal(TemporalType.DATE)
	private Date dob;

	/** calculated field */
	@Column(name = "pd_age")
	private Integer age;

	@Column(name = "pd_deleted", nullable = false)
	private Boolean deleted = Boolean.FALSE;

	@SuppressWarnings("unused")
	public void addApisMessage(ApisMessage apisMessage) {
	}

	public String getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
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

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	/*
	 * public PassengerIDTag getPaxIdTag() { return paxIdTag; }
	 * 
	 * public void setPaxIdTag(PassengerIDTag paxIdTag) { this.paxIdTag = paxIdTag;
	 * }
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 10;
		result = prime * result + ((passengerId == null) ? 0 : passengerId.hashCode());
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
		if (!(obj instanceof PassengerDetails))
			return false;
		PassengerDetails other = (PassengerDetails) obj;
		if (passengerId == null) {
			if (other.passengerId != null)
				return false;
		} else if (!passengerId.equals(other.getPassengerId())) {
			return false;
		}
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
		if (!gender.equals(other.gender))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (middleName == null) {
			return other.middleName == null;
		} else
			return middleName.equals(other.middleName);

	}
}