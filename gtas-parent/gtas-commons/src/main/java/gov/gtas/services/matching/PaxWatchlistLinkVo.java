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

	    private int percentMatch;
	    private Date lastRunTimestamp;
	    private WatchlistItem watchlistItem;
	    private Passenger passenger;

		public int getPercentMatch() {
			return percentMatch;
		}

		public void setPercentMatch(int percentMatch) {
			this.percentMatch = percentMatch;
		}

		public Date getLastRunTimestamp() {
			return lastRunTimestamp;
		}

		public void setLastRunTimestamp(Date lastRunTimestamp) {
			this.lastRunTimestamp = lastRunTimestamp;
		}

		public WatchlistItem getWatchlistItem() {
			return watchlistItem;
		}

		public void setWatchlistItem(WatchlistItem watchlistItem) {
			this.watchlistItem = watchlistItem;
		}

		public Passenger getPassenger() {
			return passenger;
		}

		public void setPassenger(Passenger passenger) {
			this.passenger = passenger;
		}
	    
}
