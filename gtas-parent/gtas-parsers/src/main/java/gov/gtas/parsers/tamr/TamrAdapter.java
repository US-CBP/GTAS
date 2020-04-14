/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.parsers.tamr;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.parsers.tamr.model.TamrDerogListEntry;
import gov.gtas.parsers.tamr.model.TamrPassenger;

public interface TamrAdapter {
	List<TamrPassenger> convertPassengers(Flight flight, Set<Passenger> passengers);
	
	List<TamrDerogListEntry> convertWatchlist(Collection<WatchlistItem> watchlistItems);
}
