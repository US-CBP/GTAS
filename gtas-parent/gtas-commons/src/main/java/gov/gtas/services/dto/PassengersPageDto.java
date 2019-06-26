/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import java.util.List;

import gov.gtas.vo.passenger.PassengerVo;

public class PassengersPageDto {
    private List<PassengerVo> passengers;
    private long totalPassengers;
    private boolean queryLimitReached;
    public PassengersPageDto(List<PassengerVo> passengers, long totalPassengers) {
        this(passengers, totalPassengers, false);
    }
    public PassengersPageDto(List<PassengerVo> passengers, long totalPassengers, boolean queryLimitReached) {
        this.passengers = passengers;
        this.totalPassengers = totalPassengers;
        this.queryLimitReached = queryLimitReached;
    }
    public List<PassengerVo> getPassengers() {
        return passengers;
    }
    public long getTotalPassengers() {
        return totalPassengers;
    }

    public boolean isQueryLimitReached() {
        return queryLimitReached;
    }

    public void setQueryLimitReached(boolean queryLimitReached) {
        this.queryLimitReached = queryLimitReached;
    }
}
