/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import java.util.List;

public class TamrQuery {
    private List<TamrPassenger> passengers;

    public TamrQuery() {
    }
    
    public TamrQuery(List<TamrPassenger> passengers) {
        this.passengers = passengers;
    }
    
    public List<TamrPassenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<TamrPassenger> passengers) {
        this.passengers = passengers;
    }
    
}
