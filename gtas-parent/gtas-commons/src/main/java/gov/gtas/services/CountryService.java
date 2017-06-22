/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Country;

import java.util.List;

public interface CountryService {

    public Country create(Country country);
    public Country delete(Long id);
    public List<Country> findAll();
    public Country update(Country country) ;
    public Country findById(Long id);
    public Country getCountryByTwoLetterCode(String country);
    public Country getCountryByThreeLetterCode(String country);
}
