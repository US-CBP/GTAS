/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.lookup.DispositionStatus;

public interface DispositionStatusRepository extends CrudRepository<DispositionStatus, Long>{
}
