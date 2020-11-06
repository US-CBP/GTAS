/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.List;
import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

import gov.gtas.vo.lookup.CarrierVo;
import gov.gtas.vo.lookup.CarrierLookupVo;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CarrierService {
  @PreAuthorize(PRIVILEGE_ADMIN)
  public CarrierVo create(CarrierVo carrier);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public CarrierVo delete(Long id);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public CarrierVo restore(CarrierVo carrier);

  @PreAuthorize(PRIVILEGE_ADMIN)
  public int restoreAll();

  public List<CarrierVo> findAll();

  @PreAuthorize(PRIVILEGE_ADMIN)
  public CarrierVo update(CarrierVo carrier);

  public CarrierVo findById(Long id);

  public List<CarrierLookupVo> getCarrierLookup();

  public CarrierVo getCarrierByTwoLetterCode(String carrierCode);

  public CarrierVo getCarrierByThreeLetterCode(String carrierCode);

}
