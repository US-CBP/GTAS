/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.CarrierRestore;

import org.springframework.data.repository.CrudRepository;

public interface CarrierRestoreRepository extends CrudRepository<CarrierRestore, Long>{

    default CarrierRestore findOne(Long carrierId)
    {
    	return findById(carrierId).orElse(null);
    }
}
