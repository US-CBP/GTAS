/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Address;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface AddressRepository extends CrudRepository<Address, Long> {
	List<Address> findByLine1AndCityAndStateAndPostalCodeAndCountry(String line1, String city, String state,
			String postalCode, String country);

	List<Address> findByLine1AndCityAndStateAndPostalCodeAndCountryAndFlightId(String line1, String city, String state,
			String postalCode, String country, Long flightId);

	@Query("SELECT add " +
			"from Address add " +
			"left join fetch add.pnrs addPnr " +
			"left join fetch addPnr.passengers " +
			"where add.flightId in :flightIds " +
			"and addPnr.id in :pnrIds")
    Set<Address> findAddressesToDelete(@Param("flightIds")Set<Long> flightIds, @Param("pnrIds") Set<Long> pnrIds);
}
