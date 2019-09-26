/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Disposition;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface DispositionRepository extends CrudRepository<Disposition, Long> {

	@Query("SELECT d.paxId from Disposition d where d.paxId in :existingPaxId")
	Set<Long> getExisitngPaxIds(@Param("existingPaxId") Set<Long> existingPaxID);
}
