/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.sql.Blob;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import gov.gtas.model.*;
import gov.gtas.repository.*;
import org.apache.commons.collections4.IteratorUtils;
import gov.gtas.services.dto.CaseCommentRequestDto;
import gov.gtas.vo.passenger.*;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import gov.gtas.constant.OneDayLookoutContants;
import gov.gtas.enumtype.CaseDispositionStatusEnum;
import gov.gtas.enumtype.EncounteredStatusEnum;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.model.lookup.CaseDispositionStatus;
import gov.gtas.model.lookup.DispositionStatusCode;
import gov.gtas.model.lookup.HitDispositionStatus;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;

import static java.util.Comparator.comparing;

@Service
public class CaseDispositionServiceImpl implements CaseDispositionService {

    private static final Logger logger = LoggerFactory.getLogger(CaseDispositionServiceImpl.class);

    private static Set casesToCommit = new HashSet<Case>();
    private static final String INITIAL_COMMENT = "Initial Comment";
    private static final String UPDATED_BY_INTERNAL = "Internal";
    private static final String CASE_CREATION_MANUAL_DESC = "Agent Created Case";
    private static final String WL_ITEM_PREFIX = "wl_item";
    private static final String GRAPH_ITEM_PREFIX = "graph_hit";
    private static final String WATCHLIST = "WATCHLIST";
    private static final String GRAPH_HIT = "GRAPH_HIT";
    private static final String UDR = "UDR";
    private static final String MANUAL = "MANUAL";

    @Resource
    private CaseDispositionRepository caseDispositionRepository;
    @Resource
    private FlightRepository flightRepository;
    @Resource
    private PassengerRepository passengerRepository;
    @Resource
    private AttachmentRepository attachmentRepository;
    @Resource
    private HitDetailRepository hitDetailRepository;
    @Resource
    private RuleCatRepository ruleCatRepository;
    @Resource
    private HitDispositionStatusRepository hitDispRepo;
    @Resource
    private CaseDispositionStatusRepository caseDispositionStatusRepository;
    @Resource
    private HitsDispositionRepository hitsDispositionRepository;
    @Resource
    private HitsDispositionCommentsRepository hitsDispositionCommentsRepository;
    @Resource
    private FlightPaxRepository flightPaxRepository;
    @Autowired
    public RuleCatService ruleCatService;
    @Autowired
    private PassengerResolverService passengerResolverService;
    @Resource
    private AppConfigurationRepository appConfigurationRepository;
    @Autowired
    private AppConfigurationService appConfigurationService;

    @Autowired
    private FlightService flightService;

    public CaseDispositionServiceImpl() {
    }

    // @Override
    // public Case create(Long flight_id, Long pax_id, List<Long> hit_ids) {
    // Case aCase = new Case();
    // HitsDisposition hitDisp = new HitsDisposition();
    // HitsDispositionComments hitsDispositionComments = new
    // HitsDispositionComments();
    // Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
    // Set<HitsDispositionComments> hitsDispCommentsSet = new
    // HashSet<HitsDispositionComments>();
    // Long highPriorityRuleCatId = 1L;
    // aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
    // aCase.setFlightId(flight_id);
    // aCase.setPaxId(pax_id);
    // aCase.setStatus(DispositionStatusCode.NEW.toString());
    // for (Long _tempHitId : hit_ids) {
    // hitDisp = new HitsDisposition();
    // hitsDispCommentsSet = new HashSet<>();
    // hitDisp.setHitId(_tempHitId);
    // hitDisp.setStatus(DispositionStatusCode.NEW.toString());
    // hitsDispositionComments = new HitsDispositionComments();
    // hitsDispositionComments.setHitId(_tempHitId);
    // hitsDispositionComments.setComments(INITIAL_COMMENT);
    // hitsDispCommentsSet.add(hitsDispositionComments);
    //
    // hitDisp.addHitsDispositionComments(hitsDispositionComments);
    // hitsDispSet.add(hitDisp);
    // }
    // // aCase.setHitsDispositions(hitsDispSet);
    // for (HitsDisposition _tempHit : hitsDispSet)
    // aCase.addHitsDisposition(_tempHit);
    //
    // caseDispositionRepository.save(aCase);
    // return aCase;
    // }
    //
    // @Override
    // public Case create(Long flight_id, Long pax_id, String paxName, String
    // paxType, String hitDesc,
    // List<Long> hit_ids) {
    // Case aCase = new Case();
    // HitsDisposition hitDisp = new HitsDisposition();
    // HitsDispositionComments hitsDispositionComments = new
    // HitsDispositionComments();
    // Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
    // Set<HitsDispositionComments> hitsDispCommentsSet = new
    // HashSet<HitsDispositionComments>();
    // Long highPriorityRuleCatId = 1L;
    // aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
    // aCase.setFlightId(flight_id);
    // aCase.setPaxId(pax_id);
    // aCase.setPaxName(paxName);
    // aCase.setPaxType(paxType);
    // aCase.setStatus(DispositionStatusCode.NEW.toString());
    // for (Long _tempHitId : hit_ids) {
    // hitDisp = new HitsDisposition();
    // hitsDispCommentsSet = new HashSet<>();
    // hitDisp.setHitId(_tempHitId);
    // hitDisp.setDescription(hitDesc);
    // hitDisp.setStatus(DispositionStatusCode.NEW.toString());
    // hitsDispositionComments = new HitsDispositionComments();
    // hitsDispositionComments.setHitId(_tempHitId);
    // hitsDispositionComments.setComments(INITIAL_COMMENT);
    // hitDisp.addHitsDispositionComments(hitsDispositionComments);
    // hitsDispSet.add(hitDisp);
    // }
    // // aCase.setHitsDispositions(hitsDispSet);
    // for (HitsDisposition _tempHit : hitsDispSet)
    // aCase.addHitsDisposition(_tempHit);
    //
    // caseDispositionRepository.save(aCase);
    // return aCase;
    // }

    @Override
    public Case create(
            Long flight_id,
            Long pax_id,
            String paxName,
            String paxType,
            String nationality,
            Date dob,
            String document,
            String hitDesc,
            List<Long> hit_ids,
            Map<Long, Case> caseMap,
            Map<Long, Flight> flightMap,
            Map<Long, Passenger> passengerMap,
            Map<Long, RuleCat> ruleCatMap) {

        Case aCase;
        List<Case> _tempCases;
        Long highPriorityRuleCatId = 1L;
        if (caseMap == null || caseMap.isEmpty() || !caseMap.containsKey(pax_id)) {
            /*
             * fetch existing case for the flight-passenger where the status is OPEN or NEW
             * data structure returns list as many results are possible. Only 1 result is expected.
             * More are possible under the condition of a user changing a closed case to open status AFTER
             * getting a new case generated (or after generating a manual case).
             *
             */
            _tempCases = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id,
                    Arrays.asList(CaseDispositionStatusEnum.NEW.getType(), CaseDispositionStatusEnum.OPEN.getType()));
            if (_tempCases != null && _tempCases.size() > 0) {
                aCase = _tempCases.get(0); //Will be expecting a list of 1 item, therefore get at index 0.
            } else {
                aCase = new Case();
                aCase.setUpdatedAt(new Date());
                aCase.setUpdatedBy(UPDATED_BY_INTERNAL); // @ToDo change this to pass-by-value in the next release
                aCase.setFlightId(flight_id);
                aCase.setPaxId(pax_id);
                aCase.setPaxName(paxName);
                aCase.setPaxType(paxType);
                aCase.setNationality(nationality);
                aCase.setDocument(document);
                aCase.setDob(dob);
                aCase.setStatus(DispositionStatusCode.NEW.toString());
                aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
                populatePassengerDetailsInCase(aCase, flight_id, pax_id, flightMap, passengerMap);
            }
        } else {
            aCase = caseMap.get(pax_id);
        }

        HitsDisposition hitDisp;
        Set<HitsDisposition> hitsDispSet = new HashSet<>();
        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            if (hitDesc.startsWith(WL_ITEM_PREFIX)) {
                hitDisp.setRuleCat(ruleCatMap.get(1L));
                hitDisp.setRuleType(WATCHLIST);
                hitDesc = hitDesc.substring(7);
            } else if (hitDesc.startsWith(GRAPH_ITEM_PREFIX)) {
                hitDisp.setRuleCat(ruleCatMap.get(1L));
                hitDisp.setRuleType(GRAPH_HIT);
                hitDesc = hitDesc.substring(GRAPH_ITEM_PREFIX.length());
            } else {
                hitDisp.setRuleCat(ruleCatMap.get(1L));
                hitDisp.setRuleType(UDR);
                pullRuleCategory(hitDisp, getRuleCatId(_tempHitId), ruleCatMap);
            }

            hitDisp.setHitId(_tempHitId);
            hitDisp.setDescription(hitDesc);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitDisp.setUpdatedAt(new Date());
            hitDisp.setUpdatedBy(UPDATED_BY_INTERNAL);
            hitsDispSet.add(hitDisp);
        }

        for (HitsDisposition _tempHit : hitsDispSet) {
            if (!aCase.getHitsDispositions().contains(_tempHit)) {
                aCase.addHitsDisposition(_tempHit);
                aCase.setSaveCase(true);
            }
        }

        if (caseMap != null) {
            caseMap.put(pax_id, aCase);
        }
        return aCase;
    }

    @Override
    public Case createManualCase(Long flight_id, Long pax_id, Long rule_cat_id, String comments, String username) {

        Case aCase = new Case();
        Long _tempHitIdForManualCase = 9999L;
        List<Case> _tempCases = null;
        /*
         * fetch existing cases for the flight-passenger where the status is OPEN or NEW
         *
         */
        _tempCases = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id,
                Arrays.asList(CaseDispositionStatusEnum.NEW.getType(), CaseDispositionStatusEnum.OPEN.getType()));

        if (_tempCases != null && _tempCases.size() > 0) {
            aCase = _tempCases.get(0);
        }
        Long highPriorityRuleCatId = 1L;
        ArrayList<Long> hit_ids = new ArrayList<>();
        Passenger pax = passengerRepository.getFullPassengerById(pax_id);
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
        aCase.setUpdatedAt(new Date());
        aCase.setUpdatedBy(username);
        aCase.setFlightId(flight_id);
        aCase.setPaxId(pax_id);
        aCase.setPaxName(pax.getPassengerDetails().getFirstName() + " " + pax.getPassengerDetails().getLastName());
        populatePassengerDetailsInCase(aCase, flight_id, pax_id);
        aCase.setPaxType(pax.getPassengerDetails().getPassengerType());
        aCase.setNationality(pax.getPassengerDetails().getNationality());
        aCase.setDocument(((Document) pax.getDocuments().parallelStream().findFirst().orElse(new Document("xxxxxxxxx")))
                .getDocumentNumber());
        aCase.setDob(pax.getPassengerDetails().getDob());
        aCase.setStatus(DispositionStatusCode.NEW.toString());

        hit_ids.add(_tempHitIdForManualCase); // Manual Distinction

        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            hitDisp.setRuleType(MANUAL);
            // pullRuleCategory(hitDisp, rule_cat_id);
            // hitDisp.getRuleCat().setHitsDispositions(null);
            highPriorityRuleCatId = 1L;
            hitsDispCommentsSet = new HashSet<>();
            hitDisp.setHitId(_tempHitId);
            // pullRuleCategory(hitDisp, rule_cat_id);
            hitDisp.setDescription(CASE_CREATION_MANUAL_DESC);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitDisp.setUpdatedAt(new Date());
            hitDisp.setUpdatedBy(UPDATED_BY_INTERNAL);
            RuleCat _tempRuleCat = ruleCatRepository.findById(rule_cat_id).orElse(null);
            if (_tempRuleCat != null)
                hitDisp.setRuleCat(ruleCatRepository.findById(rule_cat_id).orElse(null));
            hitsDispositionComments = new HitsDispositionComments();
            hitsDispositionComments.setHitId(_tempHitId);
            hitsDispositionComments.setComments(comments);
            hitsDispositionComments.setUpdatedBy(UPDATED_BY_INTERNAL);
            hitsDispositionComments.setUpdatedAt(new Date());
            hitsDispositionComments.setCreatedBy(UPDATED_BY_INTERNAL);
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }

        if (aCase.getHighPriorityRuleCatId() == null)
            aCase.setHighPriorityRuleCatId(rule_cat_id);
        if (aCase.getHighPriorityRuleCatId() != null && aCase.getHighPriorityRuleCatId().equals(1L))
            aCase.setHighPriorityRuleCatId(rule_cat_id);
        else if (aCase.getHighPriorityRuleCatId() != null && (aCase.getHighPriorityRuleCatId() > rule_cat_id)
                && rule_cat_id != 1)
            aCase.setHighPriorityRuleCatId(rule_cat_id);
        if (aCase.getHitsDispositions() != null)
            aCase.getHitsDispositions().addAll(hitsDispSet);
        else
            aCase.setHitsDispositions(hitsDispSet);

        // fix to adjust to the recent flight mappings
        aCase.setFlight(null);

        aCase = caseDispositionRepository.save(aCase);

        // _tempCase = null;
        // _tempCase = caseDispositionRepository.getOne(aCase.getId());
        //
        // if(_tempCase!=null){
        // for(HitsDisposition _tempHitDisp : _tempCase.getHitsDispositions()) {
        // if((_tempHitDisp.getHitId() == 9999L))
        // pullRuleCategory(_tempHitDisp, rule_cat_id);
        // }
        // caseDispositionRepository.save(_tempCase);
        // }
        return aCase;
    }

    @Override
    public Case createManualCaseAttachment(Long flight_id, Long pax_id, String paxName, String paxType,
                                           String nationality, Date dob, String document, String hitDesc, List<Long> hit_ids, String username,
                                           MultipartFile fileToAttach) {

        Case aCase = new Case();
        List<Case> _tempCases = null;
        Long highPriorityRuleCatId = 1L;
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
        aCase.setUpdatedAt(new Date());
        aCase.setUpdatedBy(username);
        aCase.setFlightId(flight_id);
        aCase.setPaxId(pax_id);
        aCase.setPaxName(paxName);
        populatePassengerDetailsInCase(aCase, flight_id, pax_id);
        aCase.setPaxType(paxType);
        aCase.setNationality(nationality);
        aCase.setDocument(document);
        aCase.setDob(dob);
        aCase.setStatus(DispositionStatusCode.NEW.toString());
        _tempCases = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id,
                Arrays.asList(CaseDispositionStatusEnum.NEW.getType(), CaseDispositionStatusEnum.OPEN.getType()));
        if (_tempCases != null && _tempCases.size() > 0) {
            aCase = _tempCases.get(0);
        }

        // redundant at this time
        // contextCases(aCase);

        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            pullRuleCategory(hitDisp, getRuleCatId(_tempHitId), null);
            highPriorityRuleCatId = 1L;
            hitsDispCommentsSet = new HashSet<>();
            hitDisp.setHitId(_tempHitId);
            hitDisp.setDescription(hitDesc);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitDisp.setUpdatedAt(new Date());
            hitDisp.setUpdatedBy(UPDATED_BY_INTERNAL);
            // RuleCat _tempRuleCat = ruleCatRepository.findOne(rule_cat_id);
            // if(_tempRuleCat!=null)hitDisp.setRuleCat(ruleCatRepository.findOne(rule_cat_id));
            hitsDispositionComments = new HitsDispositionComments();
            hitsDispositionComments.setHitId(_tempHitId);
            hitsDispositionComments.setComments(INITIAL_COMMENT);
            hitsDispositionComments.setUpdatedBy(UPDATED_BY_INTERNAL);
            hitsDispositionComments.setUpdatedAt(new Date());
            hitsDispositionComments.setCreatedBy(UPDATED_BY_INTERNAL);
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }
        aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
        if (aCase.getHitsDispositions() != null)
            aCase.getHitsDispositions().addAll(hitsDispSet);
        else
            aCase.setHitsDispositions(hitsDispSet);
        caseDispositionRepository.save(aCase);
        return aCase;
    }

    @Override
    public Iterable<RuleCat> findAllRuleCat() {
        Iterable<RuleCat> ruleCatList = ruleCatRepository.findAll();
        return ruleCatList;
    }

    /**
     * Utility method to manage cases to persist
     *
     * @param aCase
     */
    private void contextCases(Case aCase) {
        try {
            if (casesToCommit != null) {
                final Case _tempCaseToCompare = aCase;
                Case existingCase = (Case) casesToCommit.stream().filter(x -> _tempCaseToCompare.equals(x)).findAny()
                        .orElse(null);
                if (existingCase != null)
                    aCase = existingCase;
                else
                    casesToCommit.add(aCase);
            }
        } catch (Exception ex) {
            logger.error("Error in context cases!", ex);
        }
    }

    /**
     * Utility method to pull Rule Cat
     *
     * @param hitDisp
     * @param id
     */
    public void pullRuleCategory(HitsDisposition hitDisp, Long id, Map<Long, RuleCat> ruleCatMap) {
        try {
            if (ruleCatMap == null) {
                if (id == null || (ruleCatRepository.findOne(id) == null))
                    hitDisp.setRuleCat(ruleCatRepository.findOne(1L));
                else
                    hitDisp.setRuleCat(ruleCatRepository.findOne(id));
            } else {
                if ((id == null) || (ruleCatMap.get(id) == null)) {
                    hitDisp.setRuleCat(ruleCatMap.get(1L));
                } else {
                    hitDisp.setRuleCat(ruleCatMap.get(id));
                }
            }
        } catch (Exception ex) {
            logger.error("error in pull rule category.", ex);
        }
    }

    /**
     * @param ruleId
     * @return
     */
    private Long getHighPriorityRuleCatId(Long ruleId) {
        try {
            return ruleCatService.fetchRuleCatPriorityIdFromRuleId(ruleId);
        } catch (Exception ex) {
            logger.error("error in high priority rule cat id", ex);
        }
        return 1L;
    }

    private Long getRuleCatId(Long ruleId) {
        try {
            return ruleCatService.fetchRuleCatIdFromNonUdrRuleId(ruleId);
        } catch (Exception ex) {
            logger.error("error in get rule cat id", ex);
        }
        return 1L;
    }

    @Override
    public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id) {

        Case aCase = new Case();
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();

        aCase.setFlightId(flight_id);
        aCase.setPaxId(pax_id);
        aCase.setStatus(DispositionStatusCode.NEW.toString());
        // aCase.setHitsDispositions(hitsDispSet);
        for (HitsDisposition _tempHit : hitsDispSet)
            aCase.addHitsDisposition(_tempHit);

        caseDispositionRepository.save(aCase);
        return aCase;
    }

    @Override
    @Transactional
    public Case addGeneralCaseComment(CaseCommentRequestDto caseCommentRequestDto) {
        Case aCase = caseDispositionRepository.caseWithComments(caseCommentRequestDto.getCaseId());
        if (caseCommentRequestDto.getCaseStatus() != null
                && !caseCommentRequestDto.getCaseStatus().equalsIgnoreCase(aCase.getCaseOfficerStatus())) {
            aCase.setCaseOfficerStatus(caseCommentRequestDto.getCaseStatus());
        }
        if (caseCommentRequestDto.getComment() != null) {
            CaseComment newCaseComment = new CaseComment();
            newCaseComment.setaCase(aCase);
            newCaseComment.setCreatedAt(new Date());
            newCaseComment.setCreatedBy(caseCommentRequestDto.getUser());
            newCaseComment.setCommentType(CommentType.GENERAL);
            newCaseComment.setComment(caseCommentRequestDto.getComment());
            aCase.getCaseComments().add(newCaseComment);
        }
        //Save comment through cascading case.
        return caseDispositionRepository.save(aCase);
    }

    @Override
    public Case addCaseComments(Long caseId, Long hit_id, String caseComments, String status, String validHit,
                                MultipartFile fileToAttach, String username, String caseDisposition) {
        Case aCase = new Case();
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();

        try {
            aCase = caseDispositionRepository.findOne(caseId); //getCaseByFlightIdAndPaxId(flight_id, pax_id, Arrays.asList(CaseDispositionStatusEnum.NEW.getType(),CaseDispositionStatusEnum.OPEN.getType())).stream().findFirst().get();


            if (aCase != null && status != null) { // set case status
                if (status.startsWith("Case"))
                    aCase.setStatus(status.substring(4));
                aCase.setDisposition(caseDisposition);
            }
            hitsDispCommentsSet = null;
            hitsDispSet = aCase.getHitsDispositions();
            logger.info("There are " + hitsDispSet.size() + " hit dispositions");
            for (HitsDisposition hit : hitsDispSet) {

                // if ((hit.getCaseId() == aCase.getId()) && (hit_id != null) && (hit.getHitId()
                // == hit_id)) {
                // (hit.getaCase().getId() == aCase.getId()) &&
                if ((hit_id != null) && (hit.getId().equals(hit_id))) {

                    if (caseComments != null) { // set comments
                        caseComments = caseComments.replaceAll("[^\\p{ASCII}]", "");
                        hitsDispositionComments = new HitsDispositionComments();
                        hitsDispositionComments.setHitId(hit_id);
                        hitsDispositionComments.setComments(caseComments);
                        hitsDispositionComments.setUpdatedBy(username);
                        hitsDispositionComments.setUpdatedAt(new Date());
                        hitsDispCommentsSet = hit.getDispComments();
                        // check whether attachment exists, if yes, populate
                        if (fileToAttach != null && !fileToAttach.isEmpty())
                            populateAttachmentsToCase(fileToAttach, hitsDispositionComments, aCase.getPaxId());
                        hitsDispCommentsSet.add(hitsDispositionComments);
                        hit.setDispComments(hitsDispCommentsSet);
                    }

                    if (status != null && !status.startsWith("Case")) { // set status
                        hit.setStatus(status);
                    }

                    if (validHit != null) {
                        hit.setValid(validHit);
                    }

                } // end of hit updates

            }

            // aCase.setHitsDispositions(hitsDispSet);
            for (HitsDisposition _tempHit : hitsDispSet)
                aCase.addHitsDisposition(_tempHit);

            if ((status != null) || (caseComments != null) || (validHit != null))
                caseDispositionRepository.save(aCase);

        } catch (Exception ex) {
            logger.error("Error adding case comments: ", ex);
        }

        aCase.getHitsDispositions().stream().forEach(x -> x.setaCase(null));

        return aCase;
    }

    /**
     * Utility method to persist attachment to each case comment
     *
     * @param file
     * @param _tempHitsDispComments
     * @throws Exception
     */
    private void populateAttachmentsToCase(MultipartFile file, HitsDispositionComments _tempHitsDispComments,
                                           Long pax_id) throws Exception {

        Attachment attachment = new Attachment();
        // Build attachment to be added to pax
        Set<Attachment> _tempAttachSet = new HashSet<Attachment>();
        if (_tempHitsDispComments.getAttachmentSet() != null)
            _tempAttachSet = _tempHitsDispComments.getAttachmentSet();
        attachment.setContentType(file.getContentType());
        attachment.setFilename(file.getOriginalFilename());
        attachment.setName(file.getName());
        byte[] bytes = file.getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
        attachment.setContent(blob);
        // Grab pax to add attachment to it
        Passenger pax = passengerRepository.findById(pax_id).orElse(null);
        attachment.setPassenger(pax);
        attachmentRepository.save(attachment);
        _tempAttachSet.add(attachment);
        _tempHitsDispComments.setAttachmentSet(_tempAttachSet);

    }

    // @Override
    // public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id,
    // Long hit_id) {
    // List<Case> _tempCaseList = new ArrayList<>();
    // List<Long> _tempHitIds = new ArrayList<>();
    //
    // _tempHitIds.add(hit_id);
    // _tempCaseList.add(create(flight_id, pax_id, _tempHitIds));
    //
    // return _tempCaseList;
    // }
    //
    // @Override
    // public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id,
    // String paxName, String paxType,
    // String hitDesc, Long hit_id) {
    // List<Case> _tempCaseList = new ArrayList<>();
    // List<Long> _tempHitIds = new ArrayList<>();
    //
    // _tempHitIds.add(hit_id);
    // _tempCaseList.add(create(flight_id, pax_id, paxName, paxType, hitDesc,
    // _tempHitIds));
    //
    // return _tempCaseList;
    // }

    @Override
    public Passenger findPaxByID(Long id) {
        return passengerRepository.findById(id).orElse(null);
    }

    @Override
    public Flight findFlightByID(Long id) {
        return flightRepository.findOne(id);
    }

    @Override
    public List<Case> registerAndSaveNewCaseFromFuzzyMatching(Long flight_id, Long pax_id, String paxName, String paxType,
                                                              String nationality, Date dob, String document, String hitDesc, Long hit_id, Map<Long, Case> caseMap,
                                                              Map<Long, Flight> flightMap, Map<Long, Passenger> passengerMap, Map<Long, RuleCat> ruleCatMap) {
        List<Case> _tempCaseList = new ArrayList<>();

        List<Long> _tempHitIds = new ArrayList<>();
        _tempHitIds.add(hit_id);


        _tempCaseList.add(create(
                flight_id,
                pax_id,
                paxName,
                paxType,
                nationality,
                dob,
                document,
                hitDesc,
                _tempHitIds,
                caseMap,
                flightMap,
                passengerMap,
                ruleCatMap));

        //Name match case depends on this save to create a case.
        caseDispositionRepository.saveAll(_tempCaseList);

        return _tempCaseList;
    }

    @Override
    public Case registerCaseFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType,
                                            String nationality, Date dob, String document, String hitDesc, Long hit_id, Map<Long, Case> caseMap,
                                            Map<Long, Flight> flightMap, Map<Long, Passenger> passengerMap, Map<Long, RuleCat> ruleCatMap) {
        List<Long> _tempHitIds = new ArrayList<>();
        _tempHitIds.add(hit_id);
        return create(
                flight_id,
                pax_id,
                paxName,
                paxType,
                nationality,
                dob,
                document,
                hitDesc,
                _tempHitIds,
                caseMap,
                flightMap,
                passengerMap,
                ruleCatMap);
    }


    @Override
    public CasePageDto findHitsDispositionByCriteria(CaseRequestDto dto) {
        Case aCase = caseDispositionRepository.caseWithCommentsAndHitDispositionsById(dto.getCaseId());
        List<CaseVo> vos = new ArrayList<>();
        CaseVo vo = new CaseVo();
        vo.setHitsDispositions(aCase.getHitsDispositions());
        aCase.getFlight().setPnrs(null);
        aCase.getFlight().setApis(null);
        aCase.getFlight().setAddress(null);
        aCase.getFlight().setBags(null);
        aCase.getFlight().setCreditCard(null);
        aCase.getFlight().setPhone(null);
        aCase.getFlight().setBookingDetails(null);
        vo.setHitsDispositionVos(returnHitsDisposition(aCase.getHitsDispositions()));
        vo.setGeneralCaseCommentVos(convertCommentsToVo(aCase.getCaseComments()));
        CaseDispositionServiceImpl.copyIgnoringNullValues(aCase, vo);
        manageHitsDispositionCommentsAttachments(vo.getHitsDispositions());
        vos.add(vo);
        return new CasePageDto(vos, 1L);
    }

    public CasePageDto caseWithoutHitDispositions(CaseRequestDto dto) {
        Case aCase = caseDispositionRepository.caseWithComments(dto.getCaseId());
        List<CaseVo> vos = new ArrayList<>();
        CaseVo vo = new CaseVo();
        aCase.getFlight().setPnrs(null);
        aCase.getFlight().setApis(null);
        aCase.getFlight().setAddress(null);
        aCase.getFlight().setCreditCard(null);
        aCase.getFlight().setBags(null);
        aCase.getFlight().setPhone(null);
        aCase.getFlight().setBookingDetails(null);
        vo.setGeneralCaseCommentVos(convertCommentsToVo(aCase.getCaseComments()));
        CaseDispositionServiceImpl.copyIgnoringNullValues(aCase, vo);
        vo.setHitsDispositions(null);
        vo.setHitsDispositionVos(null);
        vos.add(vo);
        return new CasePageDto(vos, 1L);
    }


    private Set<GeneralCaseCommentVo> convertCommentsToVo(Set<CaseComment> caseComments) {
        List<GeneralCaseCommentVo> generalCaseCommentVoSet = new ArrayList<>();
        for (CaseComment cc : caseComments) {
            GeneralCaseCommentVo generalCaseCommentVo = new GeneralCaseCommentVo();
            CaseDispositionServiceImpl.copyIgnoringNullValues(cc, generalCaseCommentVo);
            generalCaseCommentVoSet.add(generalCaseCommentVo);
        }
        generalCaseCommentVoSet.sort(comparing(GeneralCaseCommentVo::getCreatedAt).reversed());
        return new LinkedHashSet<>(generalCaseCommentVoSet);
    }

    /**
     * @param dto
     * @return
     */
    @Override
    public CasePageDto findAll(CaseRequestDto dto) {
        List<CaseVo> vos = new ArrayList<>();

        CasePageDto casePageDto = null;

        Pair<Long, List<Case>> tuple2 = caseDispositionRepository.findByCriteria(dto);
        for (Case f : tuple2.getRight()) {
            CaseVo vo = new CaseVo();
            f.getFlight().setPnrs(null); // TODO: need to cherry-pick the fields we need to copy to DTO, failed to
            // serialize the lazy loaded entities
            CaseDispositionServiceImpl.copyIgnoringNullValues(f, vo);
            vo.setHitsDispositions(null);
            vo.setGeneralCaseCommentVos(null);
            vo.setCurrentTime(new Date());
            vo.setFlightDirection(f.getFlight().getDirection());
            vo.setCountdownTime(f.getCountdown());
            vo = calculateCountDownDisplayString(vo);
            vos.add(vo);
        }

        casePageDto = new CasePageDto(vos, tuple2.getLeft());

        return casePageDto;
    }

    private CaseVo calculateCountDownDisplayString(CaseVo caseVo) {

        Date etdEtaDateTime = caseVo.getCountdownTime();
        if (Boolean.parseBoolean(appConfigurationRepository.findByOption(AppConfigurationRepository.UTC_SERVER).getValue())) {
            caseVo.setCurrentTime(new Date());
        } else {
            caseVo.setCurrentTime(appConfigurationService.offSetTimeZone(new Date()));
        }
        Long currentTimeMillis = caseVo.getCurrentTime().getTime();
        Long countDownMillis = etdEtaDateTime.getTime() - currentTimeMillis;
        Long countDownSeconds = countDownMillis / 1000;

        Long daysLong = countDownSeconds / 86400;
        Long secondsRemainder1 = countDownSeconds % 86400;
        Long hoursLong = secondsRemainder1 / 3600;
        Long secondsRemainder2 = secondsRemainder1 % 3600;
        Long minutesLong = secondsRemainder2 / 60;

        String daysString = (countDownSeconds < 0 && daysLong.longValue() == 0) ? "-" + daysLong.toString()
                : daysLong.toString();

        String countDownString = daysString + "d " + Math.abs(hoursLong) + "h " + Math.abs(minutesLong) + "m";
        caseVo.setCountDownTimeDisplay(countDownString);

        return caseVo;
    }



    @Override
    public Date getCurrentServerTime() {
        if (Boolean.parseBoolean(appConfigurationRepository.findByOption(AppConfigurationRepository.UTC_SERVER).getValue())) {
            return new Date();
        } else {
            return appConfigurationService.offSetTimeZone(new Date());
        }

    }

    /**
     * Utility method to fetch model object
     *
     * @param _tempHitsDispositionSet
     * @return
     */
    private Set<HitsDispositionVo> returnHitsDisposition(Set<HitsDisposition> _tempHitsDispositionSet) {

        Set<HitsDispositionVo> _tempReturnHitsDispSet = new HashSet<HitsDispositionVo>();
        Set<RuleCat> _tempRuleCatSet = new HashSet<RuleCat>();
        HitsDispositionVo _tempHitsDisp = new HitsDispositionVo();
        RuleCat _tempRuleCat = new RuleCat();
        Set<AttachmentVo> _tempAttachmentVoSet = new HashSet<AttachmentVo>();
        List<HitsDispositionCommentsVo> _tempHitsDispCommentsVoSet;
        HitsDispositionCommentsVo _tempDispCommentsVo = new HitsDispositionCommentsVo();

        try {
            for (HitsDisposition hitDisp : _tempHitsDispositionSet) {
                _tempHitsDisp = new HitsDispositionVo();
                _tempRuleCat = new RuleCat();
                _tempHitsDispCommentsVoSet = new ArrayList<>();
                _tempAttachmentVoSet = new HashSet<AttachmentVo>();

                CaseDispositionServiceImpl.copyIgnoringNullValues(hitDisp, _tempHitsDisp);
                _tempHitsDisp.setHit_disp_id(hitDisp.getId());
                if (hitDisp.getRuleCat() != null) {
                    CaseDispositionServiceImpl.copyIgnoringNullValues(hitDisp.getRuleCat(), _tempRuleCat);
                    // _tempRuleCat.setHitsDispositions(null);
                }
                _tempRuleCatSet.add(_tempRuleCat);
                _tempHitsDisp.setCategory(_tempRuleCat.getCategory());
                _tempHitsDisp.setRuleCatSet(_tempRuleCatSet);

                // begin to retrieve attachments
                if (hitDisp.getDispComments() != null) {
                    Set<HitsDispositionComments> _tempDispCommentsSet = hitDisp.getDispComments();
                    for (HitsDispositionComments _tempComments : _tempDispCommentsSet) {
                        _tempDispCommentsVo = new HitsDispositionCommentsVo();
                        _tempAttachmentVoSet = new HashSet<AttachmentVo>();
                        CaseDispositionServiceImpl.copyIgnoringNullValues(_tempComments, _tempDispCommentsVo);
                        _tempHitsDispCommentsVoSet.add(_tempDispCommentsVo);

                        if (_tempComments.getAttachmentSet() != null) {

                            for (Attachment a : _tempComments.getAttachmentSet()) {
                                AttachmentVo attVo = new AttachmentVo();
                                // Turn blob into byte[], as input stream is not serializable
                                attVo.setContent(a.getContent().getBytes(1, (int) a.getContent().length()));
                                attVo.setId(a.getId());
                                attVo.setContentType(a.getContentType());
                                attVo.setDescription(a.getDescription());
                                attVo.setFilename(a.getFilename());
                                // Drop blob from being held in memory after each set
                                a.getContent().free();
                                // Add to attVoList to be returned to front-end
                                a.setPassenger(null);
                                _tempAttachmentVoSet.add(attVo);
                            }

                        }
                        _tempDispCommentsVo.setAttachmentSet(_tempAttachmentVoSet);
                    }
                    _tempHitsDispCommentsVoSet.sort(comparing(HitsDispositionCommentsVo::getCreatedAt).reversed());
                    _tempHitsDisp.setDispCommentsVo(new LinkedHashSet<>(_tempHitsDispCommentsVoSet));
                } // end

                _tempReturnHitsDispSet.add(_tempHitsDisp);
            }
        } catch (Exception ex) {
            logger.error("error returning hits disposition.", ex);
        }
        // _tempReturnHitsDispSet =
        // _tempReturnHitsDispSet.stream().sorted(Comparator.comparing(HitsDispositionVo::getHit_disp_id)).collect(Collectors.toSet());
        List<HitsDispositionVo> _tempArrList = _tempReturnHitsDispSet.stream()
                .sorted(Comparator.comparing(HitsDispositionVo::getHit_disp_id)).collect(Collectors.toList());
        return new HashSet<>(_tempReturnHitsDispSet.stream()
                .sorted(Comparator.comparing(HitsDispositionVo::getHit_disp_id)).collect(Collectors.toList()));
        // return _tempReturnHitsDispSet;
    }

    /**
     * Utility method to fetch model object
     *
     * @param _tempHitsDispositionSet
     * @return
     */
    private Set<HitsDispositionVo> manageHitsDispositionCommentsAttachments(
            Set<HitsDisposition> _tempHitsDispositionSet) {

        Set<HitsDispositionVo> _tempReturnHitsDispSet = new HashSet<HitsDispositionVo>();
        Set<Attachment> _tempAttachmentSet = new HashSet<Attachment>();
        HitsDispositionVo _tempHitsDisp = new HitsDispositionVo();
        RuleCat _tempRuleCat = new RuleCat();
        try {
            for (HitsDisposition hitDisp : _tempHitsDispositionSet) {
                _tempHitsDisp = new HitsDispositionVo();
                if (hitDisp.getDispComments() != null) {
                    Set<HitsDispositionComments> _tempDispCommentsSet = hitDisp.getDispComments();
                    for (HitsDispositionComments _tempComments : _tempDispCommentsSet) {
                        if (_tempComments.getAttachmentSet() != null) {

                            for (Attachment _tempAttach : _tempComments.getAttachmentSet()) {
                                _tempAttach.setPassenger(null);
                            }
                        }
                    }
                }

            }
        } catch (Exception ex) {
            logger.error("Error in manage hits disposition comments.", ex);
        }
        return _tempReturnHitsDispSet;
    }

    /**
     * Utility method to pull passenger details for the cases view
     *
     * @param aCaseVo
     * @param flightId
     * @param paxId
     */
    private void populatePassengerDetails(CaseVo aCaseVo, Long flightId, Long paxId) {
        Passenger _tempPax = findPaxByID(paxId);
        Flight _tempFlight = findFlightByID(flightId);
        if (_tempPax != null) {
            aCaseVo.setFirstName(_tempPax.getPassengerDetails().getFirstName());
            aCaseVo.setLastName(_tempPax.getPassengerDetails().getLastName());
        }
        if (_tempFlight != null) {
            aCaseVo.setFlightNumber(_tempFlight.getFlightNumber());
        }
    }

    /**
     * Utility method to pull passenger details for the cases view
     *
     * @param aCase
     * @param flightId
     * @param paxId
     */
    private void populatePassengerDetailsInCase(Case aCase, Long flightId, Long paxId) {
        populatePassengerDetailsInCase(aCase, flightId, paxId, null, null);
    }

    private void populatePassengerDetailsInCase(Case aCase, Long flightId, Long paxId, Map<Long, Flight> flightMap,
                                                Map<Long, Passenger> passengerMap) {
        Passenger _tempPax;
        Flight _tempFlight;

        if (passengerMap == null || passengerMap.isEmpty() || !passengerMap.containsKey(paxId)) {
            logger.debug("Manual get of passenger.");
            _tempPax = findPaxByID(paxId);
        } else {
            _tempPax = passengerMap.get(paxId);
        }


        if (flightMap == null || flightMap.isEmpty() || !flightMap.containsKey(flightId)) {
            logger.debug("manual get of flights");
            _tempFlight = findFlightByID(flightId);
        } else {
            _tempFlight = flightMap.get(flightId);
        }

        if (_tempPax != null) {
            aCase.setFirstName(_tempPax.getPassengerDetails().getFirstName());
            aCase.setLastName(_tempPax.getPassengerDetails().getLastName());
        }
        if (_tempFlight != null) {
            aCase.setFlightNumber(_tempFlight.getFlightNumber());
            aCase.setFlightETADate(_tempFlight.getMutableFlightDetails().getEta());
            aCase.setFlightETDDate(_tempFlight.getMutableFlightDetails().getEtd());
        }
    }

    /**
     * Static utility method to ignore nulls while copying
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * Wrapper method over BeanUtils.copyProperties
     *
     * @param src
     * @param target
     */
    public static void copyIgnoringNullValues(Object src, Object target) {
        try {
            BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
        } catch (Exception ex) {
            logger.error("error copying object", ex);
        }
    }

    @Override
    public List<HitDispositionStatus> getHitDispositionStatuses() {
        Iterable<HitDispositionStatus> i = hitDispRepo.findAll();
        if (i != null) {
            return IteratorUtils.toList(i.iterator());
        }
        return new ArrayList<>();
    }

    @Override
    public List<OneDayLookoutVo> getOneDayLookoutByDate(Date date) {

        List<OneDayLookoutVo> oneDayLookoutVoList = null;
        Date startDate = getStartDate(date);
        Date endDate = getEndDate(date);

        List<Case> oneDayLookoutResult = caseDispositionRepository.findOneDayLookoutByDate(startDate, endDate);

        if (oneDayLookoutResult == null || oneDayLookoutResult.isEmpty()) {
            return new ArrayList<OneDayLookoutVo>();
        } else {

            oneDayLookoutVoList = getOneDaylookoutVo(oneDayLookoutResult);
            FlightPax flightPax = null;
            for (OneDayLookoutVo oneDayLookoutVo : oneDayLookoutVoList) {
                if (oneDayLookoutVo.getPaxId() != null)
                    flightPax = flightPaxRepository.findOne(oneDayLookoutVo.getPaxId());

                if (flightPax != null) {
                    if (flightPax.getPassenger().getId() != null)
                        oneDayLookoutVo.setPassengerId(flightPax.getPassenger().getId());

                }

            }
        }

        return oneDayLookoutVoList;
    }

    @Override
    public List<OneDayLookoutVo> getOneDayLookoutByDateAndAirport(Date date, String airport) {

        List<OneDayLookoutVo> oneDayLookoutVoList = null;
        Date startDate = getStartDate(date);
        Date endDate = getEndDate(date);

        List<Case> oneDayLookoutResult = caseDispositionRepository.findOneDayLookoutByDateAndAirport(startDate, endDate, airport);

        if (oneDayLookoutResult == null || oneDayLookoutResult.isEmpty()) {
            return new ArrayList<OneDayLookoutVo>();
        } else {

            oneDayLookoutVoList = getOneDaylookoutVo(oneDayLookoutResult);
            FlightPax flightPax = null;
            for (OneDayLookoutVo oneDayLookoutVo : oneDayLookoutVoList) {
                if (oneDayLookoutVo.getPaxId() != null)
                    flightPax = flightPaxRepository.findOne(oneDayLookoutVo.getPaxId());

                if (flightPax != null) {
                    if (flightPax.getPassenger().getId() != null)
                        oneDayLookoutVo.setPassengerId(flightPax.getPassenger().getId());

                }

            }
        }

        return oneDayLookoutVoList;
    }

    private Date getStartDate(Date date) {

        // set start date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        Date startDate = cal.getTime();
        return startDate;
    }

    private Date getEndDate(Date date) {
        // set end date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        Date endDate = cal.getTime();
        return endDate;

    }

    // copy One Day Lookout info to the view object
    private List<OneDayLookoutVo> getOneDaylookoutVo(List<Case> oneDayLookoutList) {

        List<OneDayLookoutVo> oneDayLookoutVoList = new ArrayList<OneDayLookoutVo>();

        OneDayLookoutVo oneDayLookoutVo = null;
        String etaEtdTime = null;
        Date etaEtdDate = null;
        Calendar calendar = null;
        for (Case oneDayLookoutCase : oneDayLookoutList) {
            etaEtdTime = null;
            calendar = null;
            oneDayLookoutVo = new OneDayLookoutVo();
            oneDayLookoutVo.setCaseId(oneDayLookoutCase.getId());
            oneDayLookoutVo.setDocument(oneDayLookoutCase.getDocument());
            oneDayLookoutVo.setFirstName(oneDayLookoutCase.getFirstName());
            oneDayLookoutVo.setLastName(oneDayLookoutCase.getLastName());
            oneDayLookoutVo.setDisposition(oneDayLookoutCase.getDisposition());
            oneDayLookoutVo.setName(oneDayLookoutCase.getLastName() + ", " + oneDayLookoutCase.getFirstName());
            oneDayLookoutVo.setEncounteredStatus(oneDayLookoutCase.getEncounteredStatus().getType());

            // set flight information
            if (oneDayLookoutCase.getFlight() != null) {
                oneDayLookoutVo.setFlightNumber(oneDayLookoutCase.getFlight().getFlightNumber());
                oneDayLookoutVo.setFullFlightNumber(oneDayLookoutCase.getFlight().getFullFlightNumber());
                oneDayLookoutVo.setPaxId(oneDayLookoutCase.getPaxId());
                oneDayLookoutVo.setFlightId(oneDayLookoutCase.getFlightId());
                String origDestFlightsStr = oneDayLookoutCase.getFlight().getOrigin() + "/"
                        + oneDayLookoutCase.getFlight().getDestination();
                oneDayLookoutVo.setOrigDestAirportsStr(origDestFlightsStr);

                if (oneDayLookoutCase.getFlight().getDirection() != null) {

                    // set eta/etd time and direction
                    if (oneDayLookoutCase.getFlight().getDirection()
                            .equalsIgnoreCase(OneDayLookoutContants.FLIGHT_DIRECTION_INCOMING)) {
                        oneDayLookoutVo.setDirection(OneDayLookoutContants.FLIGHT_DIRECTION_INCOMING_DESC);
                        etaEtdDate = oneDayLookoutCase.getFlight().getMutableFlightDetails().getEta();
                        if (etaEtdDate != null) {
                            calendar = Calendar.getInstance();
                            calendar.setTime(etaEtdDate);
                            etaEtdTime = String.format("%02d", Integer.valueOf(calendar.get(Calendar.HOUR_OF_DAY)))
                                    + String.format("%02d", Integer.valueOf(calendar.get(Calendar.MINUTE)));
                            oneDayLookoutVo.setEtaEtdTime(etaEtdTime);
                        }

                    } else if (oneDayLookoutCase.getFlight().getDirection()
                            .equalsIgnoreCase(OneDayLookoutContants.FLIGHT_DIRECTION_OUTGOING)) {
                        oneDayLookoutVo.setDirection(OneDayLookoutContants.FLIGHT_DIRECTION_OUTGOING_DESC);
                        etaEtdDate = oneDayLookoutCase.getFlight().getMutableFlightDetails().getEtd();
                        calendar = Calendar.getInstance();
                        calendar.setTime(etaEtdDate);
                        etaEtdTime = String.format("%02d", Integer.valueOf(calendar.get(Calendar.HOUR_OF_DAY)))
                                + String.format("%02d", Integer.valueOf(calendar.get(Calendar.MINUTE)));
                        oneDayLookoutVo.setEtaEtdTime(etaEtdTime);
                    }

                }
            }
            oneDayLookoutVoList.add(oneDayLookoutVo);
        }

        return oneDayLookoutVoList;
    }

    public Boolean updateDayLookoutFlag(Long caseId, Boolean flag) {

        boolean result = false;

        try {
            caseDispositionRepository.updateOneDayLookoutFlag(caseId, flag);
            result = true;
        } catch (Exception e) {
            logger.error("An Error has occurred when updating one day lookout flag for CASE ID: " + caseId
                    + " with flag: " + flag);
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Case> getCaseByPaxId(List<Long> paxIds) {
        //
        return this.caseDispositionRepository.getCaseByPaxId(paxIds);
    }

    @Override
    @Transactional
    public List<Case> getCaseHistoryByPaxId(Long paxId) {

        List<Long> pax_group = this.passengerResolverService.resolve(paxId);
        return this.getCaseByPaxId(pax_group);
    }

    // returns version with TRUE flag, apisOnlyFlag;apisVersion, e.g. TRUE;16B or
    // FALSE
    @Override
    public String getAPISOnlyFlagAndVersion() {
        String apisReturnStr = "";
        AppConfiguration appConfiguration = appConfigurationRepository
                .findByOption(AppConfigurationRepository.APIS_ONLY_FLAG);
        String apisOnlyFlag = (appConfiguration != null) ? appConfiguration.getValue() : "FALSE";
        if (apisOnlyFlag.equals("TRUE")) {
            appConfiguration = appConfigurationRepository.findByOption(AppConfigurationRepository.APIS_VERSION);
            String apisVersion = (appConfiguration != null) ? appConfiguration.getValue() : "";
            apisReturnStr = apisOnlyFlag + ";" + apisVersion;
        } else {
            apisReturnStr = apisOnlyFlag;
        }
        return apisReturnStr;
    }

    @Override
    public List<CaseDispositionStatus> getCaseDispositionStatuses() {
        Iterable<CaseDispositionStatus> i = caseDispositionStatusRepository.findAll();
        if (i != null) {
            return IteratorUtils.toList(i.iterator());
        }
        return new ArrayList<>();
    }

    public void updateEncounteredStatus(Long caseId, EncounteredStatusEnum encStatus) {

        caseDispositionRepository.updateEncounteredStatus(caseId, encStatus);

    }


    public Set<Case> getOpenCasesWithTimeLeft() {
        List<Flight> flightList = flightService.getFlightsThreeDaysForwardInbound();
        flightList.addAll(flightService.getFlightsThreeDaysForwardOutbound());
        Set<Long> flightIds =  flightList.stream().map(Flight::getId).collect(Collectors.toSet());
        Set<Case> casesBeforeTakeOff = new HashSet<>();
        if (!flightIds.isEmpty()) {
            casesBeforeTakeOff = getCasesWithTimeLeft(caseDispositionRepository.getCasesByFlightIds(flightIds));
        }
        return casesBeforeTakeOff;
    }

    protected Set<Case> getCasesWithTimeLeft(Set<Case> caseSet) {
        Set<Case> caseBeforeTakeoff = new HashSet<>();
        Date now = appConfigurationService.offSetTimeZone(new Date());
        for (Case caze : caseSet) {
            Flight caseFlight = caze.getFlight();
            if (caseFlight != null) {
                Date flightDate;
                if ("I".equalsIgnoreCase(caze.getFlight().getDirection())) {
                    flightDate = caseFlight.getMutableFlightDetails().getEta();
                } else {
                    flightDate = caseFlight.getMutableFlightDetails().getEtd();
                }
                if (now.before(flightDate) && "NEW".equalsIgnoreCase(caze.getStatus())) {
                    caseBeforeTakeoff.add(caze);
                }
            }
        }
        return caseBeforeTakeoff;
    }


}