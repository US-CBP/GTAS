/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import java.util.List;

import gov.gtas.vo.passenger.FlightVo;

public class FlightsPageDto {
    private List<FlightVo> flights;
    private long totalFlights;
    public FlightsPageDto(List<FlightVo> flights, long totalFlights) {
        this.flights = flights;
        this.totalFlights = totalFlights;
    }
    public List<FlightVo> getFlights() {
        return flights;
    }
    public long getTotalFlights() {
        return totalFlights;
    }
}