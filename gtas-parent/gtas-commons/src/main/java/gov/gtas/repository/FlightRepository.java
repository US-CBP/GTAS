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
    
    @Query("SELECT f FROM Flight f join f.passengers p where p.id = (:paxId)")
    public List<Flight> getFlightByPaxId(@Param("paxId") Long paxId);

    @Query("SELECT f FROM Flight f WHERE f.flightDate between :startDate AND :endDate")
    public List<Flight> getFlightsByDates(@Param("startDate") Date startDate, 
                                          @Param("endDate") Date endDate);

    @Query("SELECT f FROM Flight f join f.passengers p join p.documents d where UPPER(p.firstName) = UPPER(:firstName) "
                                                              + "AND UPPER(p.lastName) = UPPER(:lastName)"
                                                             + " AND d.documentNumber = :documentNumber"
                                                             + " GROUP BY d.documentNumber, f.flightNumber")
    public List<Flight> getFlightsByPassengerNameAndDocument(@Param("firstName") String firstName,
                                                             @Param("lastName") String lastName,
                                                             @Param("documentNumber") String documentNumber);
    
    @Modifying
    @Transactional
    @Query("update Flight set ruleHitCount = (select count(distinct passenger) from HitsSummary where flight.id = :flightId and ruleHitCount > 0) where id = :flightId")
    public Integer updateRuleHitCountForFlight(@Param("flightId") Long flightId);

    @Modifying
    @Transactional
    @Query("update Flight set listHitCount = (select count(distinct passenger) from HitsSummary where flight.id = :flightId and watchListHitCount > 0) where id = :flightId")
    public Integer updateListHitCountForFlight(@Param("flightId") Long flightId);
    
    @Query("SELECT c FROM CodeShareFlight c where c.operatingFlightId = :flightId group by c.marketingFlightNumber")
    public List<CodeShareFlight> getCodeSharesForFlight(@Param("flightId") Long flightId);

}
