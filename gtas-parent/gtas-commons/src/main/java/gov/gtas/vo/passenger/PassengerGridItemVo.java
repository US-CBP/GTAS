/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.vo.passenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.gtas.model.PIIObject;
import gov.gtas.vo.BaseVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PassengerGridItemVo extends BaseVo implements PIIObject {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";

	private String firstName;
	private String middleName;
	private String lastName;
	private String suffix;
	private String gender;
	private String nationality;
	private String passengerType;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SHORT_DATE_FORMAT)
	private Date dob;
	private String paxId;
	private String seat;

	// flight info
	private String flightId;
	private String flightNumber;
	private String fullFlightNumber;
	private String carrier;
	private Date etd;
	private Date eta;
	private String flightOrigin;
	private String flightDestination;

	// hits info
	private Boolean onRuleHitList = Boolean.FALSE;
	private Boolean onGraphHitList = Boolean.FALSE;
	private Boolean onWatchList = Boolean.FALSE;
	private Boolean onWatchListDoc = Boolean.FALSE;
	private Boolean onWatchListLink = Boolean.FALSE;

	private List<DocumentVo> documents = new ArrayList<>();

	public void addDocument(DocumentVo d) {
		documents.add(d);
	}

	public List<DocumentVo> getDocuments() {
		return documents;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getFlightOrigin() {
		return flightOrigin;
	}

	public void setFlightOrigin(String flightOrigin) {
		this.flightOrigin = flightOrigin;
	}

	public String getFlightDestination() {
		return flightDestination;
	}

	public void setFlightDestination(String flightDestination) {
		this.flightDestination = flightDestination;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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

	public String getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getPaxId() {
		return paxId;
	}

	public void setPaxId(String paxId) {
		this.paxId = paxId;
	}

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

	public String getFullFlightNumber() {
		return fullFlightNumber;
	}

	public void setFullFlightNumber(String fullFlightNumber) {
		this.fullFlightNumber = fullFlightNumber;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public Date getEtd() {
		return etd;
	}

	public void setEtd(Date etd) {
		this.etd = etd;
	}

	public Date getEta() {
		return eta;
	}

	public void setEta(Date eta) {
		this.eta = eta;
	}

	public Boolean getOnRuleHitList() {
		return onRuleHitList;
	}

	public void setOnRuleHitList(Boolean onRuleHitList) {
		this.onRuleHitList = onRuleHitList;
	}

	public Boolean getOnGraphHitList() {
		return onGraphHitList;
	}

	public void setOnGraphHitList(Boolean onGraphHitList) {
		this.onGraphHitList = onGraphHitList;
	}

	public Boolean getOnWatchList() {
		return onWatchList;
	}

	public void setOnWatchList(Boolean onWatchList) {
		this.onWatchList = onWatchList;
	}

	public Boolean getOnWatchListDoc() {
		return onWatchListDoc;
	}

	public void setOnWatchListDoc(Boolean onWatchListDoc) {
		this.onWatchListDoc = onWatchListDoc;
	}

	public Boolean getOnWatchListLink() {
		return onWatchListLink;
	}

	public void setOnWatchListLink(Boolean onWatchListLink) {
		this.onWatchListLink = onWatchListLink;
	}

	public void setDocuments(List<DocumentVo> documents) {
		this.documents = documents;
	}

	@Override
	public PIIObject deletePII() {
		this.dob = null;
		this.firstName = "DELETED";
		this.lastName = "DELETED";
		this.middleName = "DELETED";
		this.nationality = "DELETED";
		for (DocumentVo documentVo : documents) {
			documentVo.deletePII();
		}

		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.dob = null;
		this.firstName = "MASKED";
		this.lastName = "MASKED";
		this.middleName = "MASKED";
		this.nationality = "MASKED";
		for (DocumentVo documentVo : documents) {
			documentVo.maskPII();
		}
		return this;
	}
}
