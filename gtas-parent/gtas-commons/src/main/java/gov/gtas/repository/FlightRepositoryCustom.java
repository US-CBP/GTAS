/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import gov.gtas.model.Flight;
import gov.gtas.services.dto.FlightsRequestDto;

public interface FlightRepositoryCustom {
    /**
     * Return all flights by criteria
     * @param dto the request parameters, filters, etc.
     * @return tuple consisting of total count and list of results.
     */
    public Pair<Long, List<Flight>> findByCriteria(FlightsRequestDto dto);
    
    public void deleteAllMessages() throws Exception ;
}