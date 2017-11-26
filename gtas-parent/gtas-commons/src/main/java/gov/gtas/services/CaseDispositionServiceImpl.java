/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.CaseDispositionStatusEnum;
import gov.gtas.model.*;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.model.lookup.DispositionStatusCode;
import gov.gtas.model.lookup.HitDispositionStatus;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.repository.*;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.HitsDispositionVo;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.print.Doc;
import java.sql.Blob;
import java.util.*;


@Service
public class CaseDispositionServiceImpl implements CaseDispositionService {

    private static final Logger logger = LoggerFactory
            .getLogger(CaseDispositionServiceImpl.class);

    private static Set casesToCommit = new HashSet<Case>();
    private static final String INITIAL_COMMENT = "Initial Comment";
    private static final String UPDATED_BY_INTERNAL = "Internal";
    private static final String CASE_CREATION_MANUAL_DESC = "Agent Created Case";

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
    @Autowired
    public RuleCatService ruleCatService;

    public CaseDispositionServiceImpl() {
    }

    @Override
    public Case create(Long flight_id, Long pax_id, List<Long> hit_ids) {
        Case aCase = new Case();
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
        Long highPriorityRuleCatId = 1L;
        aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
        aCase.setFlightId(flight_id);
        aCase.setPaxId(pax_id);
        aCase.setStatus(DispositionStatusCode.NEW.toString());
        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            hitsDispCommentsSet = new HashSet<>();
            hitDisp.setHitId(_tempHitId);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitsDispositionComments = new HitsDispositionComments();
            hitsDispositionComments.setHitId(_tempHitId);
            hitsDispositionComments.setComments(INITIAL_COMMENT);
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }
        aCase.setHitsDispositions(hitsDispSet);

        caseDispositionRepository.save(aCase);
        return aCase;
    }

    @Override
    public Case create(Long flight_id, Long pax_id, String paxName, String paxType, String hitDesc, List<Long> hit_ids) {
        Case aCase = new Case();
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
        Long highPriorityRuleCatId = 1L;
        aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
        aCase.setFlightId(flight_id);
        aCase.setPaxId(pax_id);
        aCase.setPaxName(paxName);
        aCase.setPaxType(paxType);
        aCase.setStatus(DispositionStatusCode.NEW.toString());
        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            hitsDispCommentsSet = new HashSet<>();
            hitDisp.setHitId(_tempHitId);
            hitDisp.setDescription(hitDesc);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitsDispositionComments = new HitsDispositionComments();
            hitsDispositionComments.setHitId(_tempHitId);
            hitsDispositionComments.setComments(INITIAL_COMMENT);
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }
        aCase.setHitsDispositions(hitsDispSet);

        caseDispositionRepository.save(aCase);
        return aCase;
    }

    @Override
    public Case create(Long flight_id, Long pax_id, String paxName, String paxType, String citizenshipCountry,
                       Date dob, String document, String hitDesc, List<Long> hit_ids) {

        Case aCase = new Case();
        Case _tempCase = null;
        Long highPriorityRuleCatId = 1L;
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
        aCase.setUpdatedAt(new Date());
        aCase.setUpdatedBy(UPDATED_BY_INTERNAL); //@ToDo change this to pass-by-value in the next release
        aCase.setFlightId(flight_id);
        aCase.setPaxId(pax_id);
        aCase.setPaxName(paxName);
        aCase.setPaxType(paxType);
        aCase.setCitizenshipCountry(citizenshipCountry);
        aCase.setDocument(document);
        aCase.setDob(dob);
        aCase.setStatus(DispositionStatusCode.NEW.toString());
        aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
        _tempCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id);
        if (_tempCase != null &&
                (_tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.NEW))
                        || _tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.OPEN)))) {
            aCase = _tempCase;
        }

        //redundant at this time
        //contextCases(aCase);

        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            pullRuleCategory(hitDisp, getRuleCatId(_tempHitId));
            highPriorityRuleCatId = getHighPriorityRuleCatId(_tempHitId);
            hitsDispCommentsSet = new HashSet<>();
            hitDisp.setHitId(_tempHitId);
            hitDisp.setDescription(hitDesc);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitDisp.setUpdatedAt(new Date());
            hitDisp.setUpdatedBy(UPDATED_BY_INTERNAL);
            hitsDispositionComments = new HitsDispositionComments();
            hitsDispositionComments.setHitId(_tempHitId);
            hitsDispositionComments.setComments(INITIAL_COMMENT);
            hitsDispositionComments.setUpdatedBy(UPDATED_BY_INTERNAL);
            hitsDispositionComments.setUpdatedAt(new Date());
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }
        if(aCase.getHighPriorityRuleCatId() != null && aCase.getHighPriorityRuleCatId().equals(1L))
            aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
        else if(aCase.getHighPriorityRuleCatId() != null && (aCase.getHighPriorityRuleCatId() > highPriorityRuleCatId) && highPriorityRuleCatId != 1)
            aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
        if (aCase.getHitsDispositions() != null) aCase.getHitsDispositions().addAll(hitsDispSet);
        else aCase.setHitsDispositions(hitsDispSet);
        caseDispositionRepository.saveAndFlush(aCase);
        return aCase;
    }

    @Override
    public Case createManualCase(Long flight_id, Long pax_id,  String comments, String username) {

        Case aCase = new Case();
        Case _tempCase = null;
        Long highPriorityRuleCatId = 1L;
        ArrayList<Long> hit_ids = new ArrayList<>();
        Passenger pax = passengerRepository.getPassengerById(pax_id);
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
        aCase.setUpdatedAt(new Date());
        aCase.setUpdatedBy(username);
        aCase.setFlightId(flight_id);
        aCase.setPaxId(pax_id);
        aCase.setPaxName(pax.getFirstName());
        aCase.setPaxType(pax.getPassengerType());
        aCase.setCitizenshipCountry(pax.getCitizenshipCountry());
        aCase.setDocument(((Document)pax.getDocuments().parallelStream().findFirst().orElse(new Document("xxxxxxxxx"))).getDocumentNumber());
        aCase.setDob(pax.getDob());
        aCase.setStatus(DispositionStatusCode.NEW.toString());

        hit_ids.add(9999L); // Manual Distinction
        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            //pullRuleCategory(hitDisp, 9999L);
            //hitDisp.getRuleCat().setHitsDispositions(null);
            highPriorityRuleCatId = 1L;
            hitsDispCommentsSet = new HashSet<>();
            hitDisp.setHitId(_tempHitId);
            hitDisp.setDescription(CASE_CREATION_MANUAL_DESC);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitDisp.setUpdatedAt(new Date());
            hitDisp.setUpdatedBy(UPDATED_BY_INTERNAL);
            hitsDispositionComments = new HitsDispositionComments();
            hitsDispositionComments.setHitId(_tempHitId);
            hitsDispositionComments.setComments(comments);
            hitsDispositionComments.setUpdatedBy(UPDATED_BY_INTERNAL);
            hitsDispositionComments.setUpdatedAt(new Date());
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }
        aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
        if (aCase.getHitsDispositions() != null) aCase.getHitsDispositions().addAll(hitsDispSet);
        else aCase.setHitsDispositions(hitsDispSet);
        caseDispositionRepository.saveAndFlush(aCase);
        return aCase;
    }

    @Override
    public Case createManualCaseAttachment(Long flight_id, Long pax_id, String paxName, String paxType, String citizenshipCountry,
                                 Date dob, String document, String hitDesc, List<Long> hit_ids, String username, MultipartFile fileToAttach) {

        Case aCase = new Case();
        Case _tempCase = null;
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
        aCase.setPaxType(paxType);
        aCase.setCitizenshipCountry(citizenshipCountry);
        aCase.setDocument(document);
        aCase.setDob(dob);
        aCase.setStatus(DispositionStatusCode.NEW.toString());
        _tempCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id);
        if (_tempCase != null &&
                (_tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.NEW))
                        || _tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.OPEN)))) {
            aCase = _tempCase;
        }

        //redundant at this time
        //contextCases(aCase);

        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            pullRuleCategory(hitDisp, getRuleCatId(_tempHitId));
            highPriorityRuleCatId = 1L;
            hitsDispCommentsSet = new HashSet<>();
            hitDisp.setHitId(_tempHitId);
            hitDisp.setDescription(hitDesc);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitDisp.setUpdatedAt(new Date());
            hitDisp.setUpdatedBy(UPDATED_BY_INTERNAL);
            hitsDispositionComments = new HitsDispositionComments();
            hitsDispositionComments.setHitId(_tempHitId);
            hitsDispositionComments.setComments(INITIAL_COMMENT);
            hitsDispositionComments.setUpdatedBy(UPDATED_BY_INTERNAL);
            hitsDispositionComments.setUpdatedAt(new Date());
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }
        aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
        if (aCase.getHitsDispositions() != null) aCase.getHitsDispositions().addAll(hitsDispSet);
        else aCase.setHitsDispositions(hitsDispSet);
        caseDispositionRepository.saveAndFlush(aCase);
        return aCase;
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
                Case existingCase = (Case) casesToCommit.stream()
                        .filter(x -> _tempCaseToCompare.equals(x))
                        .findAny()
                        .orElse(null);
                if (existingCase != null) aCase = existingCase;
                else casesToCommit.add(aCase);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Utility method to pull Rule Cat
     *
     * @param hitDisp
     * @param id
     */
    private void pullRuleCategory(HitsDisposition hitDisp, Long id) {
        try {
            if (id == null || (ruleCatRepository.findOne(id) == null))
                hitDisp.setRuleCat(ruleCatRepository.findOne(1L));
            else
                hitDisp.setRuleCat(ruleCatRepository.findOne(id));
        } catch (Exception ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
        }
        return 1L;
    }

    private Long getRuleCatId(Long ruleId) {
        try {
            return ruleCatService.fetchRuleCatIdFromRuleId(ruleId);
        } catch (Exception ex) {
            ex.printStackTrace();
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
        aCase.setHitsDispositions(hitsDispSet);

        caseDispositionRepository.save(aCase);
        return aCase;
    }

    @Override
    public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id, String caseComments, String status, String validHit, MultipartFile fileToAttach) {
        Case aCase = new Case();
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();

        try {
            aCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id);

            if (aCase != null && status != null) { // set case status
                if (status.startsWith("Case")) aCase.setStatus(status.substring(4));
            }
            hitsDispCommentsSet = null;
            hitsDispSet = aCase.getHitsDispositions();
            for (HitsDisposition hit : hitsDispSet) {

                if ((hit.getCaseId() == aCase.getId()) && (hit_id != null) && (hit.getHitId() == hit_id)) {

                    if (caseComments != null) { // set comments
                        hitsDispositionComments = new HitsDispositionComments();
                        hitsDispositionComments.setHitId(hit_id);
                        hitsDispositionComments.setComments(caseComments);
                        hitsDispCommentsSet = hit.getDispComments();
                        //check whether attachment exists, if yes, populate
                        if(fileToAttach!=null && !fileToAttach.isEmpty())populateAttachmentsToCase(fileToAttach, hitsDispositionComments, pax_id);
                        hitsDispCommentsSet.add(hitsDispositionComments);
                        hit.setDispComments(hitsDispCommentsSet);
                    }

                    if (status != null && !status.startsWith("Case")) { // set status
                        hit.setStatus(status);
                    }

                    if (!(validHit == null)) {
                        hit.setValid(validHit);
                    }

                } // end of hit updates

            }

            aCase.setHitsDispositions(hitsDispSet);

            if ((status != null) || (caseComments != null) || (validHit != null))
                caseDispositionRepository.save(aCase);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return aCase;
    }

    /**
     * Utility method to persist attachment to each case comment
     * @param file
     * @param _tempHitsDispComments
     * @throws Exception
     */
    private void populateAttachmentsToCase(MultipartFile file, HitsDispositionComments _tempHitsDispComments, Long pax_id) throws Exception {

        Attachment attachment = new Attachment();
            //Build attachment to be added to pax
        Set<Attachment> _tempAttachSet = new HashSet<Attachment>();
            if(_tempHitsDispComments.getAttachmentSet()!=null)_tempAttachSet = _tempHitsDispComments.getAttachmentSet();
            attachment.setContentType(file.getContentType());
            attachment.setFilename(file.getOriginalFilename());
            attachment.setName(file.getName());
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            attachment.setContent(blob);
            //Grab pax to add attachment to it
            Passenger pax = passengerRepository.findOne(pax_id);
            attachment.setPassenger(pax);
            attachmentRepository.save(attachment);
        _tempAttachSet.add(attachment);
        _tempHitsDispComments.setAttachmentSet(_tempAttachSet);

    }

    @Override
    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, Long hit_id) {
        List<Case> _tempCaseList = new ArrayList<>();
        List<Long> _tempHitIds = new ArrayList<>();

        _tempHitIds.add(hit_id);
        _tempCaseList.add(create(flight_id, pax_id, _tempHitIds));

        return _tempCaseList;
    }

    @Override
    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType, String hitDesc, Long hit_id) {
        List<Case> _tempCaseList = new ArrayList<>();
        List<Long> _tempHitIds = new ArrayList<>();

        _tempHitIds.add(hit_id);
        _tempCaseList.add(create(flight_id, pax_id, paxName, paxType, hitDesc, _tempHitIds));

        return _tempCaseList;
    }

    @Override
    public Passenger findPaxByID(Long id) {
        return passengerRepository.findOne(id);
    }

    @Override
    public Flight findFlightByID(Long id) {
        return flightRepository.findOne(id);
    }

    @Override
    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType, String citizenshipCountry, Date dob,
                                                   String document, String hitDesc, Long hit_id) {
        List<Case> _tempCaseList = new ArrayList<>();
        List<Long> _tempHitIds = new ArrayList<>();

        _tempHitIds.add(hit_id);
        _tempCaseList.add(create(flight_id, pax_id, paxName, paxType, citizenshipCountry, dob, document, hitDesc, _tempHitIds));

        return _tempCaseList;
    }

    @Override
    public Case findHitsDispositionByCriteria(CaseRequestDto dto) {

        return caseDispositionRepository.getCaseByFlightIdAndPaxId(dto.getFlightId(), dto.getPaxId());
    }

    /**
     * @param dto
     * @return
     */
    @Override
    public CasePageDto findAll(CaseRequestDto dto) {

        List<CaseVo> vos = new ArrayList<>();
//        Pair<Long, List<Case>> tuple = caseDispositionRepository.findByCriteria(dto);
//        for (Case f : tuple.getRight()) {
//            Long fId = f.getId();
//            int rCount = 0;
//            int wCount = 0;
//
//        }
//        caseDispositionRepository.flush();

        Pair<Long, List<Case>> tuple2 = caseDispositionRepository.findByCriteria(dto);
        for (Case f : tuple2.getRight()) {
            CaseVo vo = new CaseVo();
            f.getHitsDispositions().stream().forEach(x -> x.getRuleCat());
            vo.setHitsDispositions(f.getHitsDispositions());
            vo.setHitsDispositionVos(returnHitsDisposition(f.getHitsDispositions()));
            populatePassengerDetails(vo, f.getFlightId(), f.getPaxId());
            //BeanUtils.copyProperties(f, vo);
            manageHitsDispositionCommentsAttachments(f.getHitsDispositions());
            CaseDispositionServiceImpl.copyIgnoringNullValues(f, vo);
            manageHitsDispositionCommentsAttachments(vo.getHitsDispositions());
            vos.add(vo);
        }

        return new CasePageDto(vos, tuple2.getLeft());
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
        try {
            for (HitsDisposition hitDisp : _tempHitsDispositionSet) {
                _tempHitsDisp = new HitsDispositionVo();
                _tempRuleCat = new RuleCat();
                CaseDispositionServiceImpl.copyIgnoringNullValues(hitDisp, _tempHitsDisp);
                if (hitDisp.getRuleCat() != null) {
                    CaseDispositionServiceImpl.copyIgnoringNullValues(hitDisp.getRuleCat(), _tempRuleCat);
                    _tempRuleCat.setHitsDispositions(null);
                }
                _tempRuleCatSet.add(_tempRuleCat);
                _tempHitsDisp.setCategory(_tempRuleCat.getCategory());
                _tempHitsDisp.setRuleCatSet(_tempRuleCatSet);
                _tempReturnHitsDispSet.add(_tempHitsDisp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _tempReturnHitsDispSet;
    }


    /**
     * Utility method to fetch model object
     *
     * @param _tempHitsDispositionSet
     * @return
     */
    private Set<HitsDispositionVo> manageHitsDispositionCommentsAttachments
                                        (Set<HitsDisposition> _tempHitsDispositionSet) {

        Set<HitsDispositionVo> _tempReturnHitsDispSet = new HashSet<HitsDispositionVo>();
        Set<Attachment> _tempAttachmentSet = new HashSet<Attachment>();
        HitsDispositionVo _tempHitsDisp = new HitsDispositionVo();
        RuleCat _tempRuleCat = new RuleCat();
        try {
            for (HitsDisposition hitDisp : _tempHitsDispositionSet) {
                _tempHitsDisp = new HitsDispositionVo();
                if(hitDisp.getDispComments()!=null) {
                    Set<HitsDispositionComments> _tempDispCommentsSet = hitDisp.getDispComments();
                    for (HitsDispositionComments _tempComments : _tempDispCommentsSet) {
                        if (_tempComments.getAttachmentSet()!=null) {
                            for (Attachment _tempAttach : _tempComments.getAttachmentSet()) {
                                    _tempAttach.setPassenger(null);
                            }
                        }
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
        aCaseVo.setFirstName(_tempPax.getFirstName());
        aCaseVo.setLastName(_tempPax.getLastName());
        aCaseVo.setFlightNumber(_tempFlight.getFlightNumber());
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
            if (srcValue == null) emptyNames.add(pd.getName());
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
            ex.printStackTrace();
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
}