/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.CountryRestore;
import org.springframework.data.repository.CrudRepository;

public interface CountryRestoreRepository extends CrudRepository<CountryRestore, Long>{

    default CountryRestore findOne(Long countryId)
    {
    	return findById(countryId).orElse(null);
    }
}
