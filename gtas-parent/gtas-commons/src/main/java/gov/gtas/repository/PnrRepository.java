/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;
import java.util.Set;

import gov.gtas.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface PnrRepository extends MessageRepository<Pnr> {
	@Query("select pnr from Pnr pnr " + "left join fetch pnr.passengers pax " + "left join fetch pax.documents "
			+ "left join fetch pax.seatAssignments " + "left join fetch pax.flightPaxList fpxl "
			+ "left join fetch fpxl.flight " + "left join fetch pnr.flights f "
			+ "where :passengerId in (select p.id from pnr.passengers p) and f.id = :flightId")
	public Set<Pnr> getPnrsByPassengerIdAndFlightId(@Param("passengerId") Long passengerId,
			@Param("flightId") Long flightId);

	@Query("select pnr from Pnr pnr join pnr.passengers pax where pax.id = :passengerId")
	public List<Pnr> getPnrsByPassengerId(@Param("passengerId") Long passengerId);

	@Query("SELECT pnr FROM Pnr pnr WHERE pnr.createDate >= current_date() - 1")
	public List<Pnr> getPNRsByDates();

	default Pnr findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Transactional
	@Query(" SELECT pnr.id  ,address  FROM Pnr pnr join pnr.addresses address where pnr.id in :pnrIds ")
	List<Object[]> getAddressesByPnr(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query(" SELECT pnr.id, phone from Pnr pnr join pnr.phones phone where pnr.id in :pnrIds ")
	List<Object[]> getPhonesByPnr(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query("Select flights from Pnr pnr join pnr.flights flights where pnr.id in :pnrIds ")
	Set<Flight> getFlightsByPnrIds(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query("SELECT pnr.id, paymentForm from Pnr pnr join pnr.paymentForms paymentForm where pnr.id in :pnrIds ")
	List<Object[]> getPaymentFormsByPnrIds(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query("SELECT passenger from Pnr pnr join pnr.passengers passenger left join fetch passenger.flight where pnr.id in :pnrIds ")
	Set<Passenger> getPassengersWithFlight(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query(" SELECT pnr.id, passenger from Pnr pnr join pnr.passengers passenger where pnr.id in :pnrIds ")
	List<Object[]> getPax(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query(" SELECT pnr.id, dwellTime from Pnr pnr join pnr.dwellTimes dwellTime where pnr.id in :pnrIds ")
	List<Object[]> getDwellTimeByPnr(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query(" SELECT pnr.id, agency from Pnr pnr join pnr.agencies agency where pnr.id in :pnrIds ")
	List<Object[]> getTravelAgencyByPnr(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query(" SELECT pnr.id, ff from Pnr pnr join pnr.frequentFlyers ff where pnr.id in :pnrIds ")
	List<Object[]> getFrequentFlyerByPnrId(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query(" SELECT pnr.id, bd from Pnr pnr join pnr.bookingDetails bd join fetch bd.bags where pnr.id in :pnrIds")
	List<Object[]> getBookingDetailsByPnrId(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query(" SELECT pnr.id, cc from Pnr pnr join pnr.creditCards cc where pnr.id in :pnrIds ")
	List<Object[]> getCreditCardByIds(@Param("pnrIds") Set<Long> pnrIds);

	@Transactional
	@Query(" SELECT pnr.id, email from Pnr pnr join pnr.emails email where pnr.id in :pnrIds ")
	List<Object[]> getEmailByPnrIds(@Param("pnrIds") Set<Long> pnrIds);

	@Query ("SELECT pnr from Pnr pnr " +
			"left join pnr.flights flights " +
			"where flights.id in :flightIds " +
			"and pnr.id in :messageIds ")
    Set<Pnr> getPnrsToScrub(@Param("flightIds") Set<Long> flightIds, @Param("messageIds") Set<Long> messageIds);


	@Query("SELECT pnr from Pnr pnr left join fetch pnr.flights flight join pnr.passengers passenger where pnr.id in :messageIds" +
			" and passenger.id in :passengerIds and flight.id = :flightId")
    Set<Pnr> pnrMessageWithFlightInfo(@Param("passengerIds")Set<Long> pids, @Param("messageIds")Set<Long> messageIds,@Param("flightId") Long flightId);

	@Query("SELECT pnrs.id, passenger from Passenger passenger left join fetch passenger.documents left join fetch passenger.hitDetails hd left join fetch hd.hitMaker hm left join fetch hm.hitCategory join passenger.pnrs pnrs where pnrs.id in :pnrIds" +
			" and passenger.id in :passengerIds")
	List<Object[]> pnrAndObject(@Param("passengerIds") Set<Long>passengerIds, @Param("pnrIds") Set<Long> pnrIds);
}
