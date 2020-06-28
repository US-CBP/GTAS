/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MessageStatusRepository extends CrudRepository<MessageStatus, Long> {
	@Query(nativeQuery = true, value = "Select ms.*  " + "from message_status ms "
			+ "left join message m on ms.ms_message_id = m.id " + "where ms.ms_status = :msStatus "
			+ "order by m.create_date asc  limit :theLimit")
	List<MessageStatus> getMessagesFromStatus(@Param("msStatus") String msStatus, @Param("theLimit") Integer theLimit);

	@Modifying
	@Query("UPDATE MessageStatus ms " + "SET ms.messageStatusEnum = :statusEnum " + "WHERE ms.messageId IN :ids")
	void updateMessageWithIdAndEnum(@Param("ids") List<Long> ids, @Param("statusEnum") MessageStatusEnum statusEnum);


	@Query(nativeQuery = true, value = "Select ms.*  " + "from message_status ms "
			+ "left join message m on ms.ms_message_id = m.id " + "where ms.ms_status in :statusEnums "
			+ "and m.create_date <= :cutOffTime "
			+ "order by m.create_date asc  limit :messageLimit")
	List<MessageStatus> getMessagesToOutProcess(@Param("messageLimit") int messageLimit, @Param("cutOffTime") Date cutOffTime, @Param("statusEnums")List<String> statusEnums);
}
