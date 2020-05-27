package gov.gtas.vo.passenger;

import gov.gtas.model.PIIObject;
import gov.gtas.vo.BaseVo;

public class OneDayLookoutVo extends BaseVo implements PIIObject {

	private Long paxId;
	private Long caseId;
	private Long passengerId;
	private Long flightId;
	private String document;
	private String direction;
	private String disposition;
	private String lastName;
	private String firstName;
	private String flightNumber;
	private String etaEtdTime;
	private String fullFlightNumber;
	private String name;
	private String origDestAirportsStr;
	private String encounteredStatus;

	public String getEncounteredStatus() {
		return encounteredStatus;
	}

	public void setEncounteredStatus(String encounteredStatus) {
		this.encounteredStatus = encounteredStatus;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
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

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getEtaEtdTime() {
		return etaEtdTime;
	}

	public void setEtaEtdTime(String etaEtdTime) {
		this.etaEtdTime = etaEtdTime;
	}

	public String getFullFlightNumber() {
		return fullFlightNumber;
	}

	public void setFullFlightNumber(String fullFlightNumber) {
		this.fullFlightNumber = fullFlightNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPaxId() {
		return paxId;
	}

	public void setPaxId(Long paxId) {
		this.paxId = paxId;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public String getOrigDestAirportsStr() {
		return origDestAirportsStr;
	}

	public void setOrigDestAirportsStr(String origDestAirportsStr) {
		this.origDestAirportsStr = origDestAirportsStr;
	}

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	@Override
	public PIIObject deletePII() {
		this.firstName = "DELETED";
		this.lastName = "DELETED";
		this.document = "DELETED";
		this.name = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.firstName = "MASKED";
		this.lastName = "MASKED";
		this.document = "MASKED";
		this.name = "MASKED";
		return this;
	}
}
