package gov.gtas.services.matcher;

import java.util.List;

import gov.gtas.model.Flight;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

public interface MatchingService {
	
	List<PaxWatchlistLinkVo> findByPassengerId(Long id);

	void performFuzzyMatching(Long id);

    int performFuzzyMatching(Flight flight, Passenger passenger, MatcherParameters matcherParameters);

    int findMatchesBasedOnTimeThreshold(List<MessageStatus> messageStatuses);
}
