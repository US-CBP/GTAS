/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ApisMessageRepository extends MessageRepository<ApisMessage> {


    @Query("SELECT apis FROM ApisMessage apis WHERE apis.createDate >= current_date() - 1")
    public List<Message> getAPIsByDates();
}