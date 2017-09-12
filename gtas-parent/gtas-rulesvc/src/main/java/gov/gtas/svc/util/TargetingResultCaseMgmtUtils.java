/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.svc.util;

import gov.gtas.bo.*;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.CaseDispositionService;
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
            RuleServiceResult result, CaseDispositionService dispositionService) {
        logger.info("Entering ruleResultPostProcesssing().");
        // get the list of RuleHitDetail objects returned by the Rule Engine
        List<RuleHitDetail> resultList = result.getResultList();

        // create a Map to eliminate duplicates
        Map<RuleHitDetail, RuleHitDetail> resultMap = new HashMap<>();

        if (logger.isInfoEnabled()) {
            logger.info("Number of rule hits --> " + resultList.size());
        }
        for (RuleHitDetail rhd : resultList) {
            if (rhd.getFlightId() == null) {
                // get all the flights for the passenger
                // and replicate the RuleHitDetail object, for each flight id
                // Note that the RuleHitDetail key is (UdrId, EngineRuleId,
                // PassengerId, FlightId)
                Collection<Flight> flights = rhd.getPassenger().getFlights();
                if (flights != null && !CollectionUtils.isEmpty(flights)) {
                    try {
                        for (Flight flight : flights) {
                            RuleHitDetail newrhd = rhd.clone();
                            processPassengerFlight(newrhd, flight.getId(),
                                    resultMap, dispositionService);
                        }
                    } catch (CloneNotSupportedException cnse) {
                        cnse.printStackTrace();
                    }
                } else {
                    // ERROR we do not have flights for this passenger
                    logger.error("TargetingServiceUtils.ruleResultPostProcesssing() no flight information for passenger  with ID:"
                            + rhd.getPassenger().getId());
                }
            } else {
                processPassengerFlight(rhd, rhd.getFlightId(), resultMap, dispositionService);
            }
            rhd.setPassenger(null);
        }
        // Now create the return list from the set, thus eliminating duplicates.
        RuleServiceResult ret = new BasicRuleServiceResult(
                new LinkedList<RuleHitDetail>(resultMap.values()),
                result.getExecutionStatistics());
        logger.info("Exiting ruleResultPostProcesssing().");
        return ret;
    }

    private static void processPassengerFlight(RuleHitDetail rhd,
                                               Long flightId, Map<RuleHitDetail, RuleHitDetail> resultMap,
                                               CaseDispositionService dispositionService) {

        // Feed into Case Mgmt., Flight_ID, Pax_ID, Rule_ID to build a case
        Long _tempPaxId = null;
        Passenger _tempPax = null;
        try {
            _tempPaxId = rhd.getPassengerId();
//            _tempPax = TargetingResultCaseMgmtUtils.paxRepo.findOne(_tempPaxId);
            _tempPax = dispositionService.findPaxByID(_tempPaxId);
            //dispositionService.registerCasesFromRuleService(flightId, rhd.getPassengerId(), rhd.getRuleId());
            if (_tempPax != null) {
                String document = null;
                for (Document documentItem : _tempPax.getDocuments()) {
                    document = documentItem.getDocumentNumber();
                }
                dispositionService.registerCasesFromRuleService(flightId, rhd.getPassengerId(), rhd.getPassengerName(),
                        rhd.getPassengerType().getPassengerTypeName(), _tempPax.getCitizenshipCountry(), _tempPax.getDob(),
                        document, rhd.getDescription(), rhd.getRuleId());
            }
        } catch (Exception ex) {
            logger.error("Could not initiate a case for Flight:" + flightId + "  Pax:" + _tempPaxId + "  Rule:" + rhd.getRuleId() + " set");
            ex.printStackTrace();
        }

    }

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


}
