/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import gov.gtas.services.dto.PassengersRequestDto;

public interface PassengerRepositoryCustom {
    /**
     * 
     * @param flightId optional. When specified, only return the passengers 
     * for the given flight.  When null, return passengers on all flights.
     * @param request query criteria.
     */
    public Pair<Long, List<Object[]>> findByCriteria(Long flightId, PassengersRequestDto request);
    
    public List<Object[]> findAllDispositions();
}
