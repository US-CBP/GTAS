/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.vo.lookup.CreditCardTypeVo;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

public interface CreditCardTypeService {
  @PreAuthorize(PRIVILEGE_ADMIN)
  public CreditCardTypeVo create(CreditCardTypeVo cctype);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public CreditCardTypeVo delete(Long id);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public CreditCardTypeVo restore(CreditCardTypeVo cctype);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public int restoreAll();

  public List<CreditCardTypeVo> findAll();

  @PreAuthorize(PRIVILEGE_ADMIN)
  public CreditCardTypeVo update(CreditCardTypeVo cctype);

  public CreditCardTypeVo findById(Long id);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public List<CreditCardTypeVo> findAllNonArchived();

//  public CreditCardTypeVo getCreditCardTypeByCode(String code);

}
