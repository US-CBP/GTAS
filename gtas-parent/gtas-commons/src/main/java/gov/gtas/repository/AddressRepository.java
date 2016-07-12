/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Address;

public interface AddressRepository extends CrudRepository<Address, Long>{
    public Address findByLine1AndCityAndStateAndPostalCodeAndCountry(
            String line1, String city, String state, String postalCode, String country);
}
