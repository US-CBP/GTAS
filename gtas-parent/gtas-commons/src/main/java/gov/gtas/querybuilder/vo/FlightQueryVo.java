/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.vo;

import gov.gtas.model.Flight;

import java.util.List;

public class FlightQueryVo {
    List<Flight> flights;
    long totalFlights;
    private boolean queryLimitReached;
    
    public List<Flight> getFlights() {
        return flights;
    }
    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
    public long getTotalFlights() {
        return totalFlights;
    }
    public void setTotalFlights(long totalFlights) {
        this.totalFlights = totalFlights;
    }

    public boolean isQueryLimitReached() {
        return queryLimitReached;
    }

    public void setQueryLimitReached(boolean queryLimitReached) {
        this.queryLimitReached = queryLimitReached;
    }
}
