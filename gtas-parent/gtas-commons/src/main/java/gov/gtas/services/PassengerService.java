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

import static gov.gtas.constant.GtasSecurityConstants.*;

public interface PassengerService {

	Passenger create(Passenger passenger);

	Passenger update(Passenger passenger);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	Passenger findById(Long id);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	Passenger findByIdWithFlightAndDocumentsAndMessageDetails(Long paxId);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	Passenger findByIdWithFlightAndDocumentsAndHitDetails(Long paxId);

	/**
	 * Gets the passengers by criteria.
	 *
	 * @param flightId
	 *            optional
	 * @param request
	 *            the request
	 * @return the passengers by criteria
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER_AND_MANAGE_QUERIES)
	PassengersPageDto getPassengersByCriteria(Long flightId, PassengersRequestDto request);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	List<Flight> getTravelHistoryNotByItinerary(Long pId, Long pnrId, String pnrRef);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	List<Passenger> getBookingDetailHistoryByPaxID(Long pId);

	Set<Passenger> findPassengerFromPassengerIds(List<Long> passengerIdList);

	void setAllFlights(Set<Flight> flights, Long id);

	Set<Passenger> getPassengersForFuzzyMatching(List<MessageStatus> messageIds);

	Map<Long, Set<Document>> getDocumentMappedToPassengerIds(Set<Long> passengerIds);

	Set<Passenger> getPassengersWithHitDetails(Set<Long> passengerIds);

    Set<Passenger> getPassengersForEmailMatching(Set<Passenger> passengers);

	Set<Passenger> getPassengersFromMessageIds(Set<Long> messageIds, Set<Long> flightIds);

	Set<Passenger> getFullPassengersFromMessageIds(Set<Long> messageIds, Set<Long> flightIds);

	Set<Document> getPassengerDocuments(Set<Long> passengerIds, Set<Long> flightIds);

    Set<Passenger> getPassengersWithBags(Set<Long> passengerIds, Long flightId);
}
