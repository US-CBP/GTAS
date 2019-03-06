/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Address;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, Long>{
    List<Address> findByLine1AndCityAndStateAndPostalCodeAndCountry(
            String line1, String city, String state, String postalCode, String country);
}
