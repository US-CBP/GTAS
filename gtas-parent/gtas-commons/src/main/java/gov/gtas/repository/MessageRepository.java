/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;
import java.util.Set;

import gov.gtas.model.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MessageRepository<T extends Message> extends CrudRepository<T, Long> {
	Message findByHashCode(String hashCode);

	List<T> findTop500ByOrderByIdDesc();

	@Query("Select m from ApisMessage m " +
			"where m.id in :mIds " +
			"and m.id not in" +
			"(SELECT apis.id FROM ApisMessage apis left join apis.passengers pax " +
			"where pax.id in (select hd.passengerId from HitDetail  hd where hd.passenger in pax) and apis.id in :mIds)" )
	Set<T> messagesWithNoApisHits(@Param("mIds") Set<Long> mIds);

	@Query("Select m from Pnr m " +
			"where m.id in :mIds " +
			"and m.id not in" +
			"(SELECT pnr.id FROM Pnr pnr left join pnr.passengers pax " +
			"where pax.id in (select hd.passengerId from HitDetail  hd where hd.passenger in pax) and pnr.id in :mIds)")
			Set<T> messagesWithNoPnrHits(@Param("mIds") Set<Long> mIds);
}
