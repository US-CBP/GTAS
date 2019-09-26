/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.HitsDisposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HitsDispositionRepository extends JpaRepository<HitsDisposition, Long> {

	@Query("SELECT h FROM HitsDisposition h where h.hitId = (:hitId)")
	public HitsDisposition findByCriteria(@Param("hitId") Long hitId);
}
