/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.AuditRecord;
import gov.gtas.model.InvalidObjectInfo;
import org.springframework.data.repository.CrudRepository;

public interface ErrorLoggingRepository extends CrudRepository<InvalidObjectInfo, Long>{
	
    default InvalidObjectInfo findOne(Long id)
    {
    	return findById(id).orElse(null);
    }

}
