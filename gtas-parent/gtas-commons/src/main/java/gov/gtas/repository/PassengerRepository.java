/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Passenger;
import org.springframework.lang.NonNull;

public interface PassengerRepository extends JpaRepository<Passenger, Long>, PassengerRepositoryCustom {

	@Query("SELECT p FROM Passenger p " + "left join fetch p.passengerTripDetails "
			+ "left join fetch p.passengerDetails " + "WHERE p.id = :id")
	Passenger getPassengerById(@Param("id") Long id);

	@Query("SELECT p FROM Passenger p " + "left join fetch p.passengerTripDetails "
			+ "left join fetch p.passengerDetails " + "left join fetch p.documents " + "left join fetch p.flight "
			+ "WHERE p.id = :id")
	Passenger getFullPassengerById(@Param("id") Long id);

	@Query("SELECT p from Passenger p " + "left join fetch p.passengerTripDetails "
			+ "left join fetch p.passengerDetails " + "left join fetch p.documents " + "where p.id in :id")
	List<Passenger> getPassengersById(@Param("id") List<Long> id);

	@Query("SELECT p FROM Passenger p " + "left join fetch p.passengerTripDetails "
			+ "left join fetch p.passengerDetails " + "left join fetch p.documents "
			+ "left join fetch p.flightPaxList pfl " + "left join fetch pfl.flight " + "WHERE p.id = :id")
	Passenger findByIdWithFlightPaxAndDocuments(@Param("id") Long id);

	default Passenger findOne(Long passengerId) {
		return findById(passengerId).orElse(null);
	}

	@Query("Select p " + "from Passenger p " + "join fetch p.passengerIDTag " + "left join p.apisMessage apis "
			+ "left join p.pnrs pnrs " + "left join fetch p.flight f " + "where apis.id in :messageId "
			+ "or pnrs.id in :messageId")
	Set<Passenger> getPassengerWithIdInformation(@Param("messageId") Set<Long> messageId);

	@Query("SELECT p FROM Passenger p " + " LEFT JOIN FETCH p.passengerDetails "
			+ " LEFT JOIN FETCH p.passengerWLTimestamp " + " LEFT JOIN FETCH p.documents "
			+ " LEFT JOIN FETCH p.flight " + " LEFT JOIN p.apisMessage am " + " LEFT JOIN p.pnrs pnr "
			+ " WHERE (p.flight.id in :flightIds" + " AND (am.id IN :messageIds "
			+ "       OR pnr.id IN :messageIds)) ")
	Set<Passenger> getPassengerMatchingInformation(@Param("messageIds") Set<Long> messageIds,
			@Param("flightIds") Set<Long> flightIds);

	@Query("SELECT pax FROM Passenger pax " + "LEFT JOIN FETCH pax.documents " + "LEFT JOIN FETCH pax.seatAssignments "
			+ "LEFT JOIN FETCH pax.bags " + "LEFT JOIN FETCH pax.flightPaxList " + "LEFT JOIN FETCH pax.tickets "
			+ "LEFT JOIN FETCH pax.bookingDetails " + "LEFT JOIN FETCH pax.passengerDetails "
			+ "LEFT JOIN FETCH pax.passengerTripDetails "
			+ "WHERE (UPPER(pax.passengerDetails.firstName) = UPPER(:firstName) "
			+ "AND UPPER(pax.passengerDetails.lastName) = UPPER(:lastName)) " + "AND pax.flight.id = :flightId")
	Set<Passenger> returnAPassengerFromParameters(@Param("flightId") Long flightId,
			@Param("firstName") String firstName, @Param("lastName") String lastName);

	@Query("SELECT pax FROM Passenger pax " + "LEFT JOIN FETCH pax.documents " + "LEFT JOIN FETCH pax.seatAssignments "
			+ "LEFT JOIN FETCH pax.bags " + "LEFT JOIN FETCH pax.flightPaxList " + "LEFT JOIN FETCH pax.tickets "
			+ "LEFT JOIN FETCH pax.bookingDetails " + "LEFT JOIN FETCH pax.passengerDetails "
			+ "LEFT JOIN FETCH pax.passengerTripDetails " + "LEFT JOIN FETCH pax.flight paxFlight "
			+ "WHERE paxFlight.id = :flightId "
			+ "AND :recordLocator IN (SELECT pnr.recordLocator FROM paxFlight.pnrs pnr) "
			+ "AND pax.passengerTripDetails.pnrReservationReferenceNumber = :pnrReservationReferenceNumber ")
	Set<Passenger> getPassengerUsingREF(@NonNull @Param("flightId") Long flightId,
			@NonNull @Param("pnrReservationReferenceNumber") String pnrReservationReferenceNumber,
			@NonNull @Param("recordLocator") String recordLocator);

	@Query("Select p from Passenger p left join fetch p.hitDetails left join fetch p.flight where p.id in :passengerIds")
	Set<Passenger> getPassengersWithHitDetails(@Param("passengerIds") Set<Long> passengerIds);

	@EntityGraph(value = "passengerGraph", type = EntityGraph.EntityGraphType.FETCH)
	@Query("Select p from Passenger p left join p.flight where p.id in :passengerIds")
	Set<Passenger> getPassengersWithFlightDetails(@Param("passengerIds") Set<Long> passengerIds);

	@Query("select p from Passenger p inner join fetch p.flight inner join fetch p.documents inner join fetch p.hitDetails hd inner join fetch"
			+ " hd.hitMaker hm inner join fetch  hm.hitCategory hc inner join fetch hc.userGroups inner join fetch p.hits where p.id in :paxIds")
	Set<Passenger> getPassengersForVettingPage(@Param("paxIds") Set<Long> paxIds);
}
