package gov.gtas.parsers.tamr.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TamrMessageType {
    QUERY("QUERY"),
    DC_REPLACE("DC.REPLACE"),
    TH_UPDATE("TH.UPDATE"),
    TH_CLUSTERS("TH.CLUSTERS"),
    TH_DELTAS("TH.DELTAS"),
    ERROR("ERROR");
    
    private String stringValue;
    
    private TamrMessageType(String stringValue) {
        this.stringValue = stringValue;
    }

    @JsonValue
    public String getStringValue() {
        return stringValue;
    }
    
    public String toString() {
        return this.getStringValue();
    }
    
    public static TamrMessageType fromString(String stringValue) {
        for (TamrMessageType messageType: values()) {
            if (messageType.getStringValue().equals(stringValue)) {
                return messageType;
            }
        }
        throw new IllegalArgumentException(
                "Unknown Tamr message type \"" + stringValue + "\".");
    }
}
