package gov.gtas.rest.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Cases {

	private Long id;
	private Date createdAt;
	private Date  updatedAt;
	private String description;
	private Date dob;
	private String citizenshipCountry;
	private String document;
	private String firstName;
	private String etaDate;
	private String etdDate;
	private String flightId;
	private String flightNumber;
	private String highPriorityRuleCatId;
	private String highPriorityRuleCatDescription;
	private String lastName;
	private String paxId;
	private String passengerName;
	private String passengerType;
	private String status;
	private List<CaseHitDisposition> caseHitDisposition;
	
	@JsonIgnore
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public String getEtaDate() {
		return etaDate;
	}
	public void setEtaDate(String etaDate) {
		this.etaDate = etaDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public String getEtdDate() {
		return etdDate;
	}
	public void setEtdDate(String etdDate) {
		this.etdDate = etdDate;
	}
	@JsonIgnore
	@JsonProperty(value = "flightId")
	public String getFlightId() {
		return flightId;
	}
	public void setFlightId(String flightId) {
		this.flightId = flightId;
	}
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	@JsonIgnore
	@JsonProperty(value = "highPriorityRuleCatId")
	public String getHighPriorityRuleCatId() {
		return highPriorityRuleCatId;
	}
	public void setHighPriorityRuleCatId(String highPriorityRuleCatId) {
		this.highPriorityRuleCatId = highPriorityRuleCatId;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@JsonIgnore
	@JsonProperty(value = "paxId")
	public String getPaxId() {
		return paxId;
	}
	public void setPaxId(String paxId) {
		this.paxId = paxId;
	}
	public String getPassengerName() {
		return passengerName;
	}
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
	public String getPassengerType() {
		return passengerType;
	}
	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getHighPriorityRuleCatDescription() {
		return highPriorityRuleCatDescription;
	}
	public void setHighPriorityRuleCatDescription(String highPriorityRuleCatDescription) {
		this.highPriorityRuleCatDescription = highPriorityRuleCatDescription;
	}
	public String getCitizenshipCountry() {
		return citizenshipCountry;
	}
	public void setCitizenshipCountry(String citizenshipCountry) {
		this.citizenshipCountry = citizenshipCountry;
	}
	public List<CaseHitDisposition> getCaseHitDisposition() {
		return caseHitDisposition;
	}
	public void setCaseHitDisposition(List<CaseHitDisposition> caseHitDisposition) {
		this.caseHitDisposition = caseHitDisposition;
	}
	
	


}
