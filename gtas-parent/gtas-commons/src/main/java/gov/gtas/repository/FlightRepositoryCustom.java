/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Flight;
import gov.gtas.services.dto.FlightsRequestDto;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The Interface FlightRepositoryCustom.
 */
public interface FlightRepositoryCustom {
	/**
	 * Return all flights by criteria
	 * 
	 * @param dto
	 *            the request parameters, filters, etc.
	 * @return tuple consisting of total count and list of results.
	 */
	public Pair<Long, List<Flight>> findByCriteria(FlightsRequestDto dto);

}
