/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.repository;

import gov.gtas.model.HitViewStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.UserGroup;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Set;

public interface HitViewStatusRepository extends CrudRepository<HitViewStatus, Long> {

    Set<HitViewStatus> findAllByPassenger(Passenger p);
    
    @Query("select count(distinct hvs.passenger) from HitViewStatus hvs "
    		+ "left join hvs.passenger p left join p.flight f "
    		+ "where hvs.userGroup in :userGroups AND hvs.hitViewStatusEnum = 'NEW' "
    		+ "and f.mutableFlightDetails.etd > :etdDate and f.mutableFlightDetails.eta < :etaDate " +
            "and not hvs.hitDetail.hitType = 'PWL' ")
    int getHitViewCountWithNewStatus(@Param("userGroups") Set<UserGroup> userGroups, @Param("etdDate")Date etdDate, @Param("etaDate") Date etaDate);

    @Transactional
    @Query("select hvs from HitViewStatus hvs " +
            "left join fetch hvs.passenger p " +
            "left join fetch p.flight f " +
            "left join fetch p.pnrs " +
            "left join fetch p.passengerDetails pds " +
            "left join fetch p.documents doc " +
            "left join fetch f.mutableFlightDetails mf " +
            "left join fetch p.hitDetails hd " +
            "left join fetch hd.hitMaker hm "
    + " where hvs.userGroup in :userGroups "
    + " AND NOT hvs.lookoutStatusEnum = 'NOTPROMOTED' "
    + " AND NOT hvs.lookoutStatusEnum = 'DEMOTED' "
    + " AND (f.direction = 'O' AND mf.etd BETWEEN :startDate AND :endDate) "
    + " OR (f.direction ='I' AND mf.eta BETWEEN :startDate AND :endDate ) "
    + " OR (f.direction = 'A' AND (mf.etd BETWEEN :startDate AND :endDate "
    + " OR mf.eta BETWEEN :startDate AND :endDate))")
    Set<HitViewStatus> findAllWithNotClosedAndWithinRange(@Param("userGroups") Set<UserGroup> userGroups,
                                                           @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
