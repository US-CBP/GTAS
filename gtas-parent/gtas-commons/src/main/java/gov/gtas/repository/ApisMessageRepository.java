/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Message;
import gov.gtas.model.Pnr;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ApisMessageRepository extends MessageRepository<ApisMessage> {
	@Query("SELECT apis FROM ApisMessage apis join apis.passengers pax join apis.flights f where pax.id = :passengerId and f.id = :flightId")
	List<ApisMessage> findByFlightIdAndPassengerId(@Param("flightId") Long flightId,
			@Param("passengerId") Long passengerId);
    @Query("SELECT apis FROM ApisMessage apis WHERE apis.createDate >= current_date() - 1")
    public List<Message> getAPIsByDates();
}