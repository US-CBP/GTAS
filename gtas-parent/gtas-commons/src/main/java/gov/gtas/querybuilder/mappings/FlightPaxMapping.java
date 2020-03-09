/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum FlightPaxMapping implements IEntityMapping {

	FIRST_ARRIVAL_PORT("portOfFirstArrival", "First Arrival Port", TypeEnum.STRING.getType()),

	RESIDENCE_COUNTRY("residenceCountry", "Residence Country", TypeEnum.STRING.getType()),

	INSTALLATION_ADDRESS("installationAddress", "Installation Address", TypeEnum.STRING.getType()),

	EMBARKATION_AIRPORT("embarkation", "Embarkation - Airport", TypeEnum.STRING.getType()),

	DEBARKATION_AIRPORT("debarkation", "Debarkation - Airport", TypeEnum.STRING.getType()),

	RESERVATION_REF_NUMBER("reservationReferenceNumber", "Reservation Reference Number", TypeEnum.STRING.getType()),

	EMBARKATION_COUNTRY("embarkationCountry", "Embarkation - Country", TypeEnum.STRING.getType()),

	DEBARKATION_COUNTRY("debarkationCountry", "Debarkation - Country", TypeEnum.STRING.getType()),

	BAG_WEIGHT("bagWeight", "Bag - Weight", TypeEnum.DOUBLE.getType()),

	BAG_COUNT("bagCount", "Bag - Count", TypeEnum.INTEGER.getType()),

	AVERAGE_BAG_WEIGHT("averageBagWeight", "Bag - Average Weight", TypeEnum.DOUBLE.getType()),

	HEAD_OF_POOL("headOfPool", "Is Head Of Pool", TypeEnum.BOOLEAN.getType()),

	FLIGHT_PAX_PAX_OWNER_ID("passengerId", "Passenger Owner Id", TypeEnum.INTEGER.getType(), false),

	FLIGHT_PAX_FLIGHT_OWNER_ID("flightId", "", TypeEnum.INTEGER.getType(), false);

	private String fieldName;
	private String friendlyName;
	private String fieldType;
	private boolean displayField;

	private FlightPaxMapping(String fieldName, String friendlyName, String fieldType, boolean displayField) {
		this.fieldName = fieldName;
		this.friendlyName = friendlyName;
		this.fieldType = fieldType;
		this.displayField = displayField;
	}

	private FlightPaxMapping(String fieldName, String friendlyName, String fieldType) {
		this(fieldName, friendlyName, fieldType, true);
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @return the displayField
	 */
	public boolean isDisplayField() {
		return displayField;
	}

}
