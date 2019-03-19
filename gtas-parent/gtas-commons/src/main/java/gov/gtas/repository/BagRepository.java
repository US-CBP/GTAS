/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Bag;

import javax.transaction.Transactional;

public interface BagRepository extends CrudRepository<Bag, Long> {

	@Query("SELECT bags FROM Bag bags WHERE bags.flight.id = :flightId AND bags.passenger.id = :passengerId")
	List<Bag> findFromFlightAndPassenger(@Param("flightId") Long flightId,
										 @Param("passengerId") Long passengerId);
	@Transactional
	@Query("SELECT bags from Bag bags where bags.passenger.id in :paxIds")
    Set<Bag> getAllByPaxId(@Param("paxIds") Set<Long> paxIds);
}
