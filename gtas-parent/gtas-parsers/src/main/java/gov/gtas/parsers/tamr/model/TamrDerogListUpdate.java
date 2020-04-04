/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import java.util.List;

public class TamrDerogListUpdate {

    private List<TamrDerogListEntry> passengers;

    public TamrDerogListUpdate() {
    }
    
    public TamrDerogListUpdate(List<TamrDerogListEntry> passengers) {
        this.passengers = passengers;
    }
    
    public List<TamrDerogListEntry> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<TamrDerogListEntry> passengers) {
        this.passengers = passengers;
    }

}
