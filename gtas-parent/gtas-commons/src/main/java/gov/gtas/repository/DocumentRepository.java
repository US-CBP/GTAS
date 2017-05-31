/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Document;
import gov.gtas.model.Passenger;

public interface DocumentRepository extends CrudRepository<Document, Long>{
    @Query("SELECT d FROM Document d WHERE passenger_id = :id")
    public List<Document> getPassengerDocuments(@Param("id") Long id);
    
    public Document findByDocumentNumberAndPassenger(String documentNumber, Passenger passenger);
}
