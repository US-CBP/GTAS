/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.watchlist;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The Persistence Layer service for Watch lists.
 */
public interface WatchlistPersistenceService {
	/**
	 * Creates or Updates a Watch List.
	 * 
	 * @param wlName
	 *            the name of the watch list object to persist in the DB.
	 * @param entity
	 *            the entity (e.g., PASSENGER) for the watch list.
	 * @param createUpdateList
	 *            the list of watch list items to be added or updated.
	 * @param deleteList
	 *            the list of watch list items to be deleted.
	 * @param userId
	 *            the id of the user persisting the rule (usually also the WL
	 *            author.)
	 * @return the id's of the watch list and the updated items.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
	public List<Long> createUpdateDelete(String wlName, EntityEnum entity, List<WatchlistItem> createUpdateList,
			List<WatchlistItem> deleteList, String userId, Long catId);

	/**
	 * Find and return the list of all watch lists.
	 *
	 * @return list of all watch lists.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
	public List<Watchlist> findAllSummary();

	/**
	 * Find and return the list of all watch list items.
	 * 
	 * @param watchlistName
	 *            the name of the watch list
	 * @return list of all watch list items.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
	public List<WatchlistItem> findWatchlistItems(String watchlistName);

	/**
	 * Find and return the list of all watch list items for all watch lists.
	 * 
	 * @return list of all watch list items.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
	public Iterable<WatchlistItem> findAllWatchlistItems();

	/**
	 * Find and return the list of all non-compiled watch lists. (Either the compile
	 * time stamp is null, or it is before the edit time stamp.)
	 * 
	 * @return list of all non-compiled watch lists.
	 */
	// public List<Watchlist> findUncompiledWatchlists();

	/**
	 * Fetches a Watch list by its name.
	 * 
	 * @param name
	 *            the name of the watch list to fetch.
	 * @return the fetched watch list or null.
	 */
	public Watchlist findByName(String name);

	/**
	 * Deletes a Watch list by its name. (Note: this operation will throw an
	 * exception if the watch list contains items.)
	 * 
	 * @param name
	 *            the name of the watch list to delete.
	 * @param forceFlag
	 *            If forceFlag is true then the watch list with its items will be
	 *            deleted whether it is empty or not. Otherwise the watch list will
	 *            only be deleted if it is empty.
	 * @param userId
	 *            the id of the user requesting the delete.
	 * @return the deleted watch list or null, if the watchlist could not be found.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_WATCH_LIST)
	public Watchlist deleteWatchlist(String name, boolean forceFlag, String userId);

	/**
	 * Fetches all log entries for a Watch list by its name.
	 * 
	 * @param watchlistName
	 *            the name of the watch list.
	 * @return the fetched watch list log entries.
	 */
	public List<AuditRecord> findLogEntriesForWatchlist(String watchlistName);

	public List<HitCategory> findWatchlistCategories();

	WatchlistItem updateWatchlistItemCategory(WatchlistItem watchlistItem);

	HitCategory fetchWatchlistCategoryById(Long categoryID);

	WatchlistItem findWatchlistItemById(Long watchlistItemId);

	List<WatchlistItem> findItemsByWatchlistName(String watchlistName);

}
