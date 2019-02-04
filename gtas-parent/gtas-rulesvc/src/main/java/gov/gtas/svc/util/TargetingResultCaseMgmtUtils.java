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

import org.apache.commons.collections.CollectionUtils;
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

    /**
     * Eliminates duplicates and adds flight id, if missing.
     *
     * @param result
     * @return
     */
    public static RuleServiceResult ruleResultPostProcesssing(
            RuleServiceResult result, CaseDispositionService dispositionService, PassengerService passengerService) {
        logger.info("Entering ruleResultPostProcesssing().");
        // get the list of RuleHitDetail objects returned by the Rule Engine
        List<RuleHitDetail> resultList = result.getResultList();

        // create a Map to eliminate duplicates
        Map<RuleHitDetail, RuleHitDetail> resultMap = new HashMap<>();

        if (logger.isInfoEnabled()) {
            logger.info("Number of rule hits --> " + resultList.size());
        }
        Bench.start("here2", "Before for RuleHitDetail loop.");
        
        // all of these maps prevent many trips to the database in the for loop below.
        Map<Long, List<Flight> > passengerFlightMap = TargetingResultUtils.createPassengerFlightMap(resultList, passengerService);
        Map<Long, Passenger> passengerMap = TargetingResultUtils.createPassengerMap(resultList, passengerService);
        Map<Long, Flight> flightMap = TargetingResultUtils.createFlightMap(resultList, passengerService);
        Map<Long, Case> caseMap = createCaseMap(resultList, dispositionService);
        Map<Long, RuleCat> ruleCatMap = createRuleCatMap(dispositionService);
        
        
        for (RuleHitDetail rhd : resultList) {
            if (rhd.getFlightId() == null) {
                // get all the flights for the passenger
                // and replicate the RuleHitDetail object, for each flight id
                // Note that the RuleHitDetail key is (UdrId, EngineRuleId,
                // PassengerId, FlightId)
                List<Flight> flights = passengerFlightMap.get(rhd.getPassengerId());
                
                if (flights != null && !CollectionUtils.isEmpty(flights)) {
                    try {
                        Bench.start("here3", "Before for Flight loop.");
                        for (Flight flight : flights) {
                            RuleHitDetail newrhd = rhd.clone();
                            processPassengerFlight(newrhd, flight.getId(), caseMap, flightMap,
                                     passengerMap,ruleCatMap, dispositionService);
                        }
                        Bench.end("here3", "After for Flight loop in ruleResultPostProcesssing.");
                    } catch (CloneNotSupportedException cnse) {
                        logger.error("error, clone not supported", cnse);
                    }
                } else {
                    // ERROR we do not have flights for this passenger
                    logger.error("TargetingServiceUtils.ruleResultPostProcesssing() no flight information for passenger  with ID:"
                            + rhd.getPassenger().getId());
                }
            } else {
                Bench.start("here4", "start processPassengerFlight call in ruleResultPostProcesssing.");
                processPassengerFlight(rhd, rhd.getFlightId(),caseMap, flightMap, passengerMap,ruleCatMap, dispositionService);
                Bench.end("here4", " End processPassengerFlight call in ruleResultPostProcesssing.");
            }
            rhd.setPassenger(null);
        }
        Bench.end("here2", "After for RuleHitDetail loop in ruleResultPostProcesssing.");
        // Now create the return list from the set, thus eliminating duplicates.
        RuleServiceResult ret = new BasicRuleServiceResult(
                new LinkedList<>(resultMap.values()),
                result.getExecutionStatistics());
        logger.info("Exiting ruleResultPostProcesssing().");
        return ret;
    }

    /**
     * Method that does bulk of the Case Mgmt. calls
     * @param rhd
     * @param flightId
     * @param dispositionService
     */
    private static void processPassengerFlight(RuleHitDetail rhd,
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
        try {
            _tempPaxId = rhd.getPassengerId();
           _tempPax = passengerMap.get(_tempPaxId);

            if(rhd.getUdrRuleId() == null){
                description = watchlistItemFlag + description;
            }
            if (_tempPax != null) {
                String document = null;
                for (Document documentItem : _tempPax.getDocuments()) {
                    document = documentItem.getDocumentNumber();
                }
                dispositionService.registerCasesFromRuleService(
                        flightId,
                        rhd.getPassengerId(),
                        rhd.getPassengerName(),
                        rhd.getPassengerType().getPassengerTypeName(),
                        _tempPax.getCitizenshipCountry(),
                        _tempPax.getDob(),
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
