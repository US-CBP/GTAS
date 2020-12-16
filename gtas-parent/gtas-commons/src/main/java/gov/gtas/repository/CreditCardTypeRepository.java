/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.CreditCardType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CreditCardTypeRepository extends CrudRepository<CreditCardType, Long> {

	default CreditCardType findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Query("Select cct From CreditCardType cct Where cct.archived = false OR cct.archived IS NULL")
    List<CreditCardType> findAllNonArchived();
}
