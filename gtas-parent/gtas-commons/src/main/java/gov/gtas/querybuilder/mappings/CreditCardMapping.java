/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum CreditCardMapping implements IEntityMapping {

    ACCOUNT_HOLDER ("accountHolder", "Account Holder", TypeEnum.STRING.getType()),
    EXPIRATION_DATE ("expiration", "Expiration Date", TypeEnum.DATE.getType()),
    CREDIT_CARD_NUMBER ("number", "Number", TypeEnum.STRING.getType()),
    CREDIT_CARD_TYPE ("cardType", "Type", TypeEnum.STRING.getType());
    
    private String fieldName;
    private String friendlyName;
    private String fieldType;
    private boolean displayField;
    
    private CreditCardMapping(String fieldName, String friendlyName,
            String fieldType, boolean displayField) {
        this.fieldName = fieldName;
        this.friendlyName = friendlyName;
        this.fieldType = fieldType;
        this.displayField = displayField;
    }
    private CreditCardMapping(String fieldName, String friendlyName,
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
