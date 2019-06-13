/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Disposition;
import gov.gtas.model.Passenger;


public interface PassengerRepository extends PagingAndSortingRepository<Passenger, Long>, PassengerRepositoryCustom {
    
	@Query( "SELECT p FROM Passenger p " +
            "left join fetch p.passengerTripDetails " +
            "left join fetch p.passengerDetails " +
            "WHERE p.id = :id")
	Passenger getPassengerById(@Param("id") Long id);

	@Query( "SELECT p FROM Passenger p " +
            "left join fetch p.passengerTripDetails " +
            "left join fetch p.passengerDetails " +
            "left join fetch p.documents " +
            "left join fetch p.flight " +
            "left join fetch  p.paxWatchlistLinks " +
            "WHERE p.id = :id")
	Passenger getFullPassengerById(@Param("id") Long id);

	@Query( "SELECT p from Passenger p " +
            "left join fetch p.passengerTripDetails " +
            "left join fetch p.passengerDetails " +
            "left join fetch p.documents " +
            "where p.id in :id")
    List<Passenger> getPassengersById(@Param("id") List<Long> id);

    @Query("SELECT p from Passenger p " +
            "left join fetch p.hits " +
            "left join fetch p.flight " +
            "where p.id in :id")
    Set<Passenger> getPassengerWithHits(@Param("id") Set<Long> id);
/*

    @Query("SELECT p FROM Passenger p WHERE UPPER(p.firstName) = UPPER(:firstName) AND UPPER(p.lastName) = UPPER(:lastName)")
    List<Passenger> getPassengerByName(@Param("firstName") String firstName,@Param("lastName") String lastName);
    
    @Query("SELECT p FROM Passenger p WHERE UPPER(p.lastName) = UPPER(:lastName)")
    List<Passenger> getPassengersByLastName(@Param("lastName") String lastName);
*/

    @Query( "SELECT p FROM Passenger p " +
            "left join fetch p.passengerTripDetails " +
            "left join fetch p.passengerDetails " +
            "left join fetch p.documents " +
            "left join fetch p.flightPaxList pfl " +
            "left join fetch pfl.flight " +
            "WHERE p.id = :id")
    Passenger findByIdWithFlightPaxAndDocuments(@Param("id") Long id);

  /*  @Query("SELECT p FROM Flight f join f.passengers p where f.id = (:flightId)")MESSAGE RECEIVED FROM QUEUE:
    public List<Passenger> getPassengersByFlightId(@Param("flightId") Long flightId);*/

/*    @Query("SELECT p FROM Passenger p WHERE UPPER(p.firstName) = UPPER(:firstName) AND UPPER(p.lastName) = UPPER(:lastName)")
    List<Passenger> getPassengerByName(@Param("firstName") String firstName,@Param("lastName") String lastName);

  */
    /*@Query(nativeQuery = true, 
    		value="SELECT p.* FROM flight_passenger fp join passenger p ON (fp.passenger_id = p.id) where fp.flight_id = (:flightId)")
    public List<Passenger> getPassengersByFlightId(@Param("flightId") Long flightId);*/
    
/*    @Query("SELECT p FROM Flight f join f.passengers p where f.id = (:flightId) AND UPPER(p.firstName) = UPPER(:firstName) AND UPPER(p.lastName) = UPPER(:lastName)")
    public List<Passenger> getPassengersByFlightIdAndName(@Param("flightId") Long flightId, @Param("firstName") String firstName,@Param("lastName") String lastName);*/

   /* @Query(nativeQuery = true, 
    		value="SELECT p.*, ptag.* FROM flight_passenger fp join passenger p ON (fp.passenger_id = p.id) join pax_idtag ptag ON (ptag.pax_id = p.id) "
    				+ "where fp.flight_id = (:flightId) AND UPPER(p.first_name) = UPPER(:firstName) AND UPPER(p.last_name) = UPPER(:lastName)")
    public List<Passenger> getPassengersByFlightIdAndName(@Param("flightId") Long flightId, @Param("firstName") String firstName,@Param("lastName") String lastName);*/
    
    @Query("SELECT d FROM Disposition d where d.passenger.id = (:passengerId) AND d.flight.id = (:flightId)")
    List<Disposition> getPassengerDispositionHistory(@Param("passengerId") Long passengerId, @Param("flightId") Long flightId);


    default Passenger findOne(Long passengerId)
    {
        return findById(passengerId).orElse(null);
    }


    @Query("Select p " +
            "from Passenger p " +
            "join fetch p.passengerIDTag " +
            "left join p.apisMessage apis " +
            "left join p.pnrs pnrs " +
            "left join fetch p.flight f " +
            "where apis.id in :messageId " +
            "or pnrs.id in :messageId")
    Set<Passenger> getPassengerWithIdInformation(@Param("messageId") Set<Long> messageId);

    @Query("SELECT p FROM Passenger p " +
            " LEFT JOIN FETCH p.paxWatchlistLinks " +
            " LEFT JOIN FETCH p.passengerDetails " +
            " LEFT JOIN FETCH p.passengerWLTimestamp " +
            " LEFT JOIN FETCH p.documents " +
            " LEFT JOIN FETCH p.flight " +
            " LEFT JOIN p.apisMessage am " +
            " LEFT JOIN p.pnrs pnr " +
            " WHERE am.id IN :messageIds OR pnr.id IN :messageIds")
    Set<Passenger> getPassengerMatchingInformation(@Param("messageIds") Set<Long> messageIds);


//	@Query("SELECT p FROM Passenger p WHERE UPPER(p.firstName) = UPPER(:firstName) " +
//            "AND UPPER(p.lastName) = UPPER(:lastName)" +
//            "AND UPPER(p.gender) = UPPER(:gender)" +
//            "AND UPPER(p.nationality) = UPPER(:ctz_country) " +
//            "AND p.dob = (:dob)" +
//            "AND p.idTag IS NOT NULL")
//	public List<Passenger> getNotNullIdTaggedPassenger(@Param("firstName") String firstName, @Param("lastName") String lastName,
//                                                         @Param("gender") String gender, @Param("ctz_country") String ctz_country,
//                                                         @Param("dob") Date dob);

//	@Query("SELECT p FROM Passenger p WHERE p.idTag = (:idTag)")
//    public List<Passenger> getIdTaggedPassenger(@Param("") String idTag);
//
/*	@Query("SELECT p FROM Passenger p WHERE p.paxIdTag IS NULL")
    public List<Passenger> getNullIdTagPassengers();

    @Query("SELECT p FROM Passenger p WHERE p.paxIdTag IS NOT NULL")
    public List<Passenger> getNotNullIdTagPassengers();*/
}
