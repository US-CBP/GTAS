/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;


import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageStatusRepository  extends CrudRepository<MessageStatus, Long> {
    @Query("SELECT ms FROM MessageStatus ms WHERE ms.messageStatusEnum=:status")
    List<MessageStatus> getMessagesFromStatus(@Param("status") MessageStatusEnum status);

}
