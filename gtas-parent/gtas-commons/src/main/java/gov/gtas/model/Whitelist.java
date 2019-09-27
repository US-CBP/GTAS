/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import gov.gtas.enumtype.YesNoEnum;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The Class Whitelist.
 */
@Entity
@Table(name = "white_list")
public class Whitelist extends BaseEntityAudit {
	private static final long serialVersionUID = 1;

	/**
	 * Instantiates a new whitelist.
	 */
	public Whitelist() {
	}

	@Enumerated(value = EnumType.STRING)
	@Column(name = "DEL_FLAG", nullable = false, length = 1)
	private YesNoEnum deleted;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	@Column(length = 2)
	private String gender;

	@Temporal(value = TemporalType.DATE)
	private Date dob;

	@Column(name = "nationality")
	private String nationality;

	@Column(name = "residency_country")
	private String residencyCountry;

	@Column(name = "document_type", length = 3, nullable = false)
	private String documentType;

	@Column(name = "document_number", nullable = false)
	private String documentNumber;

	@Column(name = "expiration_date")
	@Temporal(value = TemporalType.DATE)
	private Date expirationDate;

	@Column(name = "issuance_date")
	@Temporal(value = TemporalType.DATE)
	private Date issuanceDate;

	@Column(name = "issuance_country")
	private String issuanceCountry;

	@ManyToOne
	@JoinColumn(name = "editor", referencedColumnName = "user_id", nullable = false)
	private User whiteListEditor;

	public YesNoEnum getDeleted() {
		return deleted;
	}

	public void setDeleted(YesNoEnum deleted) {
		this.deleted = deleted;
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

	public User getWhiteListEditor() {
		return whiteListEditor;
	}

	public void setWhiteListEditor(User whiteListEditor) {
		this.whiteListEditor = whiteListEditor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((nationality == null) ? 0 : nationality.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((documentNumber == null) ? 0 : documentNumber.hashCode());
		result = prime * result + ((documentType == null) ? 0 : documentType.hashCode());
		result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((issuanceCountry == null) ? 0 : issuanceCountry.hashCode());
		result = prime * result + ((issuanceDate == null) ? 0 : issuanceDate.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
		result = prime * result + ((residencyCountry == null) ? 0 : residencyCountry.hashCode());
		result = prime * result + ((whiteListEditor == null) ? 0 : whiteListEditor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Whitelist other = (Whitelist) obj;
		if (nationality == null) {
			if (other.nationality != null)
				return false;
		} else if (!nationality.equals(other.nationality))
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
		if (whiteListEditor == null) {
			if (other.whiteListEditor != null)
				return false;
		} else if (!whiteListEditor.equals(other.whiteListEditor))
			return false;
		return true;
	}
}
