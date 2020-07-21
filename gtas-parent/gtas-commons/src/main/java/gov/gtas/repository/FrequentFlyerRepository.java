/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.FrequentFlyer;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface FrequentFlyerRepository extends CrudRepository<FrequentFlyer, Long> {

	List<FrequentFlyer> findByCarrierAndNumberAndFlightId(String carrier, String number, Long flightId);

	@Query("Select ff from FrequentFlyer ff " +
			"left join fetch ff.pnrs ffPnrs " +
			"left join fetch ffPnrs.passengers " +
			"where ffPnrs.id in :pnrIds and ff.flightId in :flightIds")
    Set<FrequentFlyer> findFrequentFlyers(@Param("flightIds")Set<Long> flightIds, @Param("pnrIds") Set<Long> pnrIds);
}
