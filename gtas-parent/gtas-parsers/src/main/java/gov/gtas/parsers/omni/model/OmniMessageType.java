/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OmniMessageType {
    ASSESS_RISK_REQUEST("ASSESS_RISK_REQUEST"),
    ASSESS_RISK_RESPONSE("ASSESS_RISK_RESPONSE"),
    UPDATE_DEROG_CATEGORY("UPDATE_DEROG_CATEGORY"),
    UPDATE_DEROG_LAST_RUN_REQUEST("UPDATE_DEROG_LAST_RUN_REQUEST"),
    UPDATE_DEROG_LAST_RUN_RESPONSE("UPDATE_DEROG_LAST_RUN_RESPONSE");

    private String stringValue;

    private OmniMessageType(String stringValue) {
        this.stringValue = stringValue;
    }

    @JsonValue
    public String getStringValue() {
        return stringValue;
    }
    
    public String toString() {
        return this.getStringValue();
    }
    
    public static OmniMessageType fromString(String stringValue) {
        for (OmniMessageType messageType: values()) {
            if (messageType.getStringValue().equals(stringValue)) {
                return messageType;
            }
        }
        throw new IllegalArgumentException(
                "Unknown Omni message type \"" + stringValue + "\".");
    }
}
