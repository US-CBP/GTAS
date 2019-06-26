/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER;
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
import org.springframework.security.access.prepost.PreAuthorize;

public interface FlightService {
	public Flight create(Flight flight);

	public Flight update(Flight flight);

	public Flight findById(Long id);

	//@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	public FlightsPageDto findAll(FlightsRequestDto dto);

	public Flight getUniqueFlightByCriteria(String carrier,
			String flightNumber, String origin, String destination,
			Date flightDate);

	public List<Flight> getFlightByPaxId(Long paxId);

	public List<Flight> getFlightsByDates(Date startDate, Date endDate);

	//@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	public HashMap<Document, List<Flight>> getFlightsByPassengerNameAndDocument(
			String firstName, String lastName, Set<Document> documents);

	public List<Flight> getFlightsThreeDaysForward();

	public List<Flight> getFlightsThreeDaysForwardInbound();

	public List<Flight> getFlightsThreeDaysForwardOutbound();

	public Set<Passenger> getAllPassengers(Long id);

	public void setAllPassengers(Set<Passenger> passengers, Long flightId);
	
	public void setSinglePassenger(Long passengerId, Long flightId);

	public int getPassengerCount(Flight f);

	/*
	* Get fuzzy matches only. Does not get fuzzy matches that have firm watchlist matches.
	* */
	public Long getFlightFuzzyMatchesOnly(Long flightId);
	
	public List<SeatVo> getSeatsByFlightId(Long flightId);

	public List<FlightVo> convertFlightToFlightVo(List<Flight> flights);

}
