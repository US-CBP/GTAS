package gov.gtas.svc;

import gov.gtas.model.GraphHitDetail;
import gov.gtas.model.Passenger;

import java.net.URISyntaxException;
import java.util.Set;

public interface GraphRulesService {
    Set<GraphHitDetail> graphResults(Set<Passenger> passengers) throws URISyntaxException;

    void saveResults(Set<GraphHitDetail> graphHitDetailSet);
}
