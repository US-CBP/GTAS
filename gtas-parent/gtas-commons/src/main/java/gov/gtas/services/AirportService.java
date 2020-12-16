/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.vo.lookup.AirportVo;
import gov.gtas.vo.lookup.AirportLookupVo;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

public interface AirportService {

  @PreAuthorize(PRIVILEGE_ADMIN)
  public AirportVo create(AirportVo port);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public AirportVo delete(Long id);

  public List<AirportVo> findAll();

  @PreAuthorize(PRIVILEGE_ADMIN)
  public AirportVo update(AirportVo port);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public AirportVo restore(AirportVo airport);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public int restoreAll();

  @PreAuthorize(PRIVILEGE_ADMIN)
  public List<AirportVo> findAllNonArchived();

  public AirportVo findById(Long id);

  public List<AirportLookupVo> getAirportLookup();

  public AirportVo getAirportByThreeLetterCode(String airportCode);

  public AirportVo getAirportByFourLetterCode(String airportCode);

}
