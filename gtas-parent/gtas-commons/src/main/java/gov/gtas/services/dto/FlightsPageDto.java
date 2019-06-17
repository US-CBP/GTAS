/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import java.util.List;

import gov.gtas.vo.passenger.FlightVo;

public class FlightsPageDto {
    private List<FlightVo> flights;
    private boolean queryLimitReached;
    private long totalFlights;
    public FlightsPageDto(List<FlightVo> flights, long totalFlights) {
        this(flights, totalFlights, false);
    }

    public FlightsPageDto(List<FlightVo> flights, long totalFlights, boolean queryLimitReached) {
      this.flights = flights;
      this.totalFlights = totalFlights;
      this.queryLimitReached = queryLimitReached;
    }

    public List<FlightVo> getFlights() {
        return flights;
    }
    public long getTotalFlights() {
        return totalFlights;
    }

    public boolean isQueryLimitReached() {
        return queryLimitReached;
    }

    public void setQueryLimitReached(boolean queryLimitReached) {
        this.queryLimitReached = queryLimitReached;
    }
}
