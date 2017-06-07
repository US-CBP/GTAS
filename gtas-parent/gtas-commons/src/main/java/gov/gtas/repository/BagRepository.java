/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Bag;
import gov.gtas.model.Seat;

public interface BagRepository extends CrudRepository<Bag, Long> {
	@Query("SELECT b FROM Bag b WHERE b.flight.id = :flightId AND b.passenger.id = :passengerId")
	List<Bag> findByFlightIdAndPassengerId(@Param("flightId") Long flightId,
			@Param("passengerId") Long passengerId);
}
