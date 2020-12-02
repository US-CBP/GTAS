/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface HitsSummaryRepository extends CrudRepository<HitsSummary, Long> {

	/**
	 * @param id
	 *            pax id
	 * @return RULE hits only
	 */
	@Query("SELECT h FROM HitsSummary h WHERE h.paxId = (:id)")
	public HitsSummary findRuleHitsByPassengerId(@Param("id") Long id);

	@Query("SELECT s FROM HitsSummary s")
	public Iterable<HitsSummary> findAll();

	@Query("SELECT hits FROM HitsSummary hits WHERE hits.paxId in :pidList")
	Set<HitsSummary> findHitsByPassengerIdList(@Param("pidList") List<Long> passengerIdList);

	@Query("DELETE FROM HitsSummary hs WHERE hs.id = (:id)")
	@Modifying
	@Transactional
	public void deleteDBData(@Param("id") Long id);

	@Query("select count(distinct hits.paxId) from HitsSummary hits where hits.flightId = :flightId and hits.watchListHitCount > 0")
	Integer watchlistHitCount(@Param("flightId") Long flightId);

	@Query("select count(distinct hits.paxId) from HitsSummary hits where hits.flightId = :flightId and hits.ruleHitCount > 0")
	Integer ruleHitCount(@Param("flightId") Long flightId);

	@Query("select count(distinct hits.paxId) from HitsSummary hits where hits.flightId = :flightId and hits.graphHitCount > 0")
	Integer graphHitCount(@Param("flightId") Long flightId);

	@Query("select count(distinct hits.paxId) from HitsSummary hits where hits.flightId = :flightId and hits.partialHitCount > 0")
	Integer partialHitCount(@Param("flightId") Long flightId);

	@Query("select count(distinct hits.paxId) from HitsSummary hits where hits.flightId = :flightId and hits.externalHitCount > 0")
	Integer externalHitCount(@Param("flightId") Long flightId);

	@Query("select count(distinct hits.paxId) from HitsSummary hits where hits.flightId = :flightId and hits.manualHitCount > 0")
	Integer manualHitCount(@Param("flightId") Long flightId);
	//The Above Queries Grab Passengers With Hits, The Below Give Total Hits
	@Query("select sum(hits.watchListHitCount) from HitsSummary hits where hits.flightId = :flightId and hits.watchListHitCount > 0")
	Integer totalWatchlistHitCount(@Param("flightId") Long flightId);

	@Query("select sum(hits.ruleHitCount) from HitsSummary hits where hits.flightId = :flightId and hits.ruleHitCount > 0")
	Integer totalRuleHitCount(@Param("flightId") Long flightId);

	@Query("select sum(hits.graphHitCount) from HitsSummary hits where hits.flightId = :flightId and hits.graphHitCount > 0")
	Integer totalGraphHitCount(@Param("flightId") Long flightId);

	@Query("select sum(hits.partialHitCount) from HitsSummary hits where hits.flightId = :flightId and hits.partialHitCount > 0")
	Integer totalPartialHitCount(@Param("flightId") Long flightId);

	@Query("select sum(hits.externalHitCount) from HitsSummary hits where hits.flightId = :flightId and hits.externalHitCount > 0")
	Integer totalExternalHitCount(@Param("flightId") Long flightId);

	@Query("select sum(hits.manualHitCount) from HitsSummary hits where hits.flightId = :flightId and hits.manualHitCount > 0")
	Integer totalManualHitCount(@Param("flightId") Long flightId);

	@Query("select hits from HitsSummary hits where hits.flightId = :flightId")
	Set<HitsSummary> findByFlightId(@Param("flightId") Long flightId);

	HitsSummary findFirstByOrderByIdDesc();
}
