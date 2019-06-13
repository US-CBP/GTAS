/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.svc;

import gov.gtas.bo.RuleHitDetail;
import gov.gtas.config.Neo4JConfig;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.*;
import gov.gtas.model.lookup.PassengerTypeCode;
import gov.gtas.repository.*;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.PassengerService;
import gov.gtas.svc.util.TargetingResultCaseMgmtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class GraphRulesServiceImpl implements GraphRulesService {

    private Logger logger = LoggerFactory.getLogger(GraphRulesServiceImpl.class);

    private final
    GraphRuleRepository graphRuleRepository;

    private final Neo4JClient neo4JClient;

    private final PassengerRepository passengerRepository;

    private final HitsSummaryRepository hitsSummaryRepository;

    private final CaseDispositionService caseDispositionService;

    private final PassengerService passengerService;

    private final CaseDispositionRepository caseDispositionRepository;


    private final FlightGraphHitsRepository flightGraphHitsRepository;


    @Autowired
    public GraphRulesServiceImpl(
            GraphRuleRepository graphRuleRepository,
            AppConfigurationRepository appConfigurationRepository,
            PassengerRepository passengerRepository,
            HitsSummaryRepository hitsSummaryRepository,
            CaseDispositionService caseDispositionService,
            PassengerService passengerService,
            CaseDispositionRepository caseDispositionRepository,
            Neo4JConfig neo4JConfig, FlightGraphHitsRepository flightGraphHitsRepository) {
        this.graphRuleRepository = graphRuleRepository;
        this.hitsSummaryRepository = hitsSummaryRepository;
        this.caseDispositionService = caseDispositionService;
        this.passengerService = passengerService;
        this.caseDispositionRepository = caseDispositionRepository;
        this.flightGraphHitsRepository = flightGraphHitsRepository;
        String url = appConfigurationRepository.findByOption(AppConfigurationRepository.GRAPH_DB_URL).getValue();
        Boolean neo4J = Boolean.valueOf(
                appConfigurationRepository.findByOption(AppConfigurationRepository.GRAPH_DB_TOGGLE).getValue()
        );
        if (neo4J && neo4JConfig.enabled()) {
            this.neo4JClient = new Neo4JClient(url, neo4JConfig.neoUserName(), neo4JConfig.neoPassword());
        } else {
            this.neo4JClient = null;
        }
        this.passengerRepository = passengerRepository;
    }

    @Override
    @Transactional
    public void updateFlightGraphHitCount(Set<Flight> flightSet) {
        if (flightSet != null) {
            Set<FlightHitsGraph> flightHitsGraphs = new HashSet<>();
            for (Flight flight : flightSet) {
                Integer flightGraphCount = hitsSummaryRepository.graphHitCount(flight.getId());
                FlightHitsGraph fhg = new FlightHitsGraph();
                fhg.setFlightId(flight.getId());
                fhg.setHitCount(flightGraphCount);
                flightHitsGraphs.add(fhg);
            }
            flightGraphHitsRepository.saveAll(flightHitsGraphs);
        }
    }

    @Override
    @Transactional
    public List<HitsSummary> getHitsSummariesFromRuleDetails(List<RuleHitDetail> ruleHitDetails) {
        if (ruleHitDetails.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> paxIds = ruleHitDetails.stream().map(RuleHitDetail::getPassengerId).collect(Collectors.toSet());
        Set<Passenger> passengerSet = passengerRepository.getPassengerWithHits(paxIds);
        Map<Long, HitsSummary> paxHitSummary = passengerSet
                .stream()
                .filter(p -> !(p.getHits() == null || p.getHits().isEmpty()))
                .collect(toMap(
                        Passenger::getId,
                        p -> p.getHits().iterator().next()
                ));

        int newHitSummary = 0;
        int totalHits = 0;
        for (RuleHitDetail ruleHitDetail : ruleHitDetails) {
            //existingHit
            if (paxHitSummary.containsKey(ruleHitDetail.getPassengerId())) {
                HitsSummary existingHitsSummary = paxHitSummary.get(ruleHitDetail.getPassengerId());
                HitDetail hitDetail = getHitDetail(ruleHitDetail, existingHitsSummary);
                if (existingHitsSummary.getHitdetails().add(hitDetail)) {
                    int graphHitCounts = 0;
                    for (HitDetail hd : existingHitsSummary.getHitdetails()) {
                        if (hd.getHitType() != null && HitTypeEnum.GH.name().equalsIgnoreCase(hd.getHitType())) {
                            graphHitCounts++;
                        }
                    }
                    existingHitsSummary.setGraphHitCount(graphHitCounts);
                    existingHitsSummary.setSaveHits(true);
                    totalHits++;
                }

            } else {  // new hits.
                HitsSummary hitsSummary = getHitsSummary(ruleHitDetail);
                HitDetail hitDetail = getHitDetail(ruleHitDetail, hitsSummary);
                hitsSummary.getHitdetails().add(hitDetail);
                hitsSummary.setGraphHitCount(1);
                paxHitSummary.put(ruleHitDetail.getPassengerId(), hitsSummary);
                newHitSummary++;
                totalHits++;
            }
        }
        logger.debug("Processing " + newHitSummary + " new hits with a total of  " + totalHits + " hits.");
        return paxHitSummary.values()
                .stream()
                .filter(HitsSummary::getSaveHits)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveResults(Set<HitsSummary> hitsSummaries, Set<Case> newCases) {
        if (hitsSummaries.isEmpty() && (newCases == null || newCases.isEmpty())) {
            return;
        }

        if (!hitsSummaries.isEmpty()) {
            hitsSummaryRepository.saveAll(hitsSummaries);
            logger.debug("Created or modified(including adding hit details) " + hitsSummaries.size() + " hits summarie(s).");
        }

        if (newCases != null && !newCases.isEmpty()) {
            caseDispositionRepository.saveAll(newCases);
            logger.debug("Created or modified " + newCases.size() + " case(s).");
        }
    }

    private HitsSummary getHitsSummary(RuleHitDetail ruleHitDetail) {
        HitsSummary hitsSummary = new HitsSummary();
        hitsSummary.setSaveHits(true);
        hitsSummary.setCreatedDate(new Date());
        hitsSummary.setFlightId(ruleHitDetail.getFlightId());
        hitsSummary.setHitType(HitTypeEnum.GH.toString());
        hitsSummary.setPaxId(ruleHitDetail.getPassengerId());
        hitsSummary.setRuleHitCount(0);
        hitsSummary.setWatchListHitCount(0);
        hitsSummary.setGraphHitCount(1);
        return hitsSummary;
    }

    private HitDetail getHitDetail(RuleHitDetail ruleHitDetail, HitsSummary existingHitsSummary) {
        HitDetail hitDetail = new HitDetail();
        hitDetail.setParent(existingHitsSummary);
        hitDetail.setDescription(ruleHitDetail.getDescription());
        hitDetail.setCreatedDate(new Date());
        hitDetail.setHitType(ruleHitDetail.getHitType().toString());
        hitDetail.setRuleId(ruleHitDetail.getRuleId());
        hitDetail.setTitle(ruleHitDetail.getTitle());
        hitDetail.setRuleConditions(ruleHitDetail.getCipherQuery());
        return hitDetail;
    }

    @Override
    @Transactional
    public Set<Case> graphCases(Set<RuleHitDetail> ruleHitDetails) {
        return TargetingResultCaseMgmtUtils.ruleResultPostProcesssing
                (new ArrayList<>(ruleHitDetails),
                        caseDispositionService, passengerService);
    }

    @Override
    @Transactional
    public Set<RuleHitDetail> graphResults(Set<Passenger> passengers) {
        Iterable<GraphRule> graphRules = getGraphRules();
        Set<RuleHitDetail> ruleHitDetails = new HashSet<>();
        Map<String, List<Passenger>> paxMap = new HashMap<>();
        for (Passenger p : passengers) {
            if (p.getPassengerIDTag() != null && p.getPassengerIDTag().getIdTag() != null) {
                if (paxMap.containsKey(p.getPassengerIDTag().getIdTag())) {
                    paxMap.get(p.getPassengerIDTag().getIdTag()).add(p);
                } else {
                    List<Passenger> passengerList = new ArrayList<>();
                    passengerList.add(p);
                    paxMap.put(p.getPassengerIDTag().getIdTag(), passengerList);
                }
            }
        }

        Long GRAPH_DATABASE_INDICATOR = -1L;
        for (GraphRule graphRule : graphRules) {
            Set<String> passengerHitIds = getPassengerHitIds(graphRule, paxMap.keySet()); //This command runs the rules!
            for (String idTag : passengerHitIds) {
                for (Passenger passenger : paxMap.get(idTag)) {
                    RuleHitDetail rhd = new RuleHitDetail();
                    rhd.setFlightId(passenger.getFlight().getId());
                    rhd.setHitType(HitTypeEnum.GH);
                    rhd.setPassenger(passenger);
                    rhd.setPassengerName(passenger.getPassengerDetails().getFirstName() + " " + passenger.getPassengerDetails().getLastName());
                    rhd.setTitle(graphRule.getTitle());
                    rhd.setDescription(graphRule.getDescription());
                    rhd.setHitRule(graphRule.getDescription() + ":" + graphRule.getId());
                    rhd.setHitCount(1);
                    rhd.setRuleId(graphRule.getId());
                    rhd.setPassengerId(passenger.getId());
                    rhd.setPassengerType(PassengerTypeCode.P);
                    rhd.setFlightId(passenger.getFlight().getId());
                    rhd.setUdrRuleId(GRAPH_DATABASE_INDICATOR);
                    rhd.setCipherQuery(graphRule.getCipherQuery());
                    ruleHitDetails.add(rhd);
                }
            }
        }


        return ruleHitDetails;
    }


    private Set<String> getPassengerHitIds(GraphRule graphRule, Set<String> paxIds) {
        return neo4JClient.runQueryAndReturnPassengerIdHits(graphRule, paxIds);
    }

    private Iterable<GraphRule> getGraphRules() {
        return graphRuleRepository.findAll();
    }


}
