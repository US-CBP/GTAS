/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

import java.util.List;

public class OmniDerogListUpdate {

    private List<OmniDerogListEntry> passengers;

    public OmniDerogListUpdate(List<OmniDerogListEntry> passengers) {
        this.passengers = passengers;
    }
    
    public List<OmniDerogListEntry> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<OmniDerogListEntry> passengers) {
        this.passengers = passengers;
    }
}
