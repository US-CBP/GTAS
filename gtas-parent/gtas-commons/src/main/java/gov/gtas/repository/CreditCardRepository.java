/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.CreditCard;

public interface CreditCardRepository extends CrudRepository<CreditCard, Long> {
	List<CreditCard> findByCardTypeAndNumberAndExpiration(String cardType, String number, Date expiration);

	List<CreditCard> findByCardTypeAndNumberAndExpirationAndFlightId(String cardType, String number, Date expiration,
			Long flightId);
}
