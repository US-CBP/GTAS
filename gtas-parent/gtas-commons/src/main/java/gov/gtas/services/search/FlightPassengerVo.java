/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import gov.gtas.model.Address;
import gov.gtas.model.Document;
import gov.gtas.model.PIIObject;
import gov.gtas.vo.passenger.AddressVo;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.FlightVo;

public class FlightPassengerVo implements PIIObject {
	// flight
	private Long flightId;
	private String carrier;
	private String flightNumber;
	private String origin;
	private String originLocation;
	private String destination;
	private String destinationLocation;
	private Date flightDate;

	private Date etd;
	private Date eta;

	// pax
	private Long passengerId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String gender;
	private String nationality;
	private String residencyCountry;
	private String passengerType;
	private Date dob;
	private Set<Address> addresses;
	private Set<DocumentVo> documents;
	private String embarkation;
	private String debarkation;
	private AddressVo installationAddress;
	private String portOfFirstArrival;
	private String resRefNumber;

	private String apis;
	private String pnr;

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Date getFlightDate() {
		return flightDate;
	}

	public void setFlightDate(Date flightDate) {
		this.flightDate = flightDate;
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

	public String getOriginLocation() {
		return originLocation;
	}

	public void setOriginLocation(String originLocation) {
		this.originLocation = originLocation;
	}

	public String getDestinationLocation() {
		return destinationLocation;
	}

	public void setDestinationLocation(String destinationLocation) {
		this.destinationLocation = destinationLocation;
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

	public String getApis() {
		return apis;
	}

	public void setApis(String apis) {
		this.apis = apis;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> set) {
		this.addresses = set;
	}

	public Set<DocumentVo> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<DocumentVo> documents) {
		this.documents = documents;
	}

	public void addDocument(DocumentVo document) {
		this.documents.add(document);
	}

	public String getEmbarkation() {
		return embarkation;
	}

	public void setEmbarkation(String embarkation) {
		this.embarkation = embarkation;
	}

	public String getDebarkation() {
		return debarkation;
	}

	public void setDebarkation(String debarkation) {
		this.debarkation = debarkation;
	}

	public AddressVo getInstallationAddress() {
		return installationAddress;
	}

	public void setInstallationAddress(AddressVo installationAddress) {
		this.installationAddress = installationAddress;
	}

	public String getPortOfFirstArrival() {
		return portOfFirstArrival;
	}

	public void setPortOfFirstArrival(String portOfFirstArrival) {
		this.portOfFirstArrival = portOfFirstArrival;
	}

	public String getResRefNumber() {
		return resRefNumber;
	}

	public void setResRefNumber(String resRefNumber) {
		this.resRefNumber = resRefNumber;
	}

	@Override
	public PIIObject deletePII() {

		this.addresses = null;
		if (documents != null) {
			for (DocumentVo documentVo : documents) {
				documentVo.deletePII();
			}
		}
		this.dob = null;
		this.firstName = "DELETED";
		this.lastName = "DELETED";
		this.nationality = "DELETED";
		this.middleName = "DELETED";

		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.addresses = null;
		if (documents != null) {
			for (DocumentVo documentVo : documents) {
				documentVo.deletePII();
			}
		}
		this.dob = null;
		this.firstName = "MASKED";
		this.lastName = "MASKED";
		this.nationality = "MASKED";
		this.middleName = "MASKED";

		return this;
	}
}
