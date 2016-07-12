/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import gov.gtas.bo.BasicRuleServiceResult;
import gov.gtas.bo.TargetDetailVo;
import gov.gtas.bo.TargetSummaryVo;
import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.Flight;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetingResultUtils {
    private static final Logger logger = LoggerFactory
            .getLogger(TargetingResultUtils.class);

    /**
     * Eliminates duplicates and adds flight id, if missing.
     * 
     * @param result
     * @return
     */
    public static RuleServiceResult ruleResultPostProcesssing(
            RuleServiceResult result) {
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
                if (flights != null && flights.size() > 0) {
                    try {
                        for (Flight flight : flights) {
                            RuleHitDetail newrhd = rhd.clone();
                            processPassengerFlight(newrhd, flight.getId(),
                                    resultMap);
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
                processPassengerFlight(rhd, rhd.getFlightId(), resultMap);
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
            Long flightId, Map<RuleHitDetail, RuleHitDetail> resultMap) {
        logger.info("Entering processPassengerFlight().");
        rhd.setFlightId(flightId);

        // set the passenger object to null
        // since its only purpose was to provide flight
        // details.
        rhd.setPassenger(null);
        RuleHitDetail resrhd = resultMap.get(rhd);
        if (resrhd != null && resrhd.getRuleId() != rhd.getRuleId()) {
            resrhd.incrementHitCount();
            if (resrhd.getUdrRuleId() != null) {
                logger.info("This is a rule hit so increment the rule hit count.");
                // this is a rule hit
                resrhd.incrementRuleHitCount();
            } else {
                logger.info("This is a watch list hit.");
                // this is a watch list hit
                if (resrhd.getHitType() != rhd.getHitType()) {
                    resrhd.setHitType(HitTypeEnum.PD);
                }
            }
        } else if (resrhd == null) {
            resultMap.put(rhd, rhd);
        }
        logger.info("Exiting processPassengerFlight().");
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
}
