/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.EncounteredStatusEnum;
import gov.gtas.model.Case;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.CaseDispositionStatus;
import gov.gtas.model.lookup.HitDispositionStatus;
import gov.gtas.services.dto.CaseCommentRequestDto;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.vo.passenger.OneDayLookoutVo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

import gov.gtas.model.lookup.RuleCat;
import java.util.Map;
import java.util.Set;

import static gov.gtas.constant.GtasSecurityConstants.*;

public interface CaseDispositionService {

    @PreAuthorize(PRIVLEGES_ADMIN_AND_MANAGE_RULES_AND_MANAGE_WATCH_LIST_AND_MANAGE_QUERIES_AND_VIEW_PASSENGER)
    public CasePageDto findAll(CaseRequestDto dto);

    @PreAuthorize(PRIVLEGES_ADMIN_AND_MANAGE_RULES_AND_MANAGE_WATCH_LIST_AND_MANAGE_QUERIES_AND_VIEW_PASSENGER)
    public CasePageDto findHitsDispositionByCriteria(CaseRequestDto dto);

    @PreAuthorize(ALL_PRIVS_AND_ONE_DAY)
    public CasePageDto caseWithoutHitDispositions(CaseRequestDto dto);

    public List<HitDispositionStatus> getHitDispositionStatuses();
    
    public List<CaseDispositionStatus> getCaseDispositionStatuses();

//    public Case create(Long flight_id, Long pax_id, List<Long> hit_ids);
//
//    public Case create(Long flight_id, Long pax_id, String paxName, String paxType, String hitDesc, List<Long> hit_ids);

    public Case create(Long flight_id, Long pax_id, String paxName, String paxType, String nationality, Date dob, String document, String hitDesc, 
                       List<Long> hit_ids, Map<Long, Case> caseMap, Map<Long, Flight> flightMap, Map<Long, Passenger> passengerMap, Map<Long, RuleCat> ruleCatMap);

    public Case createManualCase(Long flight_id, Long pax_id, Long rule_cat_id, String comments, String username);

    public Case createManualCaseAttachment(Long flight_id, Long pax_id, String paxName, String paxType, String nationality, Date dob, String document, String hitDesc, List<Long> hit_ids, String username, MultipartFile fileToAttach);

    public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id);

    public Case addGeneralCaseComment(CaseCommentRequestDto caseCommentRequestDto);

    public Case addCaseComments(Long caseID, Long hit_id, String caseComments, String status, String validHit, MultipartFile fileToAttach, String username, String caseDisposition);

    public Passenger findPaxByID(Long id);

    public Flight findFlightByID(Long id);

//    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, Long hit_id);
//
//    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType, String hitDesc, Long hit_id);

    public List<Case> registerAndSaveNewCaseFromFuzzyMatching(Long flight_id, Long pax_id, String paxName, String paxType, String nationality, Date dob, String document,
                                                   String hitDesc, Long hit_id,Map<Long, Case> caseMap, Map<Long, Flight> flightMap, 
                                                   Map<Long, Passenger> passengerMap, Map<Long, RuleCat> ruleCatMap);

    public Case registerCaseFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType, String nationality, Date dob, String document,
                                                 String hitDesc, Long hit_id, Map<Long, Case> caseMap, Map<Long, Flight> flightMap,
                                                 Map<Long, Passenger> passengerMap, Map<Long, RuleCat> ruleCatMap);

    public List<OneDayLookoutVo> getOneDayLookoutByDate(Date date);
    
    public List<OneDayLookoutVo> getOneDayLookoutByDateAndAirport(Date date, String airport);
    
    public Boolean updateDayLookoutFlag(Long caseId, Boolean flag);
    
    public List<Case> getCaseByPaxId(List<Long> paxIds);
    
    public List<Case> getCaseHistoryByPaxId(Long paxId);

    public String getAPISOnlyFlagAndVersion();
    
    public Date getCurrentServerTime();
    
    public Iterable<RuleCat> findAllRuleCat();

	public void updateEncounteredStatus(Long caseIdAsLong, EncounteredStatusEnum newEncounteredStatus);

    Set<Case> getOpenCasesWithTimeLeft();
}