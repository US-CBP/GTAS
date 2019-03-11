/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;


import gov.gtas.model.MessageStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageStatusRepository  extends CrudRepository<MessageStatus, Long> {
    @Query(nativeQuery = true, value =
            "Select ms.*  " +
            "from message_status ms " +
            "left join message m on ms.ms_message_id = m.id " +
            "where ms.ms_status = :msStatus " +
            "order by m.create_date asc  limit :theLimit")
    List<MessageStatus> getMessagesFromStatus(@Param("msStatus") String msStatus, @Param("theLimit") Long theLimit);

}
