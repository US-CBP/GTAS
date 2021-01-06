package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum  SavedSegmentMapping implements IEntityMapping {

    REGEX("regex", "regex",TypeEnum.STRING.getType()),
    RAW_SEGMENT("rawSegment", "rawSegment",TypeEnum.STRING.getType() ),
    SEGMENT_NAME("segmentName", "segmentName",TypeEnum.STRING.getType() );

    private String fieldName;
    private String friendlyName;
    private String fieldType;
    private boolean displayField;

    private SavedSegmentMapping(String fieldName, String friendlyName, String fieldType, boolean displayField) {
        this.fieldName = fieldName;
        this.friendlyName = friendlyName;
        this.fieldType = fieldType;
        this.displayField = displayField;
    }

    private SavedSegmentMapping(String fieldName, String friendlyName, String fieldType) {
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
