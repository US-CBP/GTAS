/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;
import gov.gtas.model.lookup.Carrier;
import java.util.List;
import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;
import org.springframework.security.access.prepost.PreAuthorize;


public interface CarrierService {
  	@PreAuthorize(PRIVILEGE_ADMIN)
    public Carrier create(Carrier carrier);
    @PreAuthorize(PRIVILEGE_ADMIN)
    public Carrier delete(Long id);
    @PreAuthorize(PRIVILEGE_ADMIN)
    public Carrier restore(Carrier carrier);
    @PreAuthorize(PRIVILEGE_ADMIN)
    public int restoreAll();
    public List<Carrier> findAll();
    @PreAuthorize(PRIVILEGE_ADMIN)
    public Carrier update(Carrier carrier);
    public Carrier findById(Long id);
    public Carrier getCarrierByTwoLetterCode(String carrierCode);
    public Carrier getCarrierByThreeLetterCode(String carrierCode);

}
