/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Case;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER;

public interface CaseDispositionService {

    @PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER)
    public CasePageDto findAll(CaseRequestDto dto);

    public Case create(Long flight_id, Long pax_id, List<Long> hit_ids);

    public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id);

    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, Long hit_id);
}