/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.Date;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import gov.gtas.model.CodeShareFlight;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;

public interface FlightRepository extends JpaRepository<Flight, Long>, FlightRepositoryCustom {
    @Query("SELECT f FROM Flight f WHERE f.carrier = :carrier "
            + "AND f.flightNumber = :flightNumber "
            + "AND f.origin = :origin "
            + "AND f.destination=:destination "
            + "AND f.flightDate = :flightDate")
    public Flight getFlightByCriteria(@Param("carrier") String carrier,
            @Param("flightNumber") String flightNumber,@Param("origin") String origin,
            @Param("destination") String destination,@Param("flightDate") Date flightDate);

    /**
     * I thought I was having problems comparing dates with hibernate,
     * but it appears that zero'ing out the time portion of the date was
     * sufficient.  Use this method as a last resort to compare 
     * flight dates. 
     */
    @Query("SELECT f FROM Flight f WHERE f.carrier = :carrier "
            + "AND f.flightNumber = :flightNumber "
            + "AND f.origin = :origin "
            + "AND f.destination=:destination "
            + "AND f.flightDate between :startDate AND :endDate")
    public Flight getFlightByCriteria(
            @Param("carrier") String carrier,
            @Param("flightNumber") String flightNumber,
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    public Page<Flight> findAll(Pageable pageable);
    
    /*@Query("SELECT f FROM Flight f join f.passengers p where p.id = (:paxId)")
    public List<Flight> getFlightByPaxId(@Param("paxId") Long paxId);*/
    
    @Query(nativeQuery = true, 
    		value="SELECT f.* FROM flight_passenger fp join flight f ON (fp.flight_id = f.id) where fp.pax_id = (:paxId)")
    public List<Flight> getFlightByPaxId(@Param("paxId") Long paxId);

    @Query("SELECT f FROM Flight f WHERE f.flightDate between :startDate AND :endDate")
    public List<Flight> getFlightsByDates(@Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate);

    /*@Query("SELECT f FROM Flight f join f.passengers p join p.documents d where UPPER(p.firstName) = UPPER(:firstName) "
                                                              + "AND UPPER(p.lastName) = UPPER(:lastName)"
                                                             + " AND d.documentNumber = :documentNumber"
                                                             + " GROUP BY d.documentNumber, f.flightNumber")
    public List<Flight> getFlightsByPassengerNameAndDocument(@Param("firstName") String firstName,
                                                             @Param("lastName") String lastName,
                                                             @Param("documentNumber") String documentNumber);*/
    
    @Query(nativeQuery = true, 
    		value="SELECT f.* FROM flight_passenger fp JOIN Flight f ON (fp.flight_id = f.id) JOIN Passenger p ON(fp.passenger_Id = p.id )"
    				+ " JOIN Document d ON (p.id = d.passenger_id) where d.document_number=(:documentNumber) AND UPPER(p.first_name) = UPPER(:firstName)"
    				+ " AND UPPER(p.last_name) = UPPER(:lastName)")
    public List<Flight> getFlightsByPassengerNameAndDocument(@Param("firstName") String firstName,
													         @Param("lastName") String lastName,
													         @Param("documentNumber") String documentNumber);

    @Query("SELECT c FROM CodeShareFlight c where c.operatingFlightId = :flightId group by c.marketingFlightNumber")
    public List<CodeShareFlight> getCodeSharesForFlight(@Param("flightId") Long flightId);
    
    @Query( "SELECT DISTINCT f FROM Flight f " +
            "LEFT JOIN FETCH f.passengers pass " +
            "LEFT JOIN FETCH pass.paxWatchlistLinks " +
            "WHERE (f.eta BETWEEN :dateTimeStart AND :dateTimeEnd " +
            "            OR f.etd BETWEEN :dateTimeStart AND :dateTimeEnd) " +
            "       AND (((SELECT MIN(pass.watchlistCheckTimestamp) from pass where pass.flight.id = f.id) <= (SELECT MAX(wl.editTimestamp) FROM Watchlist wl)) " +
            "               OR ((SELECT count(pass.id) from pass where pass.flight.id = f.id AND pass.watchlistCheckTimestamp IS NULL) > 0))")
    public List<Flight> getInboundAndOutboundFlightsWithinTimeFrame(@Param("dateTimeStart")Date date1,
    											  @Param("dateTimeEnd") Date date2);

    default Flight findOne(Long flightId)
    {
    	return findById(flightId).orElse(null);
    }

}
