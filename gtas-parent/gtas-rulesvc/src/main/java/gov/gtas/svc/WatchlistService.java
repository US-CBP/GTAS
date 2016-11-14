/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.watchlist.json.WatchlistSpec;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The service interface for managing User Defined Rules (UDR).<br>
 * 1. CRUD on UDR.<br>
 * 2. Generation of Drools Rules and creation of versioned Knowledge Base.
 */
public interface WatchlistService {
	/**
	 * Retrieves the UDR domain object from the DB and converts it to the
	 * corresponding JSON object.
	 * 
	 * @param wlName
	 *            the name of the watch list.
	 * @return the Watch list object.
	 */
	@PreAuthorize("hasAnyAuthority('Admin', 'Manage Watch List')")
	WatchlistSpec fetchWatchlist(String wlName);

	/**
	 * Creates/Updates/Deletes watchlist items from the specified watchlist.
	 * 
	 * @param userId
	 *            the userId of the author.
	 * @param wlToCreateUpdateDelete
	 *            the JSON Watchlist object with items to be
	 *            inserted/updated/deleted.
	 * @return the service response JSON format.
	 */
	@PreAuthorize("hasAnyAuthority('Admin', 'Manage Watch List')")
	JsonServiceResponse createUpdateDeleteWatchlistItems(String userId,
			WatchlistSpec wlToCreateUpdateDelete);

	/**
	 * Creates/Updates watch list items from the specified watch list.
	 * 
	 * @param userId
	 *            the userId of the author.
	 * @param wlToCreateUpdate
	 *            the JSON Watch list object with items to be
	 *            inserted/updated/deleted.
	 * @return the service response JSON format.
	 */
	@PreAuthorize("hasAuthority('Admin')")
	JsonServiceResponse createUpdateWatchlistItems(String userId,
			WatchlistSpec wlToCreateUpdate);

	/**
	 * Fetches all watch lists
	 * 
	 * @return the list of all available watch list objects.
	 */
	@PreAuthorize("hasAnyAuthority('Admin', 'Manage Watch List')")
	List<WatchlistSpec> fetchAllWatchlists();

	/**
	 * Compiles all watch lists into a named knowledge base
	 * 
	 * @param knowledgeBaseName
	 *            name of the knowledge base to compile the watch list rules
	 *            into.
	 * @return the list of all available watch list objects.
	 */
	JsonServiceResponse activateAllWatchlists(String knowledgeBaseName);

	/**
	 * Compiles all watch lists into the default knowledge base for watch lists.
	 * 
	 * @return the list of all available watch list objects.
	 */
	@PreAuthorize("hasAnyAuthority('Admin', 'Manage Watch List')")
	JsonServiceResponse activateAllWatchlists();

	/**
	 * Deletes all the items in the named watch list and then deletes the watch
	 * list.
	 * 
	 * @param userId
	 *            the userId of the person requesting the delete.
	 * @param wlName
	 *            the name of the watch list to be deleted.
	 * @return the delete result.
	 */
	@PreAuthorize("hasAnyAuthority('Admin', 'Manage Watch List')")
	JsonServiceResponse deleteWatchlist(String userId, String wlName);
}
