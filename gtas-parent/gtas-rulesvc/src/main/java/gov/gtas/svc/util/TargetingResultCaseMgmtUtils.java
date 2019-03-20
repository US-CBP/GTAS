/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.svc.util;

import gov.gtas.bo.*;
import gov.gtas.model.Case;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.PassengerService;
import gov.gtas.util.Bench;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class TargetingResultCaseMgmtUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(TargetingResultCaseMgmtUtils.class);

    @Autowired
    private PassengerRepository paxRepoInstance;

    private static PassengerRepository paxRepo;

    @Autowired
    public TargetingResultCaseMgmtUtils(PassengerRepository paxRepo) {
        TargetingResultCaseMgmtUtils.paxRepo = paxRepo;
    }

    public TargetingResultCaseMgmtUtils() {
    }

    /**
     * Eliminates duplicates and adds flight id, if missing.
     *
     * @param result
     * @return
     */
    public static Set<Case> ruleResultPostProcesssing(
            RuleServiceResult result, CaseDispositionService dispositionService, PassengerService passengerService) {
        logger.debug("Entering ruleResultPostProcesssing().");
        // get the list of RuleHitDetail objects returned by the Rule Engine
        List<RuleHitDetail> resultList = result.getResultList();

        // create a Map to eliminate duplicates
        Map<RuleHitDetail, RuleHitDetail> resultMap = new HashMap<>();

        if (logger.isDebugEnabled()) {
            logger.debug("Number of hits --> " + resultList.size());
        }
        // all of these maps prevent many trips to the database in the for loop below.
        Map<Long, List<Long> > passengerFlightMap = TargetingResultUtils.createPassengerFlightMap(resultList, passengerService);
        Map<Long, Passenger> passengerMap = TargetingResultUtils.createPassengerMap(resultList, passengerService);
        Map<Long, Flight> flightMap = TargetingResultUtils.createFlightMap(resultList, passengerService);
        Map<Long, Case> caseMap = createCaseMap(resultList, dispositionService);
        Map<Long, RuleCat> ruleCatMap = createRuleCatMap(dispositionService);
        
        Set<Case> casesSet = new HashSet<>();
        for (RuleHitDetail rhd : resultList) {
            if (rhd.getFlightId() == null) {
                List<Long> flightIdList = passengerFlightMap.get(rhd.getPassengerId());
                if (flightIdList != null && !CollectionUtils.isEmpty(flightIdList)) {
                        for (Long flightId : flightIdList) {
                            Case caze = processPassengerFlight(rhd, flightId, caseMap, flightMap,
                                    passengerMap,ruleCatMap, dispositionService);
                            if (!casesSet.add(caze)) {
                                casesSet.remove(caze);
                                casesSet.add(caze);
                            }
                        }
                } else {
                    // ERROR we do not have flights for this passenger
                    logger.error("TargetingServiceUtils.ruleResultPostProcesssing() no flight information for passenger  with ID:"
                            + rhd.getPassenger().getId());
                }
            } else {
                Case caze = processPassengerFlight(rhd, rhd.getFlightId(),caseMap, flightMap, passengerMap,ruleCatMap, dispositionService);
                if (!casesSet.add(caze)) {
                    casesSet.remove(caze);
                    casesSet.add(caze);
                }
            }
            rhd.setPassenger(null);
        }

        logger.debug("Exiting ruleResultPostProcesssing().");
        return casesSet;
    }

    /**
     * Method that does bulk of the Case Mgmt. calls
     * @param rhd
     * @param flightId
     * @param dispositionService
     */
    private static Case processPassengerFlight(RuleHitDetail rhd,
                                                    Long flightId,
                                                    Map<Long, Case> caseMap,
                                                    Map<Long, Flight> flightMap,
                                                    Map<Long, Passenger> passengerMap,
                                                    Map<Long, RuleCat> ruleCatMap,
                                                    CaseDispositionService dispositionService) {

        // Feed into Case Mgmt., Flight_ID, Pax_ID, Rule_ID to build a case
        Long _tempPaxId = null;
        Passenger _tempPax = null;
        String description = rhd.getDescription();
        String watchlistItemFlag = "wl_item";
        Case newCase = null;
        try {
            _tempPaxId = rhd.getPassengerId();
           _tempPax = passengerMap.get(_tempPaxId);

            if(rhd.getUdrRuleId() == null){
                description = watchlistItemFlag + description;
            }
            if (_tempPax != null) {
                String document = null;
                if (_tempPax.getDocuments() != null) {
                    document = _tempPax.getDocuments().stream().findFirst().map(Document::getDocumentNumber).orElse(null);
                }
                newCase = dispositionService.registerCaseFromRuleService(
                        flightId,
                        rhd.getPassengerId(),
                        rhd.getPassengerName(),
                        rhd.getPassengerType().getPassengerTypeName(),
                        _tempPax.getPassengerDetails().getCitizenshipCountry(),
                        _tempPax.getPassengerDetails().getDob(),
                        document,
                        description,
                        rhd.getRuleId(),
                        caseMap,
                        flightMap,
                        passengerMap,
                        ruleCatMap);
            }
        } catch (Exception ex) {
            logger.error("Could not initiate a case for Flight:" + flightId + "  Pax:" + _tempPaxId
                    + "  Rule:" + rhd.getRuleId() + " set",ex);
        }

        return newCase;
    }

    /**
     *
     * @param ctx
     * @param res
     */
    public static void updateRuleExecutionContext(RuleExecutionContext ctx,
                                                  RuleServiceResult res) {
        logger.info("Entering updateRuleExecutionContext().");
        ctx.setRuleExecutionStatistics(res.getExecutionStatistics());
        final Map<String, TargetSummaryVo> hitSummaryMap = new HashMap<>();
        for (RuleHitDetail rhd : res.getResultList()) {
            String key = rhd.getFlightId() + "/" + rhd.getPassengerId();
            TargetSummaryVo hitSummmary = hitSummaryMap.get(key);
            if (hitSummmary == null) {
                hitSummmary = new TargetSummaryVo(rhd.getHitType(),
                        rhd.getFlightId(), rhd.getPassengerType(),
                        rhd.getPassengerId(), rhd.getPassengerName());
                hitSummaryMap.put(key, hitSummmary);
            }
            hitSummmary.addHitDetail(new TargetDetailVo(rhd.getUdrRuleId(), rhd
                    .getRuleId(), rhd.getHitType(), rhd.getTitle(), rhd
                    .getHitReasons()));
        }
        ctx.setTargetingResult(hitSummaryMap.values());
        logger.info("Exiting updateRuleExecutionContext().");
    }


    public PassengerRepository getPaxRepoInstance() {
        return paxRepoInstance;
    }

    public void setPaxRepoInstance(PassengerRepository paxRepoInstance) {
        this.paxRepoInstance = paxRepoInstance;
    }
    
    private static Map<Long, RuleCat> createRuleCatMap(CaseDispositionService dispositionService)
    {
       Map<Long, RuleCat> ruleCatMap = new HashMap<>();
       Iterable<RuleCat> ruleCatList = dispositionService.findAllRuleCat();
       for (RuleCat ruleCat : ruleCatList)
       {
         ruleCatMap.put(ruleCat.getId(), ruleCat);
       }
        
       return ruleCatMap;
    }
    
    private static Map<Long, Case> createCaseMap(List<RuleHitDetail> ruleHitDetailList, CaseDispositionService dispositionService)
    {
        List<Long> passengerIdList = new ArrayList<>();
        List<Case> caseResultList = null;
        Map<Long, Case> caseMap = new HashMap<>();

        for (RuleHitDetail rhd : ruleHitDetailList)
        {
           Long passengerId = rhd.getPassengerId();
           passengerIdList.add(passengerId);

        }

        if (!passengerIdList.isEmpty())
        {
            caseResultList = dispositionService.getCaseByPaxId(passengerIdList);

            for (Case caze :  caseResultList)
            {
               caseMap.put(caze.getPaxId(), caze);

            }
        }

        return caseMap;       
    }
}
