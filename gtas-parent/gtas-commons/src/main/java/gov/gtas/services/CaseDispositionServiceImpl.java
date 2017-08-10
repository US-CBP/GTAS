/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Case;
import gov.gtas.model.HitsDisposition;
import gov.gtas.model.HitsDispositionComments;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.DispositionStatusCode;
import gov.gtas.repository.CaseDispositionRepository;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service
public class CaseDispositionServiceImpl implements CaseDispositionService  {

    @Resource
    private CaseDispositionRepository caseDispositionRepository;

    @Resource
    private FlightRepository flightRepository;

    @Resource
    private PassengerRepository passengerRepository;

    @Resource
    private HitDetailRepository hitDetailRepository;

    public CaseDispositionServiceImpl() {
    }

    @Override
    public Case create(Long flight_id, Long pax_id, List<Long> hit_ids) {
        Case aCase = new Case();
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();

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
            hitsDispositionComments.setComments("Initial Comment");
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
            hitsDispositionComments.setComments("Initial Comment");
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }
        aCase.setHitsDispositions(hitsDispSet);


        caseDispositionRepository.save(aCase);
        return aCase;
    }

    @Override
    public Case create(Long flight_id, Long pax_id, String paxName, String paxType, String citizenshipCountry, Date dob, String hitDesc, List<Long> hit_ids) {
        Case aCase = new Case();
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();

        aCase.setFlightId(flight_id);
        aCase.setPaxId(pax_id);
        aCase.setPaxName(paxName);
        aCase.setPaxType(paxType);
        aCase.setCitizenshipCountry(citizenshipCountry);
        aCase.setDob(dob);
        aCase.setStatus(DispositionStatusCode.NEW.toString());
        for (Long _tempHitId : hit_ids) {
            hitDisp = new HitsDisposition();
            hitsDispCommentsSet = new HashSet<>();
            hitDisp.setHitId(_tempHitId);
            hitDisp.setDescription(hitDesc);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitsDispositionComments = new HitsDispositionComments();
            hitsDispositionComments.setHitId(_tempHitId);
            hitsDispositionComments.setComments("Initial Comment");
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }
        aCase.setHitsDispositions(hitsDispSet);


        caseDispositionRepository.save(aCase);
        return aCase;
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
//        for (Long _tempHitId : hit_ids) {
//            hitDisp = new HitsDisposition();
//            hitsDispCommentsSet = new HashSet<>();
//            hitDisp.setHit_id(_tempHitId);
//            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
//            hitsDispositionComments = new HitsDispositionComments();
//            hitsDispositionComments.setHit_id(_tempHitId);
//            hitsDispositionComments.setComments("Initial Comment");
//            hitsDispCommentsSet.add(hitsDispositionComments);
//            hitDisp.setDispComments(hitsDispCommentsSet);
//            hitsDispSet.add(hitDisp);
//        }
        aCase.setHitsDispositions(hitsDispSet);


        caseDispositionRepository.save(aCase);
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
    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType, String citizenshipCountry, Date dob, String hitDesc, Long hit_id) {
        List<Case> _tempCaseList = new ArrayList<>();
        List<Long> _tempHitIds = new ArrayList<>();

        _tempHitIds.add(hit_id);
        _tempCaseList.add(create(flight_id, pax_id, paxName, paxType, citizenshipCountry, dob, hitDesc, _tempHitIds));

        return _tempCaseList;
    }

    @Override
    public Case findHitsDispositionByCriteria(CaseRequestDto dto){

        return caseDispositionRepository.getCaseByFlightIdAndPaxId(dto.getFlightId(), dto.getPaxId());
    }

    @Override
    public CasePageDto findAll(CaseRequestDto dto) {

        List<CaseVo> vos = new ArrayList<>();
        Pair<Long, List<Case>> tuple = caseDispositionRepository.findByCriteria(dto);
        for (Case f : tuple.getRight()) {
            Long fId = f.getId();
            int rCount = 0;
            int wCount = 0;
//            List<HitsSummary> hList = hitsSummaryRepository.findHitsByFlightId(fId);
//            for (HitsSummary hs : hList) {
//                rCount += hs.getRuleHitCount();
//                wCount += hs.getWatchListHitCount();
//            }
//            f.setListHitCount(wCount);
//            f.setRuleHitCount(rCount);
        }
        caseDispositionRepository.flush();

        Pair<Long, List<Case>> tuple2 = caseDispositionRepository.findByCriteria(dto);
        for (Case f : tuple2.getRight()) {
            CaseVo vo = new CaseVo();
            vo.setHitsDispositions(f.getHitsDispositions());
            BeanUtils.copyProperties(f, vo);

//            vo.setListHitCount(f.getListHitCount());
//            vo.setRuleHitCount(f.getRuleHitCount());
            vos.add(vo);
        }

        return new CasePageDto(vos, tuple.getLeft());
    }
}