/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OmniDerogPassengerUpdate {

    @JsonProperty("message_type")
    private String messageType = "UPDATE_DEROG_CATEGORY";

    private List<OmniRawProfile> profiles;

    private List<OmniLookoutCategory> lookoutCategories;

    public List<OmniRawProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<OmniRawProfile> profiles) {
        this.profiles = profiles;
    }

    public List<OmniLookoutCategory> getLookoutCategories() {
        return lookoutCategories;
    }

    public void setLookoutCategories(List<OmniLookoutCategory> lookoutCategories) {
        this.lookoutCategories = lookoutCategories;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
