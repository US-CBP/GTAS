package gov.gtas.services.matcher;

import gov.gtas.model.Case;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;

import java.util.List;
import java.util.Map;

public class MatcherParameters {
    private Map<Long, Case> caseMap;
    private Map<Long, RuleCat> ruleCatMap;
    private List<Watchlist> _watchlists;
    private Map<Long, List<WatchlistItem>> watchlistListMap;
    private float threshold;

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
