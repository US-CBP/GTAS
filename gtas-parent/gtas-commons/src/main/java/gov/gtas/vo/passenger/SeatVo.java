/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.model.PIIObject;

public class SeatVo implements PIIObject {
	private String number;
	private Boolean apis = Boolean.valueOf(false);
	private String flightNumber;
	private String firstName;
	private String lastName;

	private Long flightId;
	private Long paxId;
	private String middleInitial;

	private boolean hasHits;

	private String refNumber;

	private String[] coTravellers;

	/**
	 * 
	 * @param seatNumber
	 * @param flightId
	 * @param paxId
	 * @param firstName
	 * @param lastName
	 * @param middleInitial
	 * @param hasHits
	 * @param coTravellers
	 */
	public SeatVo(String seatNumber, Long flightId, Long paxId, String firstName, String lastName, String middleInitial,
			boolean hasHits, String[] coTravellers, String refNumber, Boolean apis) {
		super();
		this.number = seatNumber;
		this.flightId = flightId;
		this.paxId = paxId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleInitial = middleInitial;
		this.hasHits = hasHits;
		this.coTravellers = coTravellers;
		this.refNumber = refNumber;
		this.apis = apis;
	}

	public SeatVo() {

	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Long getPaxId() {
		return paxId;
	}

	public void setPaxId(Long paxId) {
		this.paxId = paxId;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	public boolean isHasHits() {
		return hasHits;
	}

	public void setHasHits(boolean hasHits) {
		this.hasHits = hasHits;
	}

	public String[] getCoTravellers() {
		return coTravellers;
	}

	public void setCoTravellers(String[] coTravellers) {
		this.coTravellers = coTravellers;
	}

	public String getNumber() {
		return number;
	}

	public Boolean getApis() {
		return apis;
	}

	public void setApis(Boolean apis) {
		this.apis = apis;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
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

	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	@Override
	public PIIObject deletePII() {
		this.firstName = "DELETED";
		this.lastName = "DELETED";
		this.coTravellers = new String [0];
		this.middleInitial = null;
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.firstName = "MASKED";
		this.lastName = "MASKED";
		this.coTravellers = new String [0];
		this.middleInitial = null;
		return this;
	}

}
