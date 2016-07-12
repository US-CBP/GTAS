/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "document",
    uniqueConstraints={@UniqueConstraint(columnNames={"document_type", "document_number", "issuance_date", "issuance_country"})},
    indexes = {@Index(name = "document_type_index",  columnList="document_type"),
        @Index(name = "document_number_index", columnList="document_number"),
        @Index(name="issuance_date_index", columnList="issuance_date"),
        @Index(name="issuance_country_index", columnList="issuance_country")}
)
public class Document extends BaseEntity {
    private static final long serialVersionUID = 1L;  
    public Document() { }

    @Column(name = "document_type", length = 3, nullable = false)
    private String documentType;
    
    @Column(name = "document_number", nullable = false)
    private String documentNumber;
    
    @Column(name = "expiration_date")
    @Temporal(TemporalType.DATE)      
    private Date expirationDate;
    
    @Column(name = "issuance_date")
    @Temporal(TemporalType.DATE)      
    private Date issuanceDate;
    
    @Column(name = "issuance_country")
    private String issuanceCountry;
    
    @ManyToOne
    private Passenger passenger;

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
       return Objects.hash(this.documentType, this.documentNumber, this.issuanceCountry);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final Document other = (Document)obj;
        return Objects.equals(this.documentType, other.documentType)
                && Objects.equals(this.documentNumber, other.documentNumber)
                && Objects.equals(this.issuanceCountry, other.issuanceCountry);
    }    
}
