/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.CreditCardType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CreditCardTypeRepository extends CrudRepository<CreditCardType, Long> {

	default CreditCardType findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Query("SELECT c FROM CreditCardType c WHERE c.updatedAt >= :updated_at")
	public List<CreditCardType> findAllUpdated(@Param("updated_at") Date updated_at);

}
