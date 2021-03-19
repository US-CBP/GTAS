/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.LookoutLane;
import gov.gtas.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface POELaneRepository extends CrudRepository<LookoutLane, Long> {

    @Query("select l from LookoutLane l where l.archived = false  OR l.archived IS NULL")
    Iterable<LookoutLane> getNonArchivedLanes();
}
