/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.services.dto.FlightsPageDto;
import gov.gtas.services.dto.FlightsRequestDto;
import gov.gtas.vo.passenger.SeatVo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import gov.gtas.vo.passenger.FlightVo;


import gov.gtas.vo.passenger.FlightGridVo;
import org.springframework.security.access.prepost.PreAuthorize;

import static gov.gtas.constant.GtasSecurityConstants.*;


public interface FlightService {
	Flight create(Flight flight);

	Flight update(Flight flight);

	Flight findById(Long id);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHTS)
	FlightsPageDto findAll(FlightsRequestDto dto);

	@Deprecated
	Flight getUniqueFlightByCriteria(String carrier, String flightNumber, String origin, String destination,
			Date flightDate);

	// always a list of 1.
	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHTS)
	List<Flight> getFlightByPaxId(Long paxId);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHTS)
	Set<Flight> getFlightByPaxIds(Set<Long> paxId);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHTS)
	@Deprecated
	List<Flight> getFlightsByDates(Date startDate, Date endDate);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHTS)
	@Deprecated
	HashMap<Document, List<Flight>> getFlightsByPassengerNameAndDocument(String firstName, String lastName,
			Set<Document> documents);
	@Deprecated
	List<Flight> getFlightsThreeDaysForwardInbound();

	@Deprecated
	List<Flight> getFlightsThreeDaysForwardOutbound();

	@Deprecated
	Set<Passenger> getAllPassengers(Long id);

	@Deprecated
	void setAllPassengers(Set<Passenger> passengers, Long flightId);

	@Deprecated
	void setSinglePassenger(Long passengerId, Long flightId);

	int getPassengerCount(Flight f);

	List<SeatVo> getSeatsByFlightId(Long flightId);

	List<FlightVo> convertFlightToFlightVo(List<Flight> flights);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHTS)
	List<FlightGridVo> findFlights(FlightsRequestDto dto);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHTS)
	List<FlightGridVo> convertFlightToFlightGridVo(List<Flight> flights);

}
