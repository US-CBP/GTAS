/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.DispositionStatusVo;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_MANAGE_RULES_AND_MANAGE_WATCH_LIST_AND_MANAGE_QUERIES;
import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER;

public interface PassengerService {

	Passenger create(Passenger passenger);

	Passenger update(Passenger passenger);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	Passenger findById(Long id);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	Passenger findByIdWithFlightPaxAndDocuments(Long paxId);

	/**
	 * Gets the passengers by criteria.
	 *
	 * @param flightId
	 *            optional
	 * @param request
	 *            the request
	 * @return the passengers by criteria
	 */
	@PreAuthorize(GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER_AND_MANAGE_QUERIES)
	PassengersPageDto getPassengersByCriteria(Long flightId, PassengersRequestDto request);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	List<Flight> getTravelHistoryByItinerary(Long pnrId, String pnrRef);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	List<Flight> getTravelHistoryNotByItinerary(Long pId, Long pnrId, String pnrRef);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	List<Passenger> getBookingDetailHistoryByPaxID(Long pId);

	Set<FlightPax> findFlightPaxFromPassengerIds(List<Long> passengerIdList);

	void setAllFlights(Set<Flight> flights, Long id);

	Set<Passenger> getPassengersForFuzzyMatching(List<MessageStatus> messageIds);

	Map<Long, Set<Document>> getDocumentMappedToPassengerIds(Set<Long> passengerIds);

	Set<Passenger> getPassengersWithHitDetails(Set<Long> passengerIds);
}
