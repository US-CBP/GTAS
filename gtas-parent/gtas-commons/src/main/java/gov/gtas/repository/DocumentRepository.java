/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;
import java.util.Set;

import gov.gtas.enumtype.MessageType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Document;
import gov.gtas.model.Passenger;

import javax.transaction.Transactional;

public interface DocumentRepository extends CrudRepository<Document, Long> {
	@Query("SELECT d FROM Document d WHERE d.passengerId = :id")
	public List<Document> getPassengerDocuments(@Param("id") Long id);

	public List<Document> findByDocumentNumberAndPassengerAndMessageType(String documentNumber, Passenger passenger, MessageType messageType);

	@Transactional
	@Query("Select d from Document d where d.passengerId in :paxIds")
	Set<Document> getAllByPaxId(@Param("paxIds") Set<Long> paxIds);

	@Query("Select d from Document d left join fetch d.messages where d.passengerId in :passengerIds")
    Set<Document> getDocumentsByPaxIdFlightId(@Param("passengerIds") Set<Long> passengerIds);
}
