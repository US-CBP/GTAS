/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Passenger;
import gov.gtas.model.UserGroup;
import gov.gtas.services.dto.PassengersRequestDto;

import java.util.List;
import java.util.Set;

import gov.gtas.services.dto.PriorityVettingListRequest;
import org.apache.commons.lang3.tuple.Pair;

public interface PassengerRepositoryCustom {
	/**
	 * @param flightId
	 *            optional. When specified, only return the passengers for the given
	 *            flight. When null, return passengers on all flights.
	 * @param request
	 * @return
	 */
	Pair<Long, List<Passenger>> findByCriteria(Long flightId, PassengersRequestDto request);

	Pair<Long, List<Passenger>> priorityVettingListQuery(PriorityVettingListRequest request, Set<UserGroup> userGroups);

}
