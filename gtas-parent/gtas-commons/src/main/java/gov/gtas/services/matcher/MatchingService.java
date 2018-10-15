package gov.gtas.services.matcher;

import java.util.List;

import gov.gtas.model.Passenger;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

public interface MatchingService {
	
	List<PaxWatchlistLinkVo> findByPassengerId(Long id);

	void saveWatchListMatchByPaxId(Long id);

	void saveWatchListMatchByPaxId(Passenger passenger);

	int findMatchesBasedOnTimeThreshold();
}
