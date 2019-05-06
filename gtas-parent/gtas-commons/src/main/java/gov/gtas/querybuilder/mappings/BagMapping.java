/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum BagMapping implements IEntityMapping {

    AIRLINE("airline","Airline", TypeEnum.STRING.getType()),
    BAG_IDENTIFICATION("bagId", "Bag ID", TypeEnum.STRING.getType()),
    DATA_SOURCE("data_source","Data Source", TypeEnum.STRING.getType()),
    DESTINATION("destination","Destination", TypeEnum.STRING.getType()),
    DESTINATION_COUNTRY("country","Destination Country", TypeEnum.STRING.getType()),
    DESTINATION_AIRPORT("destinationAirport","Destination - Airport",TypeEnum.STRING.getType()),
	BAG_PAX_OWNER_ID("passenger.id", "Passenger Owner Id",TypeEnum.STRING.getType(),false),
	BAG_FLIGHT_OWNER_ID("flight.id", "Flight Owner Id", TypeEnum.STRING.getType(),false),
	BAG_PRIME_FLIGHT("primeFlight", "Bag on Prime Flight", TypeEnum.BOOLEAN.getType()),
	BAG_WEIGHT("bagMeasurements.weight", "Bag measurements weight", TypeEnum.DOUBLE.getType()),
	BAG_COUNT("bagMeasurements.bagCount", "Bag Count", TypeEnum.INTEGER.getType()),
	BAG_HEAD_OF_POOL("headPool", "Bag Head Pool", TypeEnum.BOOLEAN.getType());


    private String fieldName;
    private String friendlyName;
    private String fieldType;
    private boolean displayField;
    
    private BagMapping(String fieldName, String friendlyName, String fieldType, boolean displayField) {
        this.fieldName = fieldName;
        this.friendlyName = friendlyName;
        this.fieldType = fieldType;
        this.displayField = displayField;
    }
    private BagMapping(String fieldName, String friendlyName, String fieldType) {
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
