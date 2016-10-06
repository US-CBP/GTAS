/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.model.Whitelist;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * The Interface WhitelistRepository.
 */
public interface WhitelistRepository extends CrudRepository<Whitelist, Long> {

	@Query("SELECT wl FROM Whitelist wl where wl.deleted = :deleted")
	public List<Whitelist> findAllbydeleted(@Param("deleted") YesNoEnum deleted);
}
