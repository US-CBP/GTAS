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
}
