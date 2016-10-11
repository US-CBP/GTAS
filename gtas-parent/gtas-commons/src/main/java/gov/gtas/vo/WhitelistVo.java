/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

import java.util.Date;

/**
 * The Class WhitelistVo.
 */
public class WhitelistVo {

	private Long id;

	private String firstName;

	private String middleName;

	private String lastName;

	private String gender;

	private Date dob;

	private String citizenshipCountry;

	private String residencyCountry;

	private String documentType;

	private String documentNumber;

	private Date expirationDate;

	private Date issuanceDate;

	private String issuanceCountry;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getIssuanceDate() {
		return issuanceDate;
	}

	public void setIssuanceDate(Date issuanceDate) {
		this.issuanceDate = issuanceDate;
	}

	public String getIssuanceCountry() {
		return issuanceCountry;
	}

	public void setIssuanceCountry(String issuanceCountry) {
		this.issuanceCountry = issuanceCountry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((citizenshipCountry == null) ? 0 : citizenshipCountry
						.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result
				+ ((documentNumber == null) ? 0 : documentNumber.hashCode());
		result = prime * result
				+ ((documentType == null) ? 0 : documentType.hashCode());
		result = prime * result
				+ ((expirationDate == null) ? 0 : expirationDate.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result
				+ ((issuanceCountry == null) ? 0 : issuanceCountry.hashCode());
		result = prime * result
				+ ((issuanceDate == null) ? 0 : issuanceDate.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((middleName == null) ? 0 : middleName.hashCode());
		result = prime
				* result
				+ ((residencyCountry == null) ? 0 : residencyCountry.hashCode());
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
		WhitelistVo other = (WhitelistVo) obj;
		if (citizenshipCountry == null) {
			if (other.citizenshipCountry != null)
				return false;
		} else if (!citizenshipCountry.equals(other.citizenshipCountry))
			return false;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (documentNumber == null) {
			if (other.documentNumber != null)
				return false;
		} else if (!documentNumber.equals(other.documentNumber))
			return false;
		if (documentType == null) {
			if (other.documentType != null)
				return false;
		} else if (!documentType.equals(other.documentType))
			return false;
		if (expirationDate == null) {
			if (other.expirationDate != null)
				return false;
		} else if (!expirationDate.equals(other.expirationDate))
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
		if (issuanceCountry == null) {
			if (other.issuanceCountry != null)
				return false;
		} else if (!issuanceCountry.equals(other.issuanceCountry))
			return false;
		if (issuanceDate == null) {
			if (other.issuanceDate != null)
				return false;
		} else if (!issuanceDate.equals(other.issuanceDate))
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
		if (residencyCountry == null) {
			if (other.residencyCountry != null)
				return false;
		} else if (!residencyCountry.equals(other.residencyCountry))
			return false;
		return true;
	}

}
