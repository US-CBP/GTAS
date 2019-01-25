/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;

import java.util.List;

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
    @Query("SELECT hits.hitdetails FROM HitsSummary hits WHERE hits.passenger.id = (:id)")
    public List<HitDetail> findByPassengerId(@Param("id") Long id);

    @Query("SELECT det.ruleId, count(*) FROM HitsSummary h join h.hitdetails det WHERE det.hitType = 'R'"
            + " group by det.ruleId")
    public List<Object[]> findDetailsByUdr();

    /**
     * @param id
     *            pax id
     * @return RULE hits only
     */
    @Query("SELECT d FROM HitsSummary h join h.hitdetails d WHERE h.passenger.id = (:id) and d.hitType = 'R'")
    public List<HitDetail> findRuleHitsByPassengerId(@Param("id") Long id);

    @Query("SELECT s FROM HitsSummary s")
    public Iterable<HitsSummary> findAll();

    @Query("SELECT hits FROM HitsSummary hits WHERE hits.passenger.id = :pid and hits.flight.id = :fid")
    List<HitsSummary> findByFlightIdAndPassengerId(@Param("fid") Long flightId,
            @Param("pid") Long passengerId);

    @Query("SELECT hits FROM HitsSummary hits WHERE hits.passenger.id = :pid and hits.flight.id = :fid and hits.hitType IN :hitTypes")
    List<HitsSummary> findByFlightIdAndPassengerIdWithHitTypes(
            @Param("fid") Long flightId, @Param("pid") Long passengerId,
            @Param("hitTypes") List<String> listHitTypes);

    @Query("SELECT hits FROM HitsSummary hits WHERE hits.flight.id = :fid")
    List<HitsSummary> findHitsByFlightId(@Param("fid") Long flightId);
    
    @Query("SELECT hits FROM HitsSummary hits WHERE hits.passenger.id in :pidList")
    List<HitsSummary> findHitsByPassengerIdList(@Param("pidList") List<Long> passengerIdList);

    @Query("DELETE FROM HitsSummary hs WHERE hs.id = (:id)")
    @Modifying
    @Transactional
    public void deleteDBData(@Param("id") Long id);

    @Query("SELECT enabled FROM RuleMeta WHERE id IN (SELECT hd.ruleId  FROM HitDetail hd WHERE hd.parent.flight.id=:flightId AND hd.parent.passenger.id=:passengerId)")
    public List<String> enableFlagByUndeletedAndEnabledRule(
            @Param("flightId") Long flightId,
            @Param("passengerId") Long passengerId);
    
    
    @Query("SELECT hits from HitsSummary hits WHERE hits.flight.id = :flightId AND hits.passenger.id = :passengerId")
    public List<HitsSummary> retrieveHitsByFlightAndPassengerId(
            @Param("flightId") Long flightId,@Param("passengerId") Long passengerId);

    @Query("select count(distinct passenger) from HitsSummary where flight.id = :flight and watchListHitCount > 0")
    Integer watchlistHitCount(@Param("flight") Long flight);

    @Query("select count(distinct passenger) from HitsSummary where flight.id = :flight and ruleHitCount > 0")
    Integer ruleHitCount(@Param("flight") Long flight);
}
