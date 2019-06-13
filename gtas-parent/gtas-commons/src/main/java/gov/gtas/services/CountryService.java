/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Country;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

public interface CountryService {
  @PreAuthorize(PRIVILEGE_ADMIN)
  public Country create(Country country);
  @PreAuthorize(PRIVILEGE_ADMIN)
  public Country delete(Long id);
  public List<Country> findAll();
  @PreAuthorize(PRIVILEGE_ADMIN)
  public Country update(Country country) ;
  public Country findById(Long id);
  @PreAuthorize(PRIVILEGE_ADMIN)
  public Country restore(Country country);
  @PreAuthorize(PRIVILEGE_ADMIN)
  public int restoreAll();
  public Country getCountryByTwoLetterCode(String country);
  public Country getCountryByThreeLetterCode(String country);
}
