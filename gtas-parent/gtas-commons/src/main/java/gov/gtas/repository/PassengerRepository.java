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
			+ "left join fetch p.flight left join fetch p.passengerDetailFromMessages " + "WHERE p.id = :id")
	Passenger findByIdWithFlightAndDocumentsAndMessageDetails(@Param("id") Long id);
	
	@Query("SELECT p FROM Passenger p " + "left join fetch p.passengerTripDetails "
			+ "left join fetch p.passengerDetails " + "left join fetch p.documents "
			+ "left join fetch p.flight "
			+ "left join fetch p.hitDetails " +  "WHERE p.id = :id")
	Passenger findByIdWithFlightAndDocumentsAndHitDetails(@Param("id") Long id);

	default Passenger findOne(Long passengerId) {
		return findById(passengerId).orElse(null);
	}

	@Query("Select p " + "from Passenger p " + "join fetch p.passengerIDTag " + "left join p.apisMessage apis "
			+ "left join p.pnrs pnrs " + "left join fetch p.flight f " +
			" WHERE (p.flight.id in :flightIds " +
			" AND (apis.id IN :messageIds " +
			"       OR pnrs.id IN :messageIds)) ")
	Set<Passenger> getPassengerWithIdInformation(@Param("messageIds") Set<Long> messageIds,  @Param("flightIds")Set<Long> flightIds);

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

	@Query("Select p from Passenger p left join fetch p.flight where p.id in :passengerIds")
	Set<Passenger> getPassengersWithFlightDetails(@Param("passengerIds") Set<Long> passengerIds);

	@Query("select p from Passenger p inner join fetch p.flight inner join fetch p.documents inner join fetch p.hitDetails hd inner join fetch"
			+ " hd.hitMaker hm inner join fetch  hm.hitCategory hc inner join fetch hc.userGroups inner join fetch p.hits where p.id in :paxIds")
	Set<Passenger> getPassengersForVettingPage(@Param("paxIds") Set<Long> paxIds);

	@Query("SELECT p from Passenger p where p.id in :paxIds")
    Set<Passenger> getPassengersForEmailDto(@Param("paxIds") Set<Long> paxIds);


	@Query("SELECT p FROM Passenger p " + " LEFT JOIN FETCH p.dataRetentionStatus "
			+ " LEFT JOIN FETCH p.flight " + " LEFT JOIN fetch p.apisMessage am " + " LEFT JOIN fetch p.pnrs pnr "
			+ "LEFT JOIN FETCH p.hitDetails "
			+ " WHERE (p.flight.id in :flightIds" + " AND (am.id IN :messageIds "
			+ "       OR pnr.id IN :messageIds)) ")
	Set<Passenger> getPassengerIncludingHitsByMessageId(@Param("messageIds") Set<Long> messageIds, @Param("flightIds") Set<Long> flightIds);

	@Query("SELECT p FROM Passenger p " + " LEFT JOIN FETCH p.dataRetentionStatus left join fetch p.passengerDetailFromMessages"
			+ " LEFT JOIN FETCH p.flight " + " LEFT JOIN fetch p.apisMessage am " + " LEFT JOIN fetch p.pnrs pnr "
			+ "LEFT JOIN FETCH p.hitDetails LEFT JOIN FETCH p.notes pnotes left join fetch pnotes.noteType "
			+ " WHERE (p.flight.id in :flightIds" + " AND (am.id IN :messageIds "
			+ "       OR pnr.id IN :messageIds)) ")
	Set<Passenger> getFullPassengerIncludingHitsByMessageId(@Param("messageIds") Set<Long> messageIds, @Param("flightIds") Set<Long> flightIds);

	@Query("Select p from Passenger p left join fetch p.bags where p.id in :passengerIds and p.flight.id = :flightId")
    Set<Passenger> getDocumentsByPaxIdFlightId(@Param("passengerIds")Set<Long> passengerIds, @Param("flightId")Long flightId);
}
