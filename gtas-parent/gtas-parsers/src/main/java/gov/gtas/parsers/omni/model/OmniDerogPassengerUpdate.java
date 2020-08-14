/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;
import java.util.*;

public class OmniDerogPassengerUpdate {

    private OmniLastRun lastRun;

    private List<OmniRawProfile> profiles;

    private List<OmniLookoutCategory> lookoutCategories;

    public OmniLastRun getLastRun() {
        return lastRun;
    }

    public void setLastRun(OmniLastRun lastRun) {
        this.lastRun = lastRun;
    }

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
}
