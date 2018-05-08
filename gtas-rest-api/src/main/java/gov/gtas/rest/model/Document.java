package gov.gtas.rest.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Document {
	
	private Long id;
	private String documentType;
	private String documentNumber;
	private Date expirationDate;
	private String issuanceCountry;
	private String issuanceDate;
	private Long  passengerId;

	@JsonIgnore
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getIssuanceCountry() {
		return issuanceCountry;
	}
	public void setIssuanceCountry(String issuanceCountry) {
		this.issuanceCountry = issuanceCountry;
	}
	public String getIssuanceDate() {
		return issuanceDate;
	}
	public void setIssuanceDate(String issuanceDate) {
		this.issuanceDate = issuanceDate;
	}
	@JsonIgnore
	@JsonProperty(value = "passengerId")
	public Long getPassengerId() {
		return passengerId;
	}
	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}
	
	
	
	

}
