package gov.gtas.services.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.gtas.model.Case;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.services.matcher.quickmatch.QuickMatcher;

public class MatcherParameters {
    private Map<Long, Case> caseMap;
    private Map<Long, RuleCat> ruleCatMap;
    private List<Watchlist> _watchlists;
    private Map<Long, List<WatchlistItem>> watchlistListMap;
    private float threshold;
    private int dobYearOffset; 
   	private QuickMatcher qm;
    
    /*
     * Map<Passenger.id, Set<WatchlistItem.id>>
     */
	private Map<Long, Set<Long>> paxWatchlistLinks;

	private List<HashMap<String, String>> derogList = new ArrayList<>();

	public int getDobYearOffset() {
		return dobYearOffset;
	}

	public void setDobYearOffset(int dobYearOffset) {
		this.dobYearOffset = dobYearOffset;
	}

	public List<HashMap<String, String>> getDerogList() {
		return derogList;
	}

	public void addDerogList(List<HashMap<String, String>> derogList) {
		if(this.derogList == null)
			this.derogList = new ArrayList<>();
		this.derogList.addAll(derogList);
	}

	public Set<Long> getPaxWatchlistLinks(Long passengerId) {
		Set<Long> wlItemIds = paxWatchlistLinks.get(passengerId);
		if (wlItemIds == null) {
			wlItemIds = new HashSet<>();
		}
		return wlItemIds;
	}

	public void setPaxWatchlistLinks(Map<Long, Set<Long>> paxWatchlistLinks) {
		this.paxWatchlistLinks = paxWatchlistLinks;
	}

	public QuickMatcher getQm() {
		return qm;
	}

	public void setQm(QuickMatcher qm) {
		this.qm = qm;
	}

	Map<Long, Case> getCaseMap() {
        return caseMap;
    }

    void setCaseMap(Map<Long, Case> caseMap) {
        this.caseMap = caseMap;
    }

    Map<Long, RuleCat> getRuleCatMap() {
        return ruleCatMap;
    }

    void setRuleCatMap(Map<Long, RuleCat> ruleCatMap) {
        this.ruleCatMap = ruleCatMap;
    }

    List<Watchlist> get_watchlists() {
        return _watchlists;
    }

    void set_watchlists(List<Watchlist> _watchlists) {
        this._watchlists = _watchlists;
    }

    Map<Long, List<WatchlistItem>> getWatchlistListMap() {
        return watchlistListMap;
    }

    void setWatchlistListMap(Map<Long, List<WatchlistItem>> watchlistListMap) {
        this.watchlistListMap = watchlistListMap;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

}
