/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.vo.lookup.CountryVo;
import gov.gtas.vo.lookup.CountryLookupVo;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

public interface CountryService {
  @PreAuthorize(PRIVILEGE_ADMIN)
  public CountryVo create(CountryVo country);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public CountryVo delete(Long id);

  public List<CountryVo> findAll();

  @PreAuthorize(PRIVILEGE_ADMIN)
  public CountryVo update(CountryVo country);

  public CountryVo findById(Long id);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public CountryVo restore(CountryVo country);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public int restoreAll();

  public List<CountryLookupVo> getCountryLookup();

  public CountryVo getCountryByTwoLetterCode(String country);

  public CountryVo getCountryByThreeLetterCode(String country);
}
