/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Case;
import gov.gtas.model.Passenger;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER;

public interface CaseDispositionService {

    @PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER)
    public CasePageDto findAll(CaseRequestDto dto);

    @PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER)
    public Case findHitsDispositionByCriteria(CaseRequestDto dto);

    public Case create(Long flight_id, Long pax_id, List<Long> hit_ids);

    public Case create(Long flight_id, Long pax_id, String paxName, String paxType, String hitDesc, List<Long> hit_ids);

    public Case create(Long flight_id, Long pax_id, String paxName, String paxType, String citizenshipCountry, Date dob, String document, String hitDesc, List<Long> hit_ids);

    public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id);

    public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id, String caseComments, String status, String validHit);

    public Passenger findPaxByID(Long id);

    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, Long hit_id);

    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType, String hitDesc, Long hit_id);

    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType, String citizenshipCountry, Date dob, String document, String hitDesc, Long hit_id);
}