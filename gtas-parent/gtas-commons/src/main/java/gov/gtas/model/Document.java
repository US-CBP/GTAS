/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "document")
public class Document extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public Document() {
	}

	public Document(String documentNumber){
		this.documentNumber = documentNumber;
	}

	@Column(name = "document_type", length = 3)
	private String documentType;

	@Column(name = "document_number")
	private String documentNumber;

	@Column(name = "expiration_date")
	@Temporal(TemporalType.DATE)
	private Date expirationDate;

	@Column(name = "issuance_date")
	@Temporal(TemporalType.DATE)
	private Date issuanceDate;

	@Column(name = "issuance_country")
	private String issuanceCountry;

	@ManyToOne(fetch = FetchType.LAZY)
	private Passenger passenger;

    /** calculated field */
    @Column(name = "days_valid")
    private Integer numberOfDaysValid;
 
    
	public Integer getNumberOfDaysValid() {
		return numberOfDaysValid;
	}

	public void setNumberOfDaysValid(Integer numberOfDaysValid) {
		this.numberOfDaysValid = numberOfDaysValid;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
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

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.documentNumber, this.issuanceCountry, this.documentType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Document))
			return false;
		final Document other = (Document) obj;
		return Objects.equals(this.documentNumber, other.documentNumber)
				&& Objects.equals(this.issuanceCountry, other.issuanceCountry)
				&& Objects.equals(this.documentType, other.documentType);
	}
}
