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
import gov.gtas.repository.KnowledgeBaseRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.watchlist.WatchlistPersistenceService;
import gov.gtas.svc.util.WatchlistBuilder;
import gov.gtas.svc.util.WatchlistServiceJsonResponseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * The Watch list service implementation.
 */
@Service
public class WatchlistServiceImpl implements WatchlistService {

	private final Logger logger = LoggerFactory.getLogger(WatchlistServiceImpl.class);
	@Autowired
	private WatchlistPersistenceService watchlistPersistenceService;
	@Autowired
	private RuleManagementService ruleManagementService;

	@Autowired
	private WatchlistItemRepository watchlistItemRepository;

	@Autowired
	KnowledgeBaseRepository knowledgeBaseRepository;

	//TODO: Make dynamic
	Integer KB_SIZE = 10000;

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
	public JsonServiceResponse createUpdateDeleteWatchlistItems(String userId, WatchlistSpec wlToCreateUpdate,
			Long catId) {
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
	public void deleteWatchlistItems(List<Long> watchlistItemIds) {
		if (watchlistItemIds != null && !watchlistItemIds.isEmpty()) {
			watchlistPersistenceService.deleteWatchlistItems(watchlistItemIds);
		} else {
			throw new IllegalArgumentException("An empty or null id list was provided.");
		}
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
		KnowledgeBase kb = createAKnowledgeBase(knowledgeBaseName);
		return WatchlistServiceJsonResponseHelper.createKnowledBaseResponse(kb, null);
	}

	@Override
	public KnowledgeBase createAKnowledgeBase(String knowledgeBaseName) {
		Iterable<WatchlistItem> items = watchlistPersistenceService
				.findAllWatchlistItemsByKnowledgeBaseName(knowledgeBaseName);
		KnowledgeBase kb = ruleManagementService.createKnowledgeBaseFromWatchlistItems(knowledgeBaseName, items);
		return kb;
	}

	@Override
	public void rebalanceWatchlist() {
		Pageable pageRequest = PageRequest.of(0, KB_SIZE);
		Page<WatchlistItem> wlItemPage = watchlistPersistenceService.findAllWatchlistItems(pageRequest);
		String base_wl_name = WatchlistConstants.WL_KNOWLEDGE_BASE_NAME;
		int wlKbNumber = 1;

		while (!wlItemPage.isEmpty()) {
			logger.info("Starting wl page");
			String wlName = base_wl_name + "_" + wlKbNumber;
			KnowledgeBase kb = knowledgeBaseRepository.getByName(wlName);
			wlKbNumber++;
			if (kb == null) {
				kb = new KnowledgeBase(wlName);
				kb.setCreationDt(new Date());
				kb = knowledgeBaseRepository.save(kb);
			}

			kb.setCreationDt(new Date());
			List<WatchlistItem> list = new ArrayList<>(wlItemPage.getContent());
			kb.getWatchlistItemsInKb().clear();
			for (WatchlistItem item : list) {
				item.setKnowledgeBase(kb);
				kb.getWatchlistItemsInKb().add(item);
			}
			watchlistItemRepository.saveAll(list);
			if (!kb.getWatchlistItemsInKb().isEmpty()) {
				knowledgeBaseRepository.save(kb);
			}
			pageRequest = pageRequest.next(); // get the next page ready
			wlItemPage = watchlistPersistenceService.findAllWatchlistItems(pageRequest);
			logger.info("Next wl page");

		}
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
		List<JsonLookupData> result = this.watchlistPersistenceService.findWatchlistCategories().stream()
				.map(w -> new JsonLookupData(w.getId(), w.getName(), w.getDescription(), w.getSeverity().toString(), w.isArchived(), w.isPromoteToLookout()))
				.collect(Collectors.toList());

		return result;
	}

	@Override
	public JsonServiceResponse deleteWatchlistCategory(Long categoryId){
		return this.watchlistPersistenceService.deleteWatchlistCategoryById(categoryId);
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

	@Override
	public void rebalanceAndCreateWatchlist() {
		rebalanceWatchlist();
	}
}
