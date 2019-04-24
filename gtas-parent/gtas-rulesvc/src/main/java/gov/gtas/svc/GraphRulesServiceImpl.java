package gov.gtas.svc;

import gov.gtas.model.GraphHitDetail;
import gov.gtas.model.GraphRule;
import gov.gtas.model.Passenger;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.GraphHitDetailRepository;
import gov.gtas.repository.GraphRuleRepository;
import gov.gtas.repository.PassengerRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GraphRulesServiceImpl implements GraphRulesService {


    private final
    GraphRuleRepository graphRuleRepository;

    private final Neo4JClient neo4JClient;

    private final PassengerRepository passengerRepository;

    private final GraphHitDetailRepository graphHitDetailRepository;

    public GraphRulesServiceImpl(
            GraphRuleRepository graphRuleRepository,
            AppConfigurationRepository appConfigurationRepository,
            PassengerRepository passengerRepository,
            GraphHitDetailRepository graphHitDetailRepository) {
        this.graphRuleRepository = graphRuleRepository;
        this.graphHitDetailRepository = graphHitDetailRepository;
        String url = appConfigurationRepository.findByOption(AppConfigurationRepository.GRAPH_DB_URL).getValue();
        this.neo4JClient = new Neo4JClient(url);
        this.passengerRepository = passengerRepository;
    }


    @Override
    public void saveResults(Set<GraphHitDetail> graphHitDetailSet) {
        if (graphHitDetailSet.isEmpty()) {
            return;
        }
        Set<Long> paxIds = graphHitDetailSet.stream().map(GraphHitDetail::getPassenger_id).collect(Collectors.toSet());
        Set<Passenger> passengerSet = passengerRepository.getPassengerWithGraphHit(paxIds);
        Set<GraphHitDetail> existingDetails = passengerSet.stream()
                .flatMap(p -> p.getGraphHitDetails().stream())
                .collect(Collectors.toSet());
        Set<GraphHitDetail> newHits =
                graphHitDetailSet.stream().filter(existingDetails::contains).collect(Collectors.toSet());
        graphHitDetailRepository.saveAll(newHits);
    }

    @Override
    public Set<GraphHitDetail> graphResults(Set<Passenger> passengers) {
        Iterable<GraphRule> graphRules = getGraphRules();
        Set<GraphHitDetail> graphHitDetails = new HashSet<>();
        Map<String, Passenger> paxMap = passengers.stream().collect(Collectors
                .toMap(p -> p.getPassengerIDTag().getIdTag(), p -> p));
        for (GraphRule graphRule : graphRules) {
            Set<String> passengerHitIds = getPassengerHitIds(graphRule, paxMap.keySet());
            for (String idTag : passengerHitIds) {
                Passenger passenger = paxMap.get(idTag);
                GraphHitDetail graphHitDetail = new GraphHitDetail();
                graphHitDetail.setGraphRule(graphRule);
                graphHitDetail.setPassenger(passenger);
                graphHitDetail.setPassenger_id(passenger.getId());
                graphHitDetails.add(graphHitDetail);
            }
        }
        return graphHitDetails;
    }


    private Set<String> getPassengerHitIds(GraphRule graphRule, Set<String> paxIds) {
        return neo4JClient.runQueryAndReturnPassengerIdHits(graphRule, paxIds);
    }

    private Iterable<GraphRule> getGraphRules() {
        return graphRuleRepository.findAll();
    }


}
