/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *  
 */

package gov.gtas.repository;


import gov.gtas.model.PendingHitDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PendingHitDetailRepository extends CrudRepository<PendingHitDetails, Long> {

	//A direct query fails to auto parse as a long (defaulting to big int). Forced to use Number interface instead.
	@Query(nativeQuery = true, value = "SELECT DISTINCT(phd.flight) FROM pending_hit_detail phd "
			+ "ORDER BY phd.created_date ASC LIMIT :theLimit")
    Set<Number> getFlightsWithPendingHitsByLimit(@Param("theLimit") Integer theLimit);

	@Query("SELECT phd from PendingHitDetails phd where phd.flightId in :flightIds")
	List<PendingHitDetails> getPendingHitDetailsByFlightIds(@Param("flightIds") Set<Long> flightIds);

}
