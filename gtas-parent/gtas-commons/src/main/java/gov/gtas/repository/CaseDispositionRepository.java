/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Case;
import gov.gtas.model.Flight;
import gov.gtas.model.HitsDisposition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface CaseDispositionRepository extends JpaRepository<Case, Long>, CaseDispositionRepositoryCustom {


    public List<Case> findAll();

    public Page<Case> findAll(Pageable pageable);

    public Case findById(Long id);

    @Query("SELECT c FROM Case c WHERE c.flightId = (:flightId) "
            + "AND c.paxId = (:paxId) AND c.status in :status")
    public List<Case> getCaseByFlightIdAndPaxId(
            @Param("flightId") Long flightId,
            @Param("paxId") Long paxId, 
            @Param("status") List<String> status);
    
    @Query("SELECT c FROM Case c WHERE "
            + " c.paxId in (:paxIds)")
    public List<Case> getCaseByPaxId(
            @Param("paxIds") List<Long> paxIds);

    @Query("SELECT p FROM Case c join c.hitsDispositions p where c.id = (:caseId)")
    public Set<HitsDisposition> getHitsDispositionByCaseId(@Param("caseId") Long caseId);

    @Modifying
    @Transactional
    @Query("update Case set hitsDispositions = :hitsDispositionSet where id = :caseId")
    public Integer updateHitsDispositionsForCase(Long caseId, Set<HitsDisposition> hitsDispositionSet);

//    @Modifying
//    @Transactional
//    @Query("update HitsDisposition set hitsDispositions = :hitsDispositionSet where id = :id")
//    public Integer updateDispCommentsForHitsDisposition(Long id, Set<HitsDisposition> hitsDispositionSet);

   @Query("SELECT c " +
			"FROM Case c JOIN c.flight flt  WHERE c.oneDayLookoutFlag = true AND ((flt.eta BETWEEN :startDate AND :endDate AND UPPER(flt.direction)='I') OR (flt.etd BETWEEN :startDate AND :endDate AND UPPER(flt.direction) = 'O' ))" )
	public List<Case> findOneDayLookoutByDate(@Param("startDate") Date startDate, @Param("endDate")Date endDate);
   
   @Query("SELECT c " +
			"FROM Case c JOIN c.flight flt  WHERE c.oneDayLookoutFlag = true AND ((flt.eta BETWEEN :startDate AND :endDate AND UPPER(flt.direction)='I' AND flt.destination = :airport) OR (flt.etd BETWEEN :startDate AND :endDate AND UPPER(flt.direction) = 'O' AND flt.origin=:airport ))" )
	public List<Case> findOneDayLookoutByDateAndAirport(@Param("startDate") Date startDate, @Param("endDate")Date endDate, @Param("airport")String airport);
   
   @Modifying
   @Transactional
   @Query("update Case set oneDayLookoutFlag = :flag where id = :caseId")
   public Integer updateOneDayLookoutFlag(@Param("caseId")Long caseId, @Param("flag") Boolean flag);
   

	
}