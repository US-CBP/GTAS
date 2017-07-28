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
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface CaseDispositionRepository extends JpaRepository<Case, Long>, CaseDispositionRepositoryCustom {


    public List<Case> findAll();

    public Page<Case> findAll(Pageable pageable);

    public Case findById(Long flight_id, Long pax_id, Long hit_id);

    @Query("SELECT c FROM Case c WHERE c.flightId = :flightId "
            + "AND c.paxId = :paxId ")
    public Case getCaseByCriteria(
            @Param("flightId") String flightId,
            @Param("paxId") String paxId);

    @Modifying
    @Transactional
    @Query("update Case set hitsDispositions = :hitsDispositionSet where id = :caseId")
    public Integer updateHitsDispositionsForCase(Long caseId, Set<HitsDisposition> hitsDispositionSet);

    @Modifying
    @Transactional
    @Query("update HitsDisposition set hitsDispositions = :hitsDispositionSet where id = :id")
    public Integer updateDispCommentsForHitsDisposition(Long id, Set<HitsDisposition> hitsDispositionSet);


}