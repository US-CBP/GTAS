package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum DwellTimeMapping implements IEntityMapping {
	
    LOCATION ("location", "Location", TypeEnum.STRING.getType()),
    DWELL_TIME ("dwellTime", "DwellTime", TypeEnum.DOUBLE.getType());
    
    private String fieldName;
    private String friendlyName;
    private String fieldType;
    private boolean displayField;
    
    private DwellTimeMapping(String fieldName, String friendlyName, String fieldType, boolean displayField) {
        this.fieldName = fieldName;
        this.friendlyName = friendlyName;
        this.fieldType = fieldType;
        this.displayField = displayField;
    }
    private DwellTimeMapping(String fieldName, String friendlyName, String fieldType) {
        this(fieldName,  friendlyName, fieldType, true);
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
