/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Phone;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface PhoneRepository extends CrudRepository<Phone, Long> {
	List<Phone> findByNumber(String number);

	List<Phone> findByNumberAndFlightId(String number, Long flightId);


	@Query("Select phone from Phone phone " +
			"left join fetch phone.pnrs phonePnrs " +
			"left join fetch phonePnrs.passengers " +
			"where phonePnrs.id in :pnrIds " +
			"and phone.flightId in :flightIds")
	Set<Phone> findPhonesFromPnr(@Param("flightIds")Set<Long> flightIds,@Param("pnrIds") Set<Long> pnrIds);
}
