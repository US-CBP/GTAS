/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.watchlist;

import gov.gtas.model.lookup.WatchlistCategory;

public interface WatchlistCatService {

	WatchlistCategory findCatByWatchlistItemId(Long catId);
	
}
