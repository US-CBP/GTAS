/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;
import gov.gtas.model.lookup.CarrierRestore;
import java.util.List;
import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CarrierRestoreService {
    public List<CarrierRestore> findAll();
    public CarrierRestore findById(Long id);
}
