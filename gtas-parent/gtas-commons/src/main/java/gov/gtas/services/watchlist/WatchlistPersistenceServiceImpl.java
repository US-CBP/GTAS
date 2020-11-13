/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.watchlist;

import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_CREATE_MESSAGE;
import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_DELETE_ALL_MESSAGE;
import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_DELETE_MESSAGE;
//import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_TARGET_PREFIX;
//import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_TARGET_SUFFIX;
import static gov.gtas.constant.AuditLogConstants.WATCHLIST_LOG_UPDATE_MESSAGE;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.GeneralAuditRecord;
import gov.gtas.model.User;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.repository.AuditRecordRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.HitCategoryService;
import gov.gtas.services.security.UserService;
import gov.gtas.util.DateCalendarUtils;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * The back-end service for persisting watch lists.
 */
@Service
public class WatchlistPersistenceServiceImpl implements WatchlistPersistenceService {
	private static final Logger logger = LoggerFactory.getLogger(WatchlistPersistenceServiceImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Resource
	private WatchlistRepository watchlistRepository;

	@Resource
	private WatchlistItemRepository watchlistItemRepository;

	@Resource
	private AuditRecordRepository auditRecordRepository;

	@Autowired
	private UserService userService;

	@Resource
	private HitCategoryService hitCategoryService;

	@Override
	@Transactional
	public List<Long> createUpdateDelete(String wlName, EntityEnum entity, List<WatchlistItem> createUpdateList,
			List<WatchlistItem> deleteList, String userId, Long catId) {
		final User user = userService.fetchUser(userId);
		Watchlist watchlist = watchlistRepository.getWatchlistByName(wlName);
		if (watchlist == null) {
			watchlist = new Watchlist(wlName, entity);
		} else if (entity != watchlist.getWatchlistEntity()) {
			// existing watch list has a different entity than that specified in
			// the create/update operation
			ErrorHandlerFactory.createAndThrowException(CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE, "entity",
					"Update Watchlist");
		}
		// set the audit fields
		watchlist.setEditTimestamp(new Date());
		watchlist.setWatchListEditor(user);
		watchlist = watchlistRepository.save(watchlist);
		List<Long> ret = new LinkedList<>();
		ret.add(watchlist.getId());
		if (CollectionUtils.isEmpty(createUpdateList)) {
			doDeleteWithLogging(watchlist, user, deleteList);
		} else if (CollectionUtils.isEmpty(deleteList)) {
			Collection<Long> createUpdateIds = doCreateUpdateWithLogging(watchlist, user, createUpdateList, catId);
			ret.addAll(createUpdateIds);
		} else {
			Collection<Long> createUpdateIds = doCreateUpdateWithLogging(watchlist, user, createUpdateList, catId);
			doDeleteWithLogging(watchlist, user, deleteList);
			ret.addAll(createUpdateIds);
		}
		return ret;
	}

	@Override
	public List<WatchlistItem> findWatchlistItems(String watchlistName) {
		return watchlistItemRepository.getItemsByWatchlistName(watchlistName);
	}

	@Override
	public Iterable<WatchlistItem> findAllWatchlistItems() {
		return watchlistItemRepository.findAll();
	}

	@Override
	public List<Watchlist> findAllSummary() {
		List<Object[]> summaryList = watchlistRepository.fetchWatchlistSummary();
		List<Watchlist> ret = new LinkedList<>();
		for (Object[] line : summaryList) {
			ret.add(new Watchlist(line[0].toString(), (EntityEnum) line[1]));
		}
		return ret;
	}

	@Override
	public Watchlist findByName(String name) {
		return watchlistRepository.getWatchlistByName(name);
	}

	@Override
	@Transactional
	public Watchlist deleteWatchlist(String name, boolean forceFlag, String userId) {
		final User user = userService.fetchUser(userId);
		Watchlist wl = watchlistRepository.getWatchlistByName(name);
		if (wl != null) {
			List<WatchlistItem> childItems = watchlistItemRepository.getItemsByWatchlistName(name);
			if (!CollectionUtils.isEmpty(childItems) && forceFlag) {
				watchlistItemRepository.deleteAll(childItems);
				watchlistRepository.delete(wl);
			} else if (CollectionUtils.isEmpty(childItems)) {
				watchlistRepository.delete(wl);
			} else {
				throw ErrorHandlerFactory.getErrorHandler()
						.createException(WatchlistConstants.CANNOT_DELETE_NONEMPTY_WATCHLIST_ERROR_CODE, name);
			}
			// write the audit record
			auditRecordRepository.save(createAuditLogRecord(AuditActionType.DELETE_ALL_WL, wl, null,
					WATCHLIST_LOG_DELETE_ALL_MESSAGE, user));
		} else {
			logger.warn(
					"WatchlistPersistenceServiceImpl.deleteWatchlist - cannot delete watchlist since it does not exist:"
							+ name);
		}
		return wl;
	}

	@Override
	public List<AuditRecord> findLogEntriesForWatchlist(String watchlistName) {
		AuditActionTarget target = new AuditActionTarget(AuditActionType.CREATE_WL, watchlistName, null);
		return auditRecordRepository.findByTarget(target.toString());
	}

	private void doDeleteWithLogging(Watchlist watchlist, User editUser, Collection<WatchlistItem> deleteItems) {
		if (!CollectionUtils.isEmpty(deleteItems)) {
			List<WatchlistItem> hydratedDeleteItems = new ArrayList<>();
			List<AuditRecord> logRecords = new LinkedList<>();
			Map<Long, WatchlistItem> updateDeleteItemMap = validateItemsPresentInDb(deleteItems);
			for (WatchlistItem item : deleteItems) {
				WatchlistItem itemToDelete = updateDeleteItemMap.get(item.getId());
				logRecords.add(createAuditLogRecord(AuditActionType.DELETE_WL, watchlist, itemToDelete,
						WATCHLIST_LOG_DELETE_MESSAGE, editUser));
				hydratedDeleteItems.add(itemToDelete);
			}
			watchlistItemRepository.deleteAll(hydratedDeleteItems);
			auditRecordRepository.saveAll(logRecords);
		}
	}

	private Collection<Long> doCreateUpdateWithLogging(Watchlist watchlist, User editUser,
			Collection<WatchlistItem> createUpdateItems, Long catId) {
		final List<Long> ret = new LinkedList<>();
		HitCategory hc = hitCategoryService.findById(catId);
		List<AuditRecord> logRecords = new LinkedList<>();
		if (createUpdateItems != null && !createUpdateItems.isEmpty()) {
			List<WatchlistItem> updList = new LinkedList<>();
			for (WatchlistItem item : createUpdateItems) {
				if (item.getId() != null) {
					logRecords.add(createAuditLogRecord(AuditActionType.UPDATE_WL, watchlist, item,
							WATCHLIST_LOG_UPDATE_MESSAGE, editUser));
					item.setHitCategory(hc);
					item.setAuthor(editUser);
					updList.add(item);
				} else {
					item.setAuthor(editUser);
					item.setHitCategory(hc);
					logRecords.add(createAuditLogRecord(AuditActionType.CREATE_WL, watchlist, item,
							WATCHLIST_LOG_CREATE_MESSAGE, editUser));
				}
				item.setWatchlist(watchlist);
			}
			validateItemsPresentInDb(updList);
			Iterable<WatchlistItem> savedItems = watchlistItemRepository.saveAll(createUpdateItems);
			auditRecordRepository.saveAll(logRecords);
			savedItems.forEach(item -> ret.add(item.getId()));
		}
		return ret;
	}

	private AuditRecord createAuditLogRecord(AuditActionType type, Watchlist watchlist, WatchlistItem item,
			String message, User editUser) {
		AuditActionTarget target = new AuditActionTarget(type, watchlist.getWatchlistName(), null);
		AuditActionData actionData = new AuditActionData();
		if (item != null) {
			actionData.addProperty("itemId", item.getId() != null ? String.valueOf(item.getId()) : StringUtils.EMPTY);
		}
		actionData.addProperty("user", editUser.getUserId());
		actionData.addProperty("editDate",
				watchlist.getEditTimestamp() != null ? DateCalendarUtils.formatJsonDate(watchlist.getEditTimestamp())
						: StringUtils.EMPTY);
		return new GeneralAuditRecord(type, target.toString(), Status.SUCCESS, message, actionData.toString(), editUser);
	}

	private Map<Long, WatchlistItem> validateItemsPresentInDb(Collection<WatchlistItem> targetItems) {
		Map<Long, WatchlistItem> ret = new HashMap<>();
		if (targetItems != null && !targetItems.isEmpty()) {
			List<Long> lst = targetItems.stream().map(itm -> itm.getId()).collect(Collectors.toList());
			Iterable<WatchlistItem> items = watchlistItemRepository.findAllById(lst);

			int itemCount = 0;
			for (WatchlistItem itm : items) {
				ret.put(itm.getId(), itm);
				++itemCount;
			}
			if (targetItems.size() != itemCount) {
				handleMissingWlItemError(targetItems, ret.keySet());
			}
		}
		return ret;
	}

	private void handleMissingWlItemError(Collection<WatchlistItem> targetItems, Set<Long> foundKeys) {
		final StringBuilder bldr = new StringBuilder();
		targetItems.forEach(itm -> bldr.append(itm.getId()).append(','));
		String targets = bldr.substring(0, bldr.length() - 1);
		String found = StringUtils.EMPTY;
		if (!CollectionUtils.isEmpty(foundKeys)) {
			final StringBuilder bldr2 = new StringBuilder();
			foundKeys.forEach(itm -> bldr2.append(itm).append(','));
			found = bldr2.substring(0, bldr2.length() - 1);
		}

		ErrorHandlerFactory.createAndThrowException(WatchlistConstants.MISSING_DELETE_OR_UPDATE_ITEM_ERROR_CODE,
				targets, found);

	}

	public List<HitCategory> findWatchlistCategories() {
		return this.watchlistRepository.getWatchlistCategories();
	}

	public WatchlistItem updateWatchlistItemCategory(WatchlistItem watchlistItem) {
		return this.watchlistItemRepository.save(watchlistItem);
	}

	@Override
	public HitCategory fetchWatchlistCategoryById(Long categoryID) {
		//
		return this.watchlistRepository.getWatchlistCategoryById(categoryID);
	}

	@Override
	public WatchlistItem findWatchlistItemById(Long watchlistItemId) {
		//
		return this.watchlistItemRepository.findById(watchlistItemId).orElse(null);
	}

	@Override
	public List<WatchlistItem> findItemsByWatchlistName(String watchlistName) {
		//
		return this.watchlistItemRepository.getItemsByWatchlistName(watchlistName);
	}

	@Override
	public void deleteWatchlistItems(List<Long> watchlistItemIds) {
		Iterable<WatchlistItem> watchlistItemList = watchlistItemRepository.findAllById(watchlistItemIds);
		this.watchlistItemRepository.deleteAll(watchlistItemList);
	}

}
