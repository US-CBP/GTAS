/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.watchlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.lookup.WatchlistCategory;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.repository.watchlist.WatchlistItemRepository;

@Service
public class WatchlistCatServiceImpl implements WatchlistCatService {

	@Autowired
	private WatchlistItemRepository watchlistItemRepository;

	@Override
	public WatchlistCategory findCatByWatchlistItemId(Long watchlistItemId) {
		//
		WatchlistItem item = this.watchlistItemRepository.findOne(watchlistItemId);
		if (item == null) {
			return null;
		}
		return item.getWatchlistCategory();
	}

}
