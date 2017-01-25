/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Seat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * The Interface SeatRepository.
 */
public interface SeatRepository extends CrudRepository<Seat, Long> {
	@Query("SELECT s FROM Seat s WHERE s.flight.id = :flightId AND s.passenger.id = :passengerId")
	public Seat findByFlightIdAndPassenger(@Param("flightId") Long flightId,
			@Param("passengerId") Long passengerId);
}
