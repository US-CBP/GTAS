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

		public PaxWatchlistLinkVo(float percentMatch, Date lastRunTimestamp, int verifiedStatus, Long watchlistItemId,
				Long passengerId) {
			super();
			this.percentMatch = percentMatch;
			this.lastRunTimestamp = lastRunTimestamp;
			this.verifiedStatus = verifiedStatus;
			this.watchlistItemId = watchlistItemId;
			this.passengerId = passengerId;
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
}
