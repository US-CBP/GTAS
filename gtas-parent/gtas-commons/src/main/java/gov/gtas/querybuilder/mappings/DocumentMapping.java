/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum DocumentMapping implements IEntityMapping {

    ISSUANCE_COUNTRY ("issuanceCountry", "Issuance Country", TypeEnum.STRING.getType()),
    EXPIRATION_DATE ("expirationDate", "Expiration Date", TypeEnum.DATE.getType()),
    ISSUANCE_DATE ("issuanceDate", "Issuance Date", TypeEnum.DATE.getType()),
    DOCUMENT_NUMBER ("documentNumber", "Number", TypeEnum.STRING.getType()),
    DOCUMENT_TYPE ("documentType", "Type", TypeEnum.STRING.getType()),
    DOCUMENT_OWNER_ID ("passenger.id", "Owner Id", TypeEnum.STRING.getType(), false);
    
    private String fieldName;
    private String friendlyName;
    private String fieldType;
    private boolean displayField;
    
    private DocumentMapping(String fieldName, String friendlyName, String fieldType, boolean displayField) {
        this.fieldName = fieldName;
        this.friendlyName = friendlyName;
        this.fieldType = fieldType;
        this.displayField = displayField;
    }
    private DocumentMapping(String fieldName, String friendlyName, String fieldType) {
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
