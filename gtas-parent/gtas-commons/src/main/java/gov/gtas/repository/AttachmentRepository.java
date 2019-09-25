/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Attachment;

public interface AttachmentRepository extends CrudRepository<Attachment, Long> {

	@Query("FROM Attachment WHERE passenger.id = :passengerId")
	public List<Attachment> findAllAttachmentsByPassengerId(@Param("passengerId") Long passengerId);

	@Query("FROM Attachment WHERE id = :idParam")
	public Attachment findByIntegerId(@Param("idParam") Integer id);

}
