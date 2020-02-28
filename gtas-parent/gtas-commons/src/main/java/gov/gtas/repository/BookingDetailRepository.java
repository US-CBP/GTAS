/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Passenger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.BookingDetail;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface BookingDetailRepository extends CrudRepository<BookingDetail, Long> {

	@Query("SELECT pax FROM Passenger pax " + "left join fetch pax.passengerDetails  "
			+ "left join fetch pax.bookingDetails " + "left join fetch pax.flightPaxList pfpl "
			+ "left join fetch pax.passengerTripDetails " + "left join fetch pfpl.flight "
			+ "left join fetch pfpl.passenger  WHERE pax.id IN ("
			+ "SELECT pxtag.pax_id FROM PassengerIDTag pxtag WHERE pxtag.idTag IN (SELECT p.idTag FROM PassengerIDTag p WHERE p.pax_id = (:pax_id) ))")
	List<Passenger> getBookingDetailsByPassengerIdTag(@Param("pax_id") Long pax_id);

	@Query("Select bd from BookingDetail bd " + "where bd.fullFlightNumber = :fullFlightNumber "
			+ "and bd.destination = :destination " + "and bd.origin = :origin " + "and bd.etd = :etd "
			+ "and bd.flight.id = :flightId")
	List<BookingDetail> getBookingDetailByCriteria(@Param("fullFlightNumber") String fullFlightNumber,
			@Param("destination") String destination, @Param("origin") String origin, @Param("etd") Date etd,
			@Param("flightId") Long flightId);

	@Query("Select bd from BookingDetail bd left join bd.passengers pax where bd.flightId = :flightId and :passenger in (pax.id)")
	List<BookingDetail> bookingDetailsByPassengerId(@Param("passenger") Long passenger,
			@Param("flightId") long flightId);

}
