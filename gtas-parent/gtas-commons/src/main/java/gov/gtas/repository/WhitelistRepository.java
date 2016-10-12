/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.model.Whitelist;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * The Interface WhitelistRepository.
 */
public interface WhitelistRepository extends CrudRepository<Whitelist, Long> {

	public List<Whitelist> findBydeleted(YesNoEnum deleted);
}
