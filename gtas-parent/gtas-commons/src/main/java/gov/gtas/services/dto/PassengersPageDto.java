/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import java.util.List;

import gov.gtas.vo.passenger.PassengerVo;

public class PassengersPageDto {
    private List<PassengerVo> passengers;
    private long totalPassengers;
    public PassengersPageDto(List<PassengerVo> passengers, long totalPassengers) {
        this.passengers = passengers;
        this.totalPassengers = totalPassengers;
    }
    public List<PassengerVo> getPassengers() {
        return passengers;
    }
    public long getTotalPassengers() {
        return totalPassengers;
    }
}
