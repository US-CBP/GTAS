/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import gov.gtas.constant.WatchlistConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.error.ErrorHandler;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.error.WatchlistServiceErrorHandler;
import gov.gtas.json.JsonLookupData;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.validation.WatchlistValidationAdapter;
import gov.gtas.services.watchlist.WatchlistPersistenceService;
import gov.gtas.svc.util.WatchlistBuilder;
import gov.gtas.svc.util.WatchlistServiceJsonResponseHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Watch list service implementation.
 */
@Service
public class WatchlistServiceImpl implements WatchlistService {

	@Autowired
	private WatchlistPersistenceService watchlistPersistenceService;
	@Autowired
	private RuleManagementService ruleManagementService;

	@PostConstruct
	private void initializeErrorHandler() {
		ErrorHandler errorHandler = new WatchlistServiceErrorHandler();
		ErrorHandlerFactory.registerErrorHandler(errorHandler);
	}

	@Override
	public WatchlistSpec fetchWatchlist(String wlName) {
		WatchlistSpec ret = null;
		Watchlist wl = watchlistPersistenceService.findByName(wlName);
		if (wl != null) {
			List<WatchlistItem> items = watchlistPersistenceService.findWatchlistItems(wlName);
			WatchlistBuilder bldr = new WatchlistBuilder(wl, items);
			ret = bldr.buildWatchlistSpec();
		}
		return ret;
	}

	@Override
	public JsonServiceResponse createUpdateDeleteWatchlistItems(String userId, WatchlistSpec wlToCreateUpdate, Long catId) {
		WatchlistValidationAdapter.validateWatchlistSpec(wlToCreateUpdate);
		WatchlistBuilder bldr = new WatchlistBuilder(wlToCreateUpdate);
		bldr.buildPersistenceLists();
		final String wlName = bldr.getName();
		final EntityEnum entity = bldr.getEntity();
		List<WatchlistItem> createUpdateList = bldr.getCreateUpdateList();
		List<WatchlistItem> deleteList = bldr.getDeleteList();
		List<Long> idList = watchlistPersistenceService.createUpdateDelete(wlName, entity, createUpdateList, deleteList,
				userId, catId);
		List<Long> itemIdList = null;
		Long wlId = idList.get(0);
		if (idList.size() > 1) {
			itemIdList = new LinkedList<Long>();
			for (int i = 1; i < idList.size(); i++) {
				itemIdList.add(idList.get(i));
			}
		}
		return WatchlistServiceJsonResponseHelper.createResponse(true, "Create/Update", wlId, wlName, itemIdList,
				StringUtils.EMPTY);
	}

	@Override
	public JsonServiceResponse createUpdateWatchlistItems(String userId, WatchlistSpec wlToCreateUpdate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public List<WatchlistSpec> fetchAllWatchlists() {
		List<Watchlist> summary = watchlistPersistenceService.findAllSummary();
		List<WatchlistSpec> ret = new LinkedList<WatchlistSpec>();
		for (Watchlist wl : summary) {
			ret.add(new WatchlistSpec(wl.getWatchlistName(), wl.getWatchlistEntity().getEntityName()));
		}
		return ret;
	}

	@Override
	@Transactional
	public JsonServiceResponse activateAllWatchlists(String knowledgeBaseName) {
		Iterable<WatchlistItem> items = watchlistPersistenceService.findAllWatchlistItems();
		if (StringUtils.isEmpty(knowledgeBaseName)) {
			knowledgeBaseName = WatchlistConstants.WL_KNOWLEDGE_BASE_NAME;
		}
		KnowledgeBase kb = ruleManagementService.createKnowledgeBaseFromWatchlistItems(knowledgeBaseName, items);
		return WatchlistServiceJsonResponseHelper.createKnowledBaseResponse(kb, null);
	}

	@Override
	@Transactional
	public JsonServiceResponse activateAllWatchlists() {
		return activateAllWatchlists(WatchlistConstants.WL_KNOWLEDGE_BASE_NAME);
	}

	@Override
	public JsonServiceResponse deleteWatchlist(String userId, String wlName) {
		Watchlist wl = watchlistPersistenceService.deleteWatchlist(wlName, true, userId);// force the delete even if the
																							// watch list is not empty.
		if (wl != null) {
			return WatchlistServiceJsonResponseHelper.createResponse(true, WatchlistConstants.DELETE_OP_NAME,
					wl.getId(), wl.getWatchlistName());
		} else {
			return WatchlistServiceJsonResponseHelper.createResponse(false, WatchlistConstants.DELETE_OP_NAME, null,
					null, "since it does not exist or has been deleted previously");
		}
	}

	@Override
	public List<JsonLookupData> findWatchlistCategories() {
		//
		List<JsonLookupData> result = this.watchlistPersistenceService.findWatchlistCategories().stream().map(w -> {
			return new JsonLookupData(w.getId(), w.getName(), w.getDescription());
		}).collect(Collectors.toList());

		return result;
	}

	@Override
	public synchronized void updateWatchlistItemCategory(Long categoryID, Long watchlistItemId) {
		//
		WatchlistItem watchlistItem = this.fetchWatchlistItemById(watchlistItemId);
		watchlistItem.setHitCategory(fetchWatchlistCategoryById(Long.parseLong(categoryID.toString())));
		this.watchlistPersistenceService.updateWatchlistItemCategory(watchlistItem);
	}

	@Override
	public WatchlistItem fetchWatchlistItemById(Long watchlistItemId) {
		//
		return this.watchlistPersistenceService.findWatchlistItemById(watchlistItemId);
	}

	@Override
	public HitCategory fetchWatchlistCategoryById(Long categoryID) {
		//
		return this.watchlistPersistenceService.fetchWatchlistCategoryById(categoryID);
	}

	@Override
	public List<WatchlistItem> fetchItemsByWatchlistName(String watchlistName) {
		// TODO
		return this.watchlistPersistenceService.findItemsByWatchlistName(watchlistName);
	}
}
