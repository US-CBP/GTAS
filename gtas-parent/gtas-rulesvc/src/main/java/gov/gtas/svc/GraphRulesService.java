package gov.gtas.svc;

import gov.gtas.bo.RuleHitDetail;
import gov.gtas.model.Case;
import gov.gtas.model.Flight;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Passenger;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

public interface GraphRulesService {
    Set<RuleHitDetail> graphResults(Set<Passenger> passengers) throws URISyntaxException;

    void saveResults(Set<HitsSummary> hitsSummaries, Set<Case> newCases);

    Set<Case> graphCases(Set<RuleHitDetail> graphHitDetailSet);

    List<HitsSummary> getHitsSummariesFromRuleDetails(List<RuleHitDetail> filteredList);

    void updateFlightGraphHitCount(Set<Flight> passengerFlights);
}
