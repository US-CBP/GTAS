/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import gov.gtas.model.Seat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * The Interface SeatRepository.
 */
public interface SeatRepository extends CrudRepository<Seat, Long> {

	/**
	 * Find by flight id and passenger id.
	 *
	 * @param flightId
	 *            the flight id
	 * @param passengerId
	 *            the passenger id
	 * @return the Seat object
	 */
	@Query("SELECT s FROM Seat s WHERE s.flight.id = :flightId AND s.passenger.id = :passengerId")
	List<Seat> findByFlightIdAndPassengerId(@Param("flightId") Long flightId,
			@Param("passengerId") Long passengerId);
	
	@Query("SELECT s FROM Seat s WHERE s.flight.id = :flightId AND s.passenger.id = :passengerId AND s.apis=0")
	List<Seat> findByFlightIdAndPassengerIdNotApis(@Param("flightId") Long flightId,
			@Param("passengerId") Long passengerId);
}
