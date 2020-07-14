/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.CreditCard;
import org.springframework.data.repository.query.Param;

public interface CreditCardRepository extends CrudRepository<CreditCard, Long> {
	List<CreditCard> findByCardTypeAndNumberAndExpiration(String cardType, String number, Date expiration);

	List<CreditCard> findByCardTypeAndNumberAndExpirationAndFlightId(String cardType, String number,  Date expiration,
			Long flightId);

	@Query("SELECT cc " +
			"from CreditCard cc " +
			"left join fetch cc.pnrs ccPnrs " +
			"left join fetch ccPnrs.passengers " +
			"where cc.flightId in :flightIds " +
			"and ccPnrs.id in :pnrIds")
	Set<CreditCard> findCreditCardToDelete(@Param("flightIds")Set<Long> flightIds, @Param("pnrIds") Set<Long> pnrIds);
}
