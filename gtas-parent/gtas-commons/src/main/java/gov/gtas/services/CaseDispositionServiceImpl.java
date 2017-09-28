/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.CaseDispositionStatusEnum;
import gov.gtas.model.*;
import gov.gtas.model.lookup.DispositionStatusCode;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.repository.*;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.HitsDispositionVo;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service
public class CaseDispositionServiceImpl implements CaseDispositionService  {

    private static final Logger logger = LoggerFactory
            .getLogger(CaseDispositionServiceImpl.class);

    private static Set casesToCommit = new HashSet<Case>();

    private static final String INITIAL_COMMENT="Initial Comment";

    private static final String UPDATED_BY_INTERNAL="Internal";


    @Resource
    private CaseDispositionRepository caseDispositionRepository;

    @Resource
    private FlightRepository flightRepository;

    @Resource
    private PassengerRepository passengerRepository;

    @Resource
    private HitDetailRepository hitDetailRepository;

    @Resource
    private RuleCatRepository ruleCatRepository;

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
        _tempCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id);
        if(_tempCase!=null &&
                (_tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.NEW))
                ||_tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.OPEN))))
                {aCase = _tempCase; }

        //redundant at this time
        //contextCases(aCase);

        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            pullRuleCategory(hitDisp, _tempHitId);
            highPriorityRuleCatId = getHighPriorityRuleCatId(_tempHitId);
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
        aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
        if(aCase.getHitsDispositions()!=null) aCase.getHitsDispositions().addAll(hitsDispSet);
        else aCase.setHitsDispositions(hitsDispSet);
        caseDispositionRepository.saveAndFlush(aCase);
        return aCase;
    }


    /**
     * Utility method to manage cases to persist
     * @param aCase
     */
    private void contextCases(Case aCase) {
        try{
            if(casesToCommit!=null){
                final Case _tempCaseToCompare = aCase;
                Case existingCase = (Case)casesToCommit.stream()
                        .filter(x -> _tempCaseToCompare.equals(x))
                        .findAny()
                        .orElse(null);
                if(existingCase!=null)aCase=existingCase;
                else casesToCommit.add(aCase);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Utility method to pull Rule Cat
     * @param hitDisp
     * @param id
     */
    private void pullRuleCategory(HitsDisposition hitDisp, Long id){
            try{
                hitDisp.setRuleCat(ruleCatRepository.findOne(id));
            }catch (Exception ex){  ex.printStackTrace();}
    }

    /**
     *
     * @param ruleId
     * @return
     */
    private Long getHighPriorityRuleCatId(Long ruleId) {
        try{
             return ruleCatService.fetchRuleCatPriorityIdFromRuleId(ruleId);
        }catch (Exception ex){ex.printStackTrace();}
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
    public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id, String caseComments, String status, String validHit) {
        Case aCase = new Case();
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();

        try {
            aCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id);

            if(aCase!=null && status != null) { // set case status
                if (status.startsWith("Case")) aCase.setStatus(status.substring(4));
            }
            hitsDispCommentsSet = null;
            hitsDispSet = aCase.getHitsDispositions();
            for(HitsDisposition hit : hitsDispSet){

                if((hit.getCaseId() == aCase.getId()) && (hit_id != null) && (hit.getHitId() == hit_id)){

                    if(caseComments != null){ // set comments
                        hitsDispositionComments = new HitsDispositionComments();
                        hitsDispositionComments.setHitId(hit_id);
                        hitsDispositionComments.setComments(caseComments);
                        hitsDispCommentsSet = hit.getDispComments();
                        hitsDispCommentsSet.add(hitsDispositionComments);
                        hit.setDispComments(hitsDispCommentsSet);
                    }

                    if(status != null && !status.startsWith("Case")){ // set status
                        hit.setStatus(status);
                    }

                    if(!(validHit==null)){
                        hit.setValid(validHit);
                    }

                } // end of hit updates

            }

            aCase.setHitsDispositions(hitsDispSet);

            if((status != null) || (caseComments != null) || (validHit != null))
                caseDispositionRepository.save(aCase);

        }catch (Exception ex){
            ex.printStackTrace();
        }
            return aCase;
    }

    @Override
    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, Long hit_id)
    {
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
    public Case findHitsDispositionByCriteria(CaseRequestDto dto){

        return caseDispositionRepository.getCaseByFlightIdAndPaxId(dto.getFlightId(), dto.getPaxId());
    }

    /**
     *
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
            populatePassengerDetails(vo, f.getFlightId(),f.getPaxId());
            //BeanUtils.copyProperties(f, vo);
            CaseDispositionServiceImpl.copyIgnoringNullValues(f, vo);
            vos.add(vo);
        }

        return new CasePageDto(vos, tuple2.getLeft());
    }

    /**
     * Utility method to fetch model object
     * @param _tempHitsDispositionSet
     * @return
     */
    private Set<HitsDispositionVo> returnHitsDisposition (Set<HitsDisposition> _tempHitsDispositionSet){

        Set<HitsDispositionVo> _tempReturnHitsDispSet = new HashSet<HitsDispositionVo>();
        Set<RuleCat> _tempRuleCatSet = new HashSet<RuleCat>();
        HitsDispositionVo _tempHitsDisp = new HitsDispositionVo();
        RuleCat _tempRuleCat = new RuleCat();
        for(HitsDisposition hitDisp : _tempHitsDispositionSet){
            _tempHitsDisp = new HitsDispositionVo();
            //BeanUtils.copyProperties(hitDisp, _tempHitsDisp);
            CaseDispositionServiceImpl.copyIgnoringNullValues(hitDisp, _tempHitsDisp);
            //BeanUtils.copyProperties(hitDisp.getRuleCat(), _tempRuleCat);
            CaseDispositionServiceImpl.copyIgnoringNullValues(hitDisp.getRuleCat(), _tempRuleCat);
            _tempRuleCatSet.add(_tempRuleCat);
            _tempHitsDisp.setRuleCatSet(_tempRuleCatSet);
            _tempReturnHitsDispSet.add(_tempHitsDisp);
        }
        return _tempReturnHitsDispSet;
    }


    /**
     * Utility method to pull passenger details for the cases view
     * @param aCaseVo
     * @param flightId
     * @param paxId
     */
    private void populatePassengerDetails (CaseVo aCaseVo, Long flightId, Long paxId){
        Passenger _tempPax = findPaxByID(paxId);
        Flight _tempFlight = findFlightByID(flightId);
        aCaseVo.setFirstName(_tempPax.getFirstName());
        aCaseVo.setLastName(_tempPax.getLastName());
        aCaseVo.setFlightNumber(_tempFlight.getFlightNumber());
    }


    /**
     * Static utility method to ignore nulls while copying
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * Wrapper method over BeanUtils.copyProperties
     * @param src
     * @param target
     */
    public static void copyIgnoringNullValues(Object src, Object target) {
        try {
            BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}