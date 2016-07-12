/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.DashboardMessageStats;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardMessageStatsRepository extends CrudRepository<DashboardMessageStats, Long>  {

    @Query("SELECT d FROM DashboardMessageStats d WHERE UPPER(d.messageType) = UPPER(:message_type)")
    public List<DashboardMessageStats> getMessages(@Param("message_type") String message_type);


}
