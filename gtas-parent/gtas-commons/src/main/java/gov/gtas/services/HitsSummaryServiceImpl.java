/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;
import gov.gtas.repository.HitsSummaryRepository;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class HitsSummaryServiceImpl implements HitsSummaryService {

    @Autowired
    HitsSummaryRepository hitsSummaryRepository;
    
    @Override
    public List<HitDetail> findByPassengerId(Long id) {
        return hitsSummaryRepository.findRuleHitsByPassengerId(id);
    }

    @Override
    public Iterable<HitsSummary> findAll() {
        return hitsSummaryRepository.findAll();
    }

    @Override
    public List<HitsSummary> findHitsByFlightId(Long flightId){return hitsSummaryRepository.findHitsByFlightId(flightId);}
    
    public List<HitsSummary> findByFlightIdAndPassengerIdAndUdrRule(
            Long fightId, Long passengerId) {
        List<String> listHitTypes = Arrays.asList("R","RPD", "RP", "RD");
        return hitsSummaryRepository.findByFlightIdAndPassengerIdWithHitTypes(fightId, passengerId, listHitTypes);
    }
    
    public List<HitsSummary> findByFlightIdAndPassengerIdAndWL(Long fightId, Long passengerId) {
        List<String> listHitTypes = Arrays.asList("P","D", "PD");
        return hitsSummaryRepository.findByFlightIdAndPassengerIdWithHitTypes(fightId, passengerId, listHitTypes);
    }
    
    public List<HitsSummary> findByFlightIdAndPassengerIdAndCombinedWithUdrRule(
            Long fightId, Long passengerId) {
        List<String> listHitTypes = Arrays.asList("RPD", "RP", "RD");
        return hitsSummaryRepository.findByFlightIdAndPassengerIdWithHitTypes(fightId, passengerId, listHitTypes);
    }
    
}
