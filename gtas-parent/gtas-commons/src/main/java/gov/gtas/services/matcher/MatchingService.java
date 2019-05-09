package gov.gtas.services.matcher;

import java.util.List;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

public interface MatchingService {
	
	List<PaxWatchlistLinkVo> findByPassengerId(Long id);

	void performFuzzyMatching(Long id);

	void performFuzzyMatching(Flight flight, Passenger passenger, MatcherParameters matcherParameters);

	int findMatchesBasedOnTimeThreshold();
}
