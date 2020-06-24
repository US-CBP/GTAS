/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.CodeShareFlight;
import gov.gtas.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long>, FlightRepositoryCustom {
	@Query("SELECT f FROM Flight f WHERE f.carrier = :carrier " + "AND f.flightNumber = :flightNumber "
			+ "AND f.origin = :origin " + "AND f.destination=:destination " + "AND f.etdDate = :flightDate")
	Flight getFlightByCriteria(@Param("carrier") String carrier, @Param("flightNumber") String flightNumber,
			@Param("origin") String origin, @Param("destination") String destination,
			@Param("flightDate") Date flightDate);



	Flight findFlightByIdTag(String idTag);

	/**
	 * I thought I was having problems comparing dates with hibernate, but it
	 * appears that zero'ing out the time portion of the date was sufficient. Use
	 * this method as a last resort to compare flight dates.
	 */
	/*
	 * @Query("SELECT f FROM Flight f WHERE f.carrier = :carrier " +
	 * "AND f.flightNumber = :flightNumber " + "AND f.origin = :origin " +
	 * "AND f.destination=:destination " +
	 * "AND f.flightDate between :startDate AND :endDate") Flight
	 * getFlightByCriteria(
	 * 
	 * @Param("carrier") String carrier,
	 * 
	 * @Param("flightNumber") String flightNumber,
	 * 
	 * @Param("origin") String origin,
	 * 
	 * @Param("destination") String destination,
	 * 
	 * @Param("startDate") Date startDate,
	 * 
	 * @Param("endDate") Date endDate);
	 */

	Page<Flight> findAll(Pageable pageable);

	/*
	 * @Query("SELECT f FROM Flight f join f.passengers p where p.id = (:paxId)")
	 * public List<Flight> getFlightByPaxId(@Param("paxId") Long paxId);
	 */

	@Query(nativeQuery = true, value = "SELECT f.* FROM flight_passenger fp join flight f ON (fp.flight_id = f.id) where fp.passenger_id = (:paxId)")
	List<Flight> getFlightByPaxId(@Param("paxId") Long paxId);

	@Query("SELECT f FROM Flight f WHERE f.etdDate between :startDate AND :endDate")
	List<Flight> getFlightsByDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	/*
	 * @Query("SELECT f FROM Flight f join f.passengers p join p.documents d where UPPER(p.firstName) = UPPER(:firstName) "
	 * + "AND UPPER(p.lastName) = UPPER(:lastName)" +
	 * " AND d.documentNumber = :documentNumber" +
	 * " GROUP BY d.documentNumber, f.flightNumber") public List<Flight>
	 * getFlightsByPassengerNameAndDocument(@Param("firstName") String firstName,
	 * 
	 * @Param("lastName") String lastName,
	 * 
	 * @Param("documentNumber") String documentNumber);
	 */

	@Query(nativeQuery = true, value = "SELECT f.* FROM flight_passenger fp JOIN flight f ON (fp.flight_id = f.id) JOIN passenger p ON(fp.passenger_Id = p.id )"
			+ " JOIN document d ON (p.id = d.passenger_id) where d.document_number=(:documentNumber) AND UPPER(p.first_name) = UPPER(:firstName)"
			+ " AND UPPER(p.last_name) = UPPER(:lastName)")
	List<Flight> getFlightsByPassengerNameAndDocument(@Param("firstName") String firstName,
			@Param("lastName") String lastName, @Param("documentNumber") String documentNumber);

	@Query("SELECT c FROM CodeShareFlight c where c.operatingFlightId = :flightId group by c.marketingFlightNumber")
	List<CodeShareFlight> getCodeSharesForFlight(@Param("flightId") Long flightId);

	@Query("SELECT DISTINCT f FROM Flight f  LEFT JOIN FETCH f.passengers pass " + " LEFT JOIN FETCH pass.documents "
			+ "LEFT JOIN FETCH pass.passengerWLTimestamp pwts "
			+ "WHERE (f.mutableFlightDetails.eta BETWEEN :dateTimeStart AND :dateTimeEnd "
			+ "            OR f.mutableFlightDetails.etd BETWEEN :dateTimeStart AND :dateTimeEnd) "
			+ "AND (((SELECT MIN(pwts.watchlistCheckTimestamp) from pwts where pass.flight.id = f.id and pwts.id = pass.id ) <= (SELECT MAX(wl.editTimestamp) FROM Watchlist wl)) "
			+ "       OR (((SELECT COUNT(pass.id) FROM pass WHERE pass.flight.id = f.id AND pass.id NOT IN (SELECT id FROM pwts)) > 0) "
			+ " OR (SELECT COUNT(pass.id) FROM pass WHERE pass.flight.id = f.id AND pass.id = pwts.id AND pwts.watchlistCheckTimestamp IS NULL) > 0))")
	List<Flight> getInboundAndOutboundFlightsWithinTimeFrame(@Param("dateTimeStart") Date date1,
			@Param("dateTimeEnd") Date date2);

	default Flight findOne(Long flightId) {
		return findById(flightId).orElse(null);
	}

	@Query("SELECT f FROM Flight f " + "LEFT JOIN FETCH f.flightPassengerCount "
			+ "WHERE (f.mutableFlightDetails.eta BETWEEN :dateTimeStart AND :dateTimeEnd) " + "AND f.direction = 'I' ")
	List<Flight> getFlightsInboundByDateRange(@Param("dateTimeStart") Date date1, @Param("dateTimeEnd") Date date2);

	@Query("SELECT f FROM Flight f " + "LEFT JOIN FETCH f.flightPassengerCount "
			+ "WHERE (f.mutableFlightDetails.etd BETWEEN :dateTimeStart AND :dateTimeEnd) " + "AND f.direction = 'O' ")
	List<Flight> getFlightsOutBoundByDateRange(@Param("dateTimeStart") Date date1, @Param("dateTimeEnd") Date date2);

	@Query("SELECT f from Flight f " + "LEFT JOIN FETCH f.passengers pax " + "LEFT JOIN FETCH pax.hits hit "
			+ "LEFT JOIN FETCH pax.seatAssignments " + "WHERE f.id = :id")
	Flight getFlightPassengerAndSeatById(@Param("id") Long flightId);

	@Query("SELECT f from Flight f JOIN f.passengers pax WHERE pax.id in :passengerIds ")
	Set<Flight> getFlightByPassengerIds(@Param("passengerIds") Set<Long> passengerIds);
}
