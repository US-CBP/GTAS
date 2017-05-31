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

public interface BagRepository extends CrudRepository<Bag, Long> {
    
    @Query("SELECT b.bagId FROM Bag b where b.passengerId = (:passengerId) AND b.flightId = (:flightId)")
    List<String> findByFlightIdAndPassenger(@Param("flightId") Long flightId, @Param("passengerId") Long passengerId);
}
