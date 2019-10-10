/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST;

import gov.gtas.json.JsonLookupData;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.watchlist.WatchlistItem;
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
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
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
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
	JsonServiceResponse createUpdateDeleteWatchlistItems(String userId, WatchlistSpec wlToCreateUpdateDelete, Long categoryId );

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
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
	JsonServiceResponse createUpdateWatchlistItems(String userId, WatchlistSpec wlToCreateUpdate);

	/**
	 * Fetches all watch lists
	 * 
	 * @return the list of all available watch list objects.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
	List<WatchlistSpec> fetchAllWatchlists();

	/**
	 * Compiles all watch lists into a named knowledge base
	 * 
	 * @param knowledgeBaseName
	 *            name of the knowledge base to compile the watch list rules into.
	 * @return the list of all available watch list objects.
	 */
	JsonServiceResponse activateAllWatchlists(String knowledgeBaseName);

	/**
	 * Compiles all watch lists into the default knowledge base for watch lists.
	 * 
	 * @return the list of all available watch list objects.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
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
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
	JsonServiceResponse deleteWatchlist(String userId, String wlName);

	public List<JsonLookupData> findWatchlistCategories();

	public void updateWatchlistItemCategory(Long categoryID, Long watchlistItemId);

	public WatchlistItem fetchWatchlistItemById(Long watchlistItemId);

	public HitCategory fetchWatchlistCategoryById(Long categoryID);

	List<WatchlistItem> fetchItemsByWatchlistName(String watchlistName);

}
