/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.RuleCat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RuleCatRepository extends CrudRepository<RuleCat, Long> {

	@Query("SELECT rc FROM RuleCat rc where rc.catId = :catId")
	List<RuleCat> findRuleCatByCatId(@Param("catId") Long catId);

	default RuleCat findOne(Long ruleCatId) {
		return findById(ruleCatId).orElse(null);
	}
}
