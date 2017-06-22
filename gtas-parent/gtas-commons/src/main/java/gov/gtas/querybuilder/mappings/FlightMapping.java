/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum FlightMapping implements IEntityMapping {

    AIRPORT_DESTINATION ("destination", "Airport - Destination", TypeEnum.STRING.getType()),
    AIRPORT_ORIGIN ("origin", "Airport - Origin", TypeEnum.STRING.getType()),
    CARRIER ("carrier", "Carrier", TypeEnum.STRING.getType()),
    COUNTRY_DESTINATION ("destinationCountry", "Country - Destination", TypeEnum.STRING.getType()),
    COUNTRY_ORIGIN ("originCountry", "Country - Origin", TypeEnum.STRING.getType()),
    DIRECTION ("direction", "Direction", TypeEnum.STRING.getType()),
    ETA ("etaDate", "ETA", TypeEnum.DATE.getType()),
    ETD ("etdDate", "ETD", TypeEnum.DATE.getType()),
    FLIGHT_DATE ("flightDate", "Flight Date", TypeEnum.DATE.getType(), false),  
    FLIGHT_NUMBER ("flightNumber", "Number", TypeEnum.STRING.getType()),
    IS_OPERATING_FLIGHT("isOperatingFlight","isOperatingFlight",TypeEnum.BOOLEAN.getType()),
    IS_MARKETING_FLIGHT("isMarketingFlight","isMarketingFlight",TypeEnum.BOOLEAN.getType());
    
    private String fieldName;
    private String friendlyName;
    private String fieldType;
    private boolean displayField;
    
    private FlightMapping(String fieldName, String friendlyName, String fieldType, boolean displayField) {
        this.fieldName = fieldName;
        this.friendlyName = friendlyName;
        this.fieldType = fieldType;
        this.displayField = displayField;
    }
    private FlightMapping(String fieldName, String friendlyName, String fieldType) {
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
