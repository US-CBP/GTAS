package gov.gtas.services.matcher;

import java.util.List;

import gov.gtas.model.Passenger;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

public interface MatchingService {
	public List<PaxWatchlistLinkVo> findByPassengerId(Long id);
	
	public void saveWatchListMatchByPaxId(Long id);
	
	public void saveWatchListMatchByPaxId(Passenger passenger);
	
	public int findMatchesBasedOnTimeThreshold();
}
