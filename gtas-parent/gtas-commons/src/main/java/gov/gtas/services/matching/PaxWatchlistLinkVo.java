/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matching;

import java.util.Date;
import gov.gtas.model.Passenger;
import gov.gtas.model.watchlist.WatchlistItem;

public class PaxWatchlistLinkVo {

	    private float percentMatch;
	    private Date lastRunTimestamp;
	    private int verifiedStatus;
	    private Long watchlistItemId;
	    private Long passengerId;
	    private String watchListFirstName;
	    private String watchListLastName;
	    private String watchListDOB;
	    private String watchlistCategory;

		public PaxWatchlistLinkVo(float percentMatch, Date lastRunTimestamp, int verifiedStatus, Long watchlistItemId,
				Long passengerId) {
			super();
			this.percentMatch = percentMatch;
			this.lastRunTimestamp = lastRunTimestamp;
			this.verifiedStatus = verifiedStatus;
			this.watchlistItemId = watchlistItemId;
			this.passengerId = passengerId;
		}
		public PaxWatchlistLinkVo(float percentMatch, Date lastRunTimestamp, int verifiedStatus,
				Long passengerId, Long watchlistItemId, String watchListFirstName, String watchListLastName, String watchListDOB,
		String watchlistCategory) {
			super();
			this.percentMatch = percentMatch;
			this.lastRunTimestamp = lastRunTimestamp;
			this.verifiedStatus = verifiedStatus;
			this.watchlistItemId = watchlistItemId;
			this.passengerId = passengerId;
			this.watchListFirstName = watchListFirstName;
			this.watchListLastName = watchListLastName;
			this.watchListDOB = watchListDOB;
			this.watchlistCategory = watchlistCategory;
		}

		public String getWatchListFirstName() {
			return watchListFirstName;
		}
		public void setWatchListFirstName(String watchListFirstName) {
			this.watchListFirstName = watchListFirstName;
		}
		public String getWatchListLastName() {
			return watchListLastName;
		}
		public void setWatchListLastName(String watchListLastName) {
			this.watchListLastName = watchListLastName;
		}
		public String getWatchListDOB() {
			return watchListDOB;
		}
		public void setWatchListDOB(String watchListDOB) {
			this.watchListDOB = watchListDOB;
		}
		public float getPercentMatch() {
			return percentMatch;
		}

		public void setPercentMatch(float percentMatch) {
			this.percentMatch = percentMatch;
		}

		public Date getLastRunTimestamp() {
			return lastRunTimestamp;
		}

		public void setLastRunTimestamp(Date lastRunTimestamp) {
			this.lastRunTimestamp = lastRunTimestamp;
		}

		public Long getWatchlistItemId() {
			return watchlistItemId;
		}

		public void setWatchlistItemId(Long watchlistItemId) {
			this.watchlistItemId = watchlistItemId;
		}

		public Long getPassengerId() {
			return passengerId;
		}

		public void setPassengerId(Long passengerId) {
			this.passengerId = passengerId;
		}

		public int getVerifiedStatus() {
			return verifiedStatus;
		}

		public void setVerifiedStatus(int verifiedStatus) {
			this.verifiedStatus = verifiedStatus;
		}

	public String getWatchlistCategory() {
		return watchlistCategory;
	}

	public void setWatchlistCategory(String watchlistCategory) {
		this.watchlistCategory = watchlistCategory;
	}
}
