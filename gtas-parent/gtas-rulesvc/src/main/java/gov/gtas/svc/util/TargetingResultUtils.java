/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import gov.gtas.bo.BasicRuleServiceResult;
import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.bo.TargetDetailVo;
import gov.gtas.bo.TargetSummaryVo;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightPax;
import gov.gtas.model.Passenger;
import gov.gtas.repository.udr.RuleMetaRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.PassengerService;
import gov.gtas.svc.TargetingResultServices;
import gov.gtas.util.Bench;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetingResultUtils {
    private static final Logger logger = LoggerFactory
            .getLogger(TargetingResultUtils.class);

    public static Map<Long, Flight> createFlightMap(List<RuleHitDetail> ruleHitDetailList, PassengerService passengerService) {
        List<Long> flightIdList = new ArrayList<>();
        List<Flight> flightResultList = null;
        Map<Long, Flight> flightMap = new HashMap<>();

        for (RuleHitDetail rhd : ruleHitDetailList) {
            Long flightId = rhd.getFlightId();
            flightIdList.add(flightId);
        }

        if (!flightIdList.isEmpty()) {
            flightResultList = passengerService.getFlightsByIdList(flightIdList);

            for (Flight flight : flightResultList) {
                flightMap.put(flight.getId(), flight);
            }
        }

        return flightMap;
    }

    public static Map<Long, Passenger> createPassengerMap(List<RuleHitDetail> ruleHitDetailList, PassengerService passengerService) {
        List<Long> passengerIdList = new ArrayList<>();
        List<Passenger> passengerResultList = null;
        Map<Long, Passenger> passengerMap = new HashMap<>();

        for (RuleHitDetail rhd : ruleHitDetailList) {
            Long passengerId = rhd.getPassengerId();
            passengerIdList.add(passengerId);

        }

        if (!passengerIdList.isEmpty()) {
            passengerResultList = passengerService.getPaxByPaxIdList(passengerIdList);

            for (Passenger passenger : passengerResultList) {
                passengerMap.put(passenger.getId(), passenger);

            }
        }

        return passengerMap;

    }


    public static Map<Long, List<Long>> createPassengerFlightMap(List<RuleHitDetail> ruleHitDetailList, PassengerService passengerService) {
        List<Long> ruleHitDetailPassengerIdList = new ArrayList<>();
        Map<Long, List<Long>> passengerFlightMap = new HashMap<>();

        for (RuleHitDetail rhd : ruleHitDetailList) {
            Long passengerId = rhd.getPassengerId();
            ruleHitDetailPassengerIdList.add(passengerId);
        }

        // TODO: check for no rule hits, empty ruleHitDetailPassengerIdList
        if (!ruleHitDetailPassengerIdList.isEmpty()) {
            List<FlightPax> allFlightPaxByPassengerId = passengerService.findFlightPaxFromPassengerIds(ruleHitDetailPassengerIdList);

            for (FlightPax flightPax : allFlightPaxByPassengerId) {
                Long passengerId = flightPax.getPassengerId();
                Long flightId = flightPax.getFlightId();
                if (passengerFlightMap.containsKey(passengerId)) {
                    passengerFlightMap.get(passengerId).add(flightId);
                } else {
                    List<Long> newFlightList = new ArrayList<>();
                    newFlightList.add(flightId);
                    passengerFlightMap.put(passengerId, newFlightList);
                }
            }
        }

        return passengerFlightMap;
    }

    /**
     * Eliminates duplicates and adds flight id, if missing.
     *
     * @param result
     * @param targetingResultServices
     * @return
     */
    public static RuleServiceResult ruleResultPostProcesssing(
            RuleServiceResult result, TargetingResultServices targetingResultServices) {

        PassengerService passengerService = targetingResultServices.getPassengerService();

        //logger.info("Entering ruleResultPostProcesssing().");
        // get the list of RuleHitDetail objects returned by the Rule Engine
        List<RuleHitDetail> resultList = result.getResultList();

        // create a Map to eliminate duplicates
        Map<RuleHitDetail, RuleHitDetail> resultMap = new HashMap<>();

        if (logger.isInfoEnabled()) {
            logger.info("Number of hits --> " + resultList.size());
        }
        resultList = filterRuleHitDetails(resultList, targetingResultServices);

        Bench.start("qwerty1", "Before for RuleHitDetail loop in TargetingResultUtils.");

        //can this be done from a PNR? We have the message in scope...
        Map<Long, List<Long>> passengerFlightMap = createPassengerFlightMap(resultList, passengerService);

        for (RuleHitDetail rhd : resultList) {
            if (rhd.getFlightId() == null) {
                // get all the flights for the passenger
                // and replicate the RuleHitDetail object, for each flight id
                // Note that the RuleHitDetail key is (UdrId, EngineRuleId,
                // PassengerId, FlightId)

                List<Long> flightIdList = passengerFlightMap.get(rhd.getPassengerId());

                if (flightIdList != null && !CollectionUtils.isEmpty(flightIdList)) {
                    try {
                        Bench.start("qwerty2", "Before for Flight loop in TargetingResultUtils.");
                        for (Long flightId : flightIdList) {
                            RuleHitDetail newrhd = rhd.clone();
                            processPassengerFlight(newrhd, flightId,
                                    resultMap);
                        }
                        Bench.end("qwerty2", "Before for Flight loop in TargetingResultUtils.");
                    } catch (CloneNotSupportedException cnse) {
                        logger.error("error - clone not supported:", cnse);
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
        Bench.end("qwerty1", "After for RuleHitDetail loop in TargetingResultUtils.");
        // Now create the return list from the set, thus eliminating duplicates.
        RuleServiceResult ret = new BasicRuleServiceResult(
                new LinkedList<>(resultMap.values()),
                result.getExecutionStatistics());
        //logger.info("Exiting ruleResultPostProcesssing().");
        return ret;
    }

    private static List<RuleHitDetail> filterRuleHitDetails(List<RuleHitDetail> resultList, TargetingResultServices targetingResultServices) {
        /*
         * Take care of run away rules by not creating hits/cases on them as well as flagging them in the database.
         * */


        Map<Long, Integer> ruleIdAsKeyCountAsValue = new HashMap<>();
        for (RuleHitDetail ruleHitDetail : resultList) {
            Long udrRuleId = ruleHitDetail.getUdrRuleId();
            if (ruleIdAsKeyCountAsValue.containsKey(udrRuleId)) {
                Integer hitCount = ruleIdAsKeyCountAsValue.get(udrRuleId);
                ruleIdAsKeyCountAsValue.put(udrRuleId, hitCount + 1);
            } else {
                ruleIdAsKeyCountAsValue.put(udrRuleId, 1);
            }
        }

        List<RuleHitDetail> filteredList = new ArrayList<>();
        boolean ruleFiltered = false;
        Set<Long> filteredRules = new HashSet<>();
        AppConfigurationService appConfigurationService = targetingResultServices.getAppConfigurationService();
        String maxRuleHitsAsString = appConfigurationService.findByOption("MAX_RULE_HITS").getValue();
        Integer MAX_RULE_HITS = maxRuleHitsAsString == null ? Integer.MAX_VALUE : Integer.parseInt(maxRuleHitsAsString);
        for (RuleHitDetail ruleHitDetail : resultList) {
            Long udrRuleId = ruleHitDetail.getUdrRuleId();
            if (ruleIdAsKeyCountAsValue.get(udrRuleId) <= MAX_RULE_HITS) {
                filteredList.add(ruleHitDetail);
            } else {
                ruleFiltered = true;
                filteredRules.add(udrRuleId);
            }
        }

        if (ruleFiltered) {
            logger.warn("WARNING: THE FOLLOWING RULE(S) WITH PRIMARY KEY(S) LISTED DID NOT RUN DUE " +
                    "TO PULLING BACK MORE THAN THE MAX NUMBER OF " + MAX_RULE_HITS + " HITS: " + Arrays.toString(filteredRules.toArray()));
            logger.info("Updating Rule Flag");
            try {
                RuleMetaRepository ruleMetaRepository = targetingResultServices.getRuleMetaRepository();
                ruleMetaRepository.flagUdrRule(filteredRules);
            } catch (Exception databaseException) {
                logger.error("Caught error updating UDR: " + databaseException.getMessage());
            }
        }

        resultList = filteredList;
        return resultList;
    }

    private static void processPassengerFlight(RuleHitDetail rhd,
                                               Long flightId, Map<RuleHitDetail, RuleHitDetail> resultMap) {

        //logger.info("Entering processPassengerFlight().");
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
        //logger.info("Exiting processPassengerFlight().");
    }

    public static void updateRuleExecutionContext(RuleServiceResult res, RuleResults ruleResults) {
        logger.info("Entering updateRuleExecutionContext().");
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
        ruleResults.setTargetingResult(hitSummaryMap.values());
        logger.info("Exiting updateRuleExecutionContext().");
    }
}
