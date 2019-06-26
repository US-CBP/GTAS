package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum PassengerDetailsMapping implements IEntityMapping {

    AGE("age","Age",TypeEnum.INTEGER.getType()),
    NATIONALITY("nationality","Nationality",TypeEnum.STRING.getType()),
    DEBARKATION("debarkation","Debarkation",TypeEnum.STRING.getType()),
    DEBARKATION_COUNTRY("debarkCountry","Debarkation Country",TypeEnum.STRING.getType()),
    DOB("dob","DOB",TypeEnum.DATE.getType()),
    EMBARKATION("embarkation","Embarkation",TypeEnum.STRING.getType()),
    EMBARKATION_COUNTRY("embarkCountry","Embarkation Country",TypeEnum.STRING.getType()),
    GENDER("gender","Gender",TypeEnum.STRING.getType()),
    FIRST_NAME("firstName","First Name",TypeEnum.STRING.getType()),
    LAST_NAME("lastName","Last Name",TypeEnum.STRING.getType()),
    MIDDLE_NAME("middleName","Middle Name",TypeEnum.STRING.getType()),
    RESIDENCY_COUNTRY("residencyCountry","Residency Country",TypeEnum.STRING.getType()),
    PASSENGER_TYPE("passengerType","Type",TypeEnum.STRING.getType()),
    TRAVEL_FREQUENCY("travelFrequency","Travel Frequency",TypeEnum.STRING.getType());

    private String fieldName;
    private String friendlyName;
    private String fieldType;
    private boolean displayField;

    PassengerDetailsMapping(String fieldName, String friendlyName,
                             String fieldType, boolean displayField) {
        this.fieldName = fieldName;
        this.friendlyName = friendlyName;
        this.fieldType = fieldType;
        this.displayField = displayField;
    }

    PassengerDetailsMapping(String fieldName, String friendlyName,
                             String fieldType) {
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