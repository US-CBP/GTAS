/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.gtas.model.PIIObject;

import java.util.ArrayList;

import java.util.Date;

public class CaseVo implements PIIObject {
	private Long id;
	private Long paxId;
	private Long flightId;
	private Date flightETADate;
	private Date flightETDDate;
	private String flightDirection;
	private String flightDestination;
	private String flightOrigin;
	private String lastName;
	private String firstName;
	private String middleName;
	private String nationality;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FlightVo.SHORT_DATE_FORMAT)
	private Date dob;
	private String document;
	private String flightNumber;
	private String status;
	private ArrayList<String> hitNames;
	private String gender;
	private String docType;
	private int highPrioHitCount;
	private int medPrioHitCount;
	private int lowPrioHitCount;
	private String lookoutStatus;

	public String getlookoutStatus() { return lookoutStatus; }

	public void setlookoutStatus(String lookoutStatus) { this.lookoutStatus = lookoutStatus; }


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

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

	public Date getFlightETADate() {
		return flightETADate;
	}

	public void setFlightETADate(Date flightETADate) {
		this.flightETADate = flightETADate;
	}

	public Date getFlightETDDate() {
		return flightETDDate;
	}

	public void setFlightETDDate(Date flightETDDate) {
		this.flightETDDate = flightETDDate;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFlightDirection() {
		return flightDirection;
	}

	public void setFlightDirection(String flightDirection) {
		this.flightDirection = flightDirection;
	}

  public Long getPaxId() {
		return paxId;
	}

	public void setPaxId(Long paxId) {
		this.paxId = paxId;
	}

	public void setHitNames(ArrayList<String> hitNames) {
		this.hitNames = hitNames;
	}

	public ArrayList<String> getHitNames() {
		return this.hitNames;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGender() {
		return gender;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocType() {
		return this.docType;
	}

	public String getFlightDestination() {
		return flightDestination;
	}

	public void setFlightDestination(String flightDestination) {
		this.flightDestination = flightDestination;
	}

	public String getFlightOrigin() {
		return flightOrigin;
	}

	public void setFlightOrigin(String flightOrigin) {
		this.flightOrigin = flightOrigin;
	}

	public int getHighPrioHitCount() { return highPrioHitCount; }

	public void setHighPrioHitCount(int highPrioHitCount) { this.highPrioHitCount = highPrioHitCount; }

	public int getMedPrioHitCount() { return medPrioHitCount; }

	public void setMedPrioHitCount(int medPrioHitCount) { this.medPrioHitCount = medPrioHitCount; }

	public int getLowPrioHitCount() { return lowPrioHitCount; }

	public void setLowPrioHitCount(int lowPrioHitCount) { this.lowPrioHitCount = lowPrioHitCount; }

	@Override
	public PIIObject deletePII() {
		this.dob = null;
		this.document = null;
		this.firstName = "DELETED";
		this.lastName = "DELETED";
		this.middleName = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.document = "MASKED";
		this.firstName = "MASKED";
		this.lastName = "MASKED";
		this.middleName = "MASKED";
		return this;
	}
}
