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

public interface HitsSummaryRepository extends
        CrudRepository<HitsSummary, Long> {

    /**
     * @param id
     *            pax id
     * @return all hit types
     */
    @Query("SELECT hits.hitdetails FROM HitsSummary hits WHERE hits.paxId = (:id)")
    public List<HitDetail> findByPassengerId(@Param("id") Long id);

    @Query("SELECT det.ruleId, count(*) FROM HitsSummary h join h.hitdetails det WHERE det.hitType = 'R'"
            + " group by det.ruleId")
    public List<Object[]> findDetailsByUdr();

    /**
     * @param id
     *            pax id
     * @return RULE hits only
     */
    @Query("SELECT h FROM HitsSummary h WHERE h.paxId = (:id)")
    public HitsSummary findRuleHitsByPassengerId(@Param("id") Long id);

    @Query("SELECT s FROM HitsSummary s")
    public Iterable<HitsSummary> findAll();

    @Query("SELECT hits FROM HitsSummary hits WHERE hits.paxId = :pid and hits.flightId = :fid")
    List<HitsSummary> findByFlightIdAndPassengerId(@Param("fid") Long flightId,
            @Param("pid") Long passengerId);

    @Query("SELECT hits FROM HitsSummary hits WHERE hits.paxId = :pid and hits.flightId = :fid and hits.hitType IN :hitTypes")
    List<HitsSummary> findByFlightIdAndPassengerIdWithHitTypes(
            @Param("fid") Long flightId, @Param("pid") Long passengerId,
            @Param("hitTypes") List<String> listHitTypes);

    @Query("SELECT hits FROM HitsSummary hits WHERE hits.flightId = :fid")
    List<HitsSummary> findHitsByFlightId(@Param("fid") Long flightId);
    
    @Query("SELECT hits FROM HitsSummary hits WHERE hits.paxId in :pidList")
    Set<HitsSummary> findHitsByPassengerIdList(@Param("pidList") List<Long> passengerIdList);

    @Query("DELETE FROM HitsSummary hs WHERE hs.id = (:id)")
    @Modifying
    @Transactional
    public void deleteDBData(@Param("id") Long id);

    @Query("SELECT enabled FROM RuleMeta WHERE id IN (SELECT hd.ruleId  FROM HitDetail hd WHERE hd.parent.flightId=:flightId AND hd.parent.paxId=:passengerId)")
    public List<String> enableFlagByUndeletedAndEnabledRule(
            @Param("flightId") Long flightId,
            @Param("passengerId") Long passengerId);
    
    
    @Query("SELECT hits from HitsSummary hits WHERE hits.flightId = :flightId AND hits.paxId = :passengerId")
    public List<HitsSummary> retrieveHitsByFlightAndPassengerId(
            @Param("flightId") Long flightId,@Param("passengerId") Long passengerId);

    @Query("select count(distinct hits.paxId) from HitsSummary hits where hits.flightId = :flightId and watchListHitCount > 0")
    Integer watchlistHitCount(@Param("flightId") Long flightId);

    @Query("select count(distinct hits.paxId) from HitsSummary hits where hits.flightId = :flightId and ruleHitCount > 0")
    Integer ruleHitCount(@Param("flightId") Long flightId);

    @Query("select count(distinct hits.paxId) from HitsSummary hits where hits.flightId = :flightId and graphHitCount > 0")
    Integer graphHitCount(@Param("flightId") Long flightId);
}
