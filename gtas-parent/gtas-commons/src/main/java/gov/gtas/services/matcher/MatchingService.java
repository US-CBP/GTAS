package gov.gtas.services.matcher;

import java.util.List;
import java.util.Map;

import gov.gtas.model.Case;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

public interface MatchingService {
	
	List<PaxWatchlistLinkVo> findByPassengerId(Long id);

//	void saveWatchListMatchByPaxId(Long id);

	void saveWatchListMatchByPaxId(Map<Long, Case> existingCases, Flight flight,Passenger passenger);

	int findMatchesBasedOnTimeThreshold();
}
