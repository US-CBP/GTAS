/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matcher.quickmatch;

public class DerogHit {
	public static final String WATCH_LIST_NAME ="WatchlistName";
	private String derogId;
	private String watchlistName;
	private String clause;
	private float percent;

	public DerogHit(String derogId, String clause, float percent, String watchlistName) {
		this.derogId = derogId;
		this.clause = clause;
		this.percent = percent;
		this.watchlistName = watchlistName;
	}

	public float getPercent() {
		return percent;
	}

	public String getclause() {
		return clause;
	}

	public String getDerogId() {
		return derogId;
	}

	
	public String getWatchlistName() {
		return watchlistName;
	}

	@Override
	public String toString() {
		return "DerogHit [derogId=" + derogId + ", clause=" + clause + ", percent=" + percent + "]";
	}
}
