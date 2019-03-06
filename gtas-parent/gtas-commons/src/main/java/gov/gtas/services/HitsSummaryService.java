/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;

import java.util.List;

import org.springframework.data.repository.query.Param;

public interface HitsSummaryService {

	public HitsSummary findByPassengerId(Long id);

	public Iterable<HitsSummary> findAll();

	public List<HitsSummary> findHitsByFlightId(@Param("fid") Long flightId);

	public List<HitsSummary> findByFlightIdAndPassengerIdAndUdrRule(
			Long fightId, Long passengerId);

	public List<HitsSummary> findByFlightIdAndPassengerIdAndWL(Long fightId,
			Long passengerId);

	public List<HitsSummary> findByFlightIdAndPassengerIdAndCombinedWithUdrRule(
			Long fightId, Long passengerId);

	public List<HitsSummary> findByFlightIdAndPassengerId(Long fightId,
			Long passengerId);
}
