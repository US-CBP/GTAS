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
import gov.gtas.model.*;
import gov.gtas.repository.udr.RuleMetaRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.PassengerService;
import gov.gtas.svc.TargetingResultServices;
import gov.gtas.svc.TargetingServiceResults;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetingResultUtils {
    private static final Logger logger = LoggerFactory
            .getLogger(TargetingResultUtils.class);

    static Map<Long, Flight> createFlightMap(List<RuleHitDetail> ruleHitDetailList, PassengerService passengerService) {
        List<Long> flightIdList = new ArrayList<>();
        List<Flight> flightResultList;
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


    static Map<Long, Passenger> createPassengerMap(List<RuleHitDetail> ruleHitDetailList, PassengerService passengerService) {
        List<Long> passengerIdList = new ArrayList<>();
        List<Passenger> passengerResultList;
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


    static Map<Long, List<Long>> createPassengerFlightMap(List<RuleHitDetail> ruleHitDetailList, PassengerService passengerService) {
        List<Long> ruleHitDetailPassengerIdList = new ArrayList<>();
        Map<Long, List<Long>> passengerFlightMap = new HashMap<>();

        for (RuleHitDetail rhd : ruleHitDetailList) {
            Long passengerId = rhd.getPassengerId();
            ruleHitDetailPassengerIdList.add(passengerId);
        }

        // TODO: check for no rule hits, empty ruleHitDetailPassengerIdList
        if (!ruleHitDetailPassengerIdList.isEmpty()) {
            Set<FlightPax> allFlightPaxByPassengerId = passengerService.findFlightPaxFromPassengerIds(ruleHitDetailPassengerIdList);

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
            logger.debug("Number of hits --> " + resultList.size());
        }
        resultList = filterRuleHitDetails(resultList, targetingResultServices);


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
                        for (Long flightId : flightIdList) {
                            RuleHitDetail newrhd = rhd.clone();
                            processPassengerFlight(newrhd, flightId,
                                    resultMap);
                        }
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
        return new BasicRuleServiceResult(
                new LinkedList<>(resultMap.values()),
                result.getExecutionStatistics());
    }

    public static List<TargetingServiceResults> batchResults(List<TargetingServiceResults> targetingServiceResultsList, int BATCH_SIZE) {

        List<TargetingServiceResults> batchedResults = new ArrayList<>();
        TargetingServiceResults conglomerateResults = new TargetingServiceResults();
        int counter = 0;
        while (!targetingServiceResultsList.isEmpty()) {
            TargetingServiceResults targetingServiceResults = targetingServiceResultsList.get(0);
            if (targetingServiceResults != null) {
                Set<Case> casesSet = conglomerateResults.getCaseSet();
                List<HitsSummary> hitsSummaries = conglomerateResults.getHitsSummaryList();
                if (casesSet == null) {
                    conglomerateResults.setCaseSet(targetingServiceResults.getCaseSet());
                } else {
                    if (targetingServiceResults.getCaseSet() != null) {
                        conglomerateResults.getCaseSet().addAll(targetingServiceResults.getCaseSet());
                    }
                }
                if (hitsSummaries == null) {
                    conglomerateResults.setHitsSummaryList(targetingServiceResults.getHitsSummaryList());
                } else {
                    if (targetingServiceResults.getHitsSummaryList() != null) {
                        conglomerateResults.getHitsSummaryList().addAll(targetingServiceResults.getHitsSummaryList());
                    }
                }
                counter++;
                if (targetingServiceResultsList.size() == 1) {
                    batchedResults.add(conglomerateResults);
                } else {
                    if (counter >= BATCH_SIZE) {
                        batchedResults.add(conglomerateResults);
                        conglomerateResults = new TargetingServiceResults();
                        counter = 1;
                    }
                }
                targetingServiceResultsList.remove(0);
            }
        }
        return batchedResults;
    }

    public static List<TargetingServiceResults> getTargetingResults(Set<Case> casesSet, List<HitsSummary> hitsSummaryList) {
        Map<Long, Set<Case>> caseToFlightIdMap = new HashMap<>();
        List<TargetingServiceResults> targetingServiceResultsList = new ArrayList<>();
        for (Case caze : casesSet) {
            Long flightId = caze.getFlightId();
            if (caseToFlightIdMap.containsKey(flightId)) {
                caseToFlightIdMap.get(flightId).add(caze);
            } else {
                Set<Case> objectHashSet = new HashSet<>();
                objectHashSet.add(caze);
                caseToFlightIdMap.put(flightId, objectHashSet);
            }
        }

        Map<Long, List<HitsSummary>> hitSummaryToFlightIdMap = new HashMap<>();
        for (HitsSummary hitsSummary : hitsSummaryList) {
            Long flightId = hitsSummary.getFlightId();
            if (hitSummaryToFlightIdMap.containsKey(flightId)) {
                hitSummaryToFlightIdMap.get(flightId).add(hitsSummary);
            } else {
                List<HitsSummary> hitsSummaries = new ArrayList<>();
                hitsSummaries.add(hitsSummary);
                hitSummaryToFlightIdMap.put(flightId, hitsSummaries);
            }
        }
        Set<Long> flightIdsToProcess = new HashSet<>();
        flightIdsToProcess.addAll(caseToFlightIdMap.keySet());
        flightIdsToProcess.addAll(hitSummaryToFlightIdMap.keySet());
        for (Long flightId : flightIdsToProcess) {
            Set<Case> cases = caseToFlightIdMap.get(flightId);
            List<HitsSummary> hitsSummaries = hitSummaryToFlightIdMap.get(flightId);
            TargetingServiceResults targetingServiceResults = new TargetingServiceResults();
            targetingServiceResults.setCaseSet(cases);
            targetingServiceResults.setHitsSummaryList(hitsSummaries);
            targetingServiceResultsList.add(targetingServiceResults);
        }
        return targetingServiceResultsList;
    }

    public static List<RuleHitDetail> filterRuleHitDetails(List<RuleHitDetail> resultList, TargetingResultServices targetingResultServices) {
        /*
         * Take care of run away rules by not creating hits/cases on them as well as flagging them in the database.
         * */
        Set<RuleHitDetail> rhdSet = new HashSet<>(resultList);
        Map<Long, Integer> udrRuleIdAsKeyCountAsValue = new HashMap<>();
        Map<Long, Integer> graphRuleIdAsKeyCountAsValue = new HashMap<>();
        Map<Long, Integer> watchlistIdAsKeyCountAsValue = new HashMap<>();
        for (RuleHitDetail ruleHitDetail : rhdSet) {
            if (HitTypeEnum.GH == ruleHitDetail.getHitType()) {
                Long graphRuleId = ruleHitDetail.getRuleId();
                if (graphRuleIdAsKeyCountAsValue.containsKey(graphRuleId)) {
                    Integer hitCount = graphRuleIdAsKeyCountAsValue.get(graphRuleId);
                    graphRuleIdAsKeyCountAsValue.put(graphRuleId, hitCount + 1);
                } else {
                    graphRuleIdAsKeyCountAsValue.put(graphRuleId, 1);
                }
            } else if (HitTypeEnum.PD == ruleHitDetail.getHitType()
                    || HitTypeEnum.P == ruleHitDetail.getHitType()
                    || HitTypeEnum.D == ruleHitDetail.getHitType()) {
                Long wlRuleId = ruleHitDetail.getRuleId();
                if (watchlistIdAsKeyCountAsValue.containsKey(wlRuleId)) {
                    Integer hitCount = watchlistIdAsKeyCountAsValue.get(wlRuleId);
                    watchlistIdAsKeyCountAsValue.put(wlRuleId, hitCount + 1);
                } else {
                    watchlistIdAsKeyCountAsValue.put(wlRuleId, 1);
                }
            } else {
                Long udrRuleId = ruleHitDetail.getUdrRuleId();
                if (udrRuleIdAsKeyCountAsValue.containsKey(udrRuleId)) {
                    Integer hitCount = udrRuleIdAsKeyCountAsValue.get(udrRuleId);
                    udrRuleIdAsKeyCountAsValue.put(udrRuleId, hitCount + 1);
                } else {
                    udrRuleIdAsKeyCountAsValue.put(udrRuleId, 1);
                }
            }
        }

        List<RuleHitDetail> filteredList = new ArrayList<>();
        Set<Long> graphFilteredRules = new HashSet<>();
        Set<Long> watchlistFilteredRules = new HashSet<>();
        Set<Long> udrFilteredRules = new HashSet<>();
        AppConfigurationService appConfigurationService = targetingResultServices.getAppConfigurationService();
        String maxRuleHitsAsString = appConfigurationService.findByOption("MAX_RULE_HITS").getValue();
        Integer MAX_RULE_HITS = maxRuleHitsAsString == null ? Integer.MAX_VALUE : Integer.parseInt(maxRuleHitsAsString);
        for (RuleHitDetail ruleHitDetail : rhdSet) {
            Long ruleId;
            if (HitTypeEnum.GH == ruleHitDetail.getHitType()) {
                ruleId = ruleHitDetail.getRuleId();
                if (graphRuleIdAsKeyCountAsValue.get(ruleId) <= MAX_RULE_HITS) {
                    filteredList.add(ruleHitDetail);
                } else {
                    graphFilteredRules.add(ruleId);
                }
            } else if (HitTypeEnum.PD == ruleHitDetail.getHitType()
                    || HitTypeEnum.P == ruleHitDetail.getHitType()
                    || HitTypeEnum.D == ruleHitDetail.getHitType()) {
                ruleId = ruleHitDetail.getRuleId();
                if (watchlistIdAsKeyCountAsValue.get(ruleId) <= MAX_RULE_HITS) {
                    filteredList.add(ruleHitDetail);
                } else {
                    watchlistFilteredRules.add(ruleId);
                }
            } else {
                ruleId = ruleHitDetail.getUdrRuleId(); //UDR uses UDRID instead of rule ID.
                if (udrRuleIdAsKeyCountAsValue.get(ruleId) <= MAX_RULE_HITS) {
                    filteredList.add(ruleHitDetail);
                } else {
                    udrFilteredRules.add(ruleId);
                }
            }
        }

        if (!watchlistFilteredRules.isEmpty() || !udrFilteredRules.isEmpty() || !graphFilteredRules.isEmpty()) {
            if (!watchlistFilteredRules.isEmpty()) {
                logger.warn("WARNING: THE FOLLOWING WATCHLIST RULE(S) WITH PRIMARY KEY(S) LISTED DID NOT RUN DUE " +
                        "TO PULLING BACK MORE THAN THE MAX NUMBER OF " + MAX_RULE_HITS + " HITS: " + Arrays.toString(watchlistFilteredRules.toArray()));
            }
            if (!udrFilteredRules.isEmpty()) {
                logger.warn("WARNING: THE FOLLOWING UDR RULE(S) WITH PRIMARY KEY(S) LISTED DID NOT RUN DUE " +
                        "TO PULLING BACK MORE THAN THE MAX NUMBER OF " + MAX_RULE_HITS + " HITS: " + Arrays.toString(udrFilteredRules.toArray()));
            }
            if (!graphFilteredRules.isEmpty()) {
                logger.warn("WARNING: THE FOLLOWING GRAPH RULE(S) WITH PRIMARY KEY(S) LISTED DID NOT RUN DUE " +
                        "TO PULLING BACK MORE THAN THE MAX NUMBER OF " + MAX_RULE_HITS + " HITS: " + Arrays.toString(graphFilteredRules.toArray()));
            }
            try {
                if (!udrFilteredRules.isEmpty()) {
                    logger.debug("Updating UDR over max hits Flag");
                    RuleMetaRepository ruleMetaRepository = targetingResultServices.getRuleMetaRepository();
                    ruleMetaRepository.flagUdrRule(udrFilteredRules);
                }
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
        if (resrhd != null && !resrhd.getRuleId().equals(rhd.getRuleId())) {
            resrhd.incrementHitCount();
            if (resrhd.getUdrRuleId() != null) {
                logger.debug("This is a rule hit so increment the rule hit count.");
                // this is a rule hit
                resrhd.incrementRuleHitCount();
            } else {
                logger.debug("This is a watch list hit.");
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
        logger.debug("Entering updateRuleExecutionContext().");
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
        logger.debug("Exiting updateRuleExecutionContext().");
    }
}
