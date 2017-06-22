/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
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
