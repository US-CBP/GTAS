/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum PassengerMapping implements IEntityMapping {

	AGE("passengerDetails.age", "Age", TypeEnum.INTEGER.getType()),

	NATIONALITY("passengerDetails.nationality", "Nationality", TypeEnum.STRING.getType()),

	DEBARKATION("passengerTripDetails.debarkation", "Debarkation", TypeEnum.STRING.getType()),

	DEBARKATION_COUNTRY("passengerTripDetails.debarkCountry", "Debarkation Country", TypeEnum.STRING.getType()),

	DOB("passengerDetails.dob", "DOB", TypeEnum.DATE.getType()),

	EMBARKATION("passengerTripDetails.embarkation", "Embarkation", TypeEnum.STRING.getType()),

	EMBARKATION_COUNTRY("passengerTripDetails.embarkCountry", "Embarkation Country", TypeEnum.STRING.getType()),

	GENDER("passengerDetails.gender", "Gender", TypeEnum.STRING.getType()),

	FIRST_NAME("passengerDetails.firstName", "First Name", TypeEnum.STRING.getType()),

	LAST_NAME("passengerDetails.lastName", "Last Name", TypeEnum.STRING.getType()),

	MIDDLE_NAME("passengerDetails.middleName", "Middle Name", TypeEnum.STRING.getType()),

	RESIDENCY_COUNTRY("passengerDetails.residencyCountry", "Residency Country", TypeEnum.STRING.getType()),

	APIS_CO_TRAVELERS("passengerTripDetails.coTravelerCount", "APIS Co Travelers", TypeEnum.INTEGER.getType()),

	HOURS_BEFORE_TAKE_OFF("passengerTripDetails.hoursBeforeTakeOff", "Hours Before Take Off",
			TypeEnum.INTEGER.getType()),

	SEAT("seat", "Seat", TypeEnum.STRING.getType()),

	PASSENGER_TYPE("passengerDetails.passengerType", "Type", TypeEnum.STRING.getType()),
	
	DAYS_OUT_OF_COUNTRY("passengerTripDetails.daysOutOfCountry", "Days out of Country", TypeEnum.DOUBLE.getType()),

	TRAVEL_FREQUENCY("passengerTripDetails.travelFrequency", "Travel Frequency", TypeEnum.STRING.getType());

	private String fieldName;
	private String friendlyName;
	private String fieldType;
	private boolean displayField;

	private PassengerMapping(String fieldName, String friendlyName, String fieldType, boolean displayField) {
		this.fieldName = fieldName;
		this.friendlyName = friendlyName;
		this.fieldType = fieldType;
		this.displayField = displayField;
	}

	private PassengerMapping(String fieldName, String friendlyName, String fieldType) {
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
