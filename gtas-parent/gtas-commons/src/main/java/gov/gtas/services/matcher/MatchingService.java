package gov.gtas.services.matcher;

import java.util.List;
import java.util.Set;

import gov.gtas.model.*;

public interface MatchingService {

	void performFuzzyMatching(Long id);

	Set<HitDetail> performFuzzyMatching(Flight flight, Passenger passenger, MatcherParameters matcherParameters);

	int findMatchesBasedOnTimeThreshold(List<MessageStatus> messageStatuses);
}
