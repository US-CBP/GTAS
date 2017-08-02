/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Case;

import javax.annotation.Resource;

import gov.gtas.model.HitsDisposition;
import gov.gtas.model.HitsDispositionComments;
import gov.gtas.model.lookup.DispositionStatusCode;
import gov.gtas.repository.CaseDispositionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class CaseDispositionServiceImpl implements CaseDispositionService  {

    @Resource
    private CaseDispositionRepository caseDispositionRepository;

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

    public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, List<Long> hit_ids)
    {
        List<Case> _tempCaseList = new ArrayList<>();

        return _tempCaseList;
    }
}