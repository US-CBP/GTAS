package gov.gtas.svc;

import gov.gtas.model.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

public interface GraphRulesService {
	Set<RuleHitDetail> graphResults(Set<Passenger> passengers) throws URISyntaxException;

	Set<HitDetail> generateHitDetails(List<RuleHitDetail> filteredList);

}
