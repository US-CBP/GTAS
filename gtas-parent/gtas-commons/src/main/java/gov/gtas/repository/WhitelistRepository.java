/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Whitelist;

import org.springframework.data.repository.CrudRepository;

public interface WhitelistRepository extends CrudRepository<Whitelist, Long> {

}
