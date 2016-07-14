/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.User;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.services.security.UserService;
import gov.gtas.services.watchlist.WatchlistPersistenceService;
import gov.gtas.test.util.WatchlistDataGenUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Persistence layer tests for Watch list.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
		CachingConfig.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class WatchlistPersistenceServiceIT {
	private static final String TEST_WL_NAME = "Foo Knowledge Base";
	private static final EntityEnum TEST_WL_ENTITY = EntityEnum.PASSENGER;
	private static final String[] TEST_WL_ITEMS1 = new String[] {
			"Test Item 1", "Test Item 2", "Test Item 3", "Test Item 4" };
	private static final String[] TEST_WL_ITEMS2 = new String[] {
			"Test Item 5", "Test Item 6" };

	@Autowired
	private WatchlistPersistenceService testTarget;
	@Autowired
	private UserService userService;

	private WatchlistDataGenUtils testGenUtils;

	@Before
	public void setUp() throws Exception {
		testGenUtils = new WatchlistDataGenUtils(userService);
		testGenUtils.initUserData();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Transactional
	@Test()
	public void testCreateWatchlist() {
		List<WatchlistItem> createList = testGenUtils
				.createWatchlistItems(TEST_WL_ITEMS1);
		List<Long> idList = testTarget.createUpdateDelete(TEST_WL_NAME,
				TEST_WL_ENTITY, createList, null,
				WatchlistDataGenUtils.TEST_USER1_ID);
		assertNotNull(idList);
		assertEquals(5, idList.size());
		Watchlist wl = testTarget.findByName(TEST_WL_NAME);
		assertNotNull(wl.getEditTimestamp());
		assertEquals(TEST_WL_ENTITY, wl.getWatchlistEntity());
		User editor = wl.getWatchListEditor();
		assertEquals(WatchlistDataGenUtils.TEST_USER1_ID, editor.getUserId());
		List<WatchlistItem> items = testTarget.findWatchlistItems(wl
				.getWatchlistName());
		assertNotNull(items);
		assertEquals(4, items.size());
		for (WatchlistItem item : items) {
			assertNotNull(item.getId());
			assertNotNull(item.getItemData());
			assertNull(item.getItemRuleData());
			assertNotNull(item.getWatchlist());
		}
	}

	@Transactional
	@Test()
	public void testUpdateDeleteWatchlist() {
		List<WatchlistItem> createList = testGenUtils
				.createWatchlistItems(TEST_WL_ITEMS1);
		List<WatchlistItem> addList = testGenUtils
				.createWatchlistItems(TEST_WL_ITEMS2);
		List<Long> idList = testTarget.createUpdateDelete(TEST_WL_NAME,
				TEST_WL_ENTITY, createList, null,
				WatchlistDataGenUtils.TEST_USER1_ID);
		assertNotNull(idList);
		assertEquals(5, idList.size());// wl id + 4 inserted item ids
		List<WatchlistItem> items = testTarget.findWatchlistItems(TEST_WL_NAME);
		assertEquals(4, items.size());
		Map<String, Long> jsonMap = new HashMap<>();

		// delete item 1
		List<WatchlistItem> deleteItems = new LinkedList<WatchlistItem>();
		deleteItems.add(items.get(0));

		// update items 2,3,4
		List<WatchlistItem> updateItems = new LinkedList<WatchlistItem>();
		for (int i = 1; i < items.size(); i++) {
			WatchlistItem item = items.get(i);
			String update = item.getItemData() + "-update";
			item.setItemData(update);
			updateItems.add(item);
			jsonMap.put(update, item.getId());
		}
		// insert two more items
		updateItems.addAll(addList);

		testTarget.createUpdateDelete(TEST_WL_NAME, TEST_WL_ENTITY,
				updateItems, deleteItems, WatchlistDataGenUtils.TEST_USER1_ID);
		items = testTarget.findWatchlistItems(TEST_WL_NAME);
		assertNotNull(items);
		assertEquals(5, items.size());
		int updateCount = 0;
		for (WatchlistItem item : items) {
			Long id = item.getId();
			assertNotNull(id);
			assertNotNull(item.getItemData());
			assertNull(item.getItemRuleData());
			assertNotNull(item.getWatchlist());
			// check updates
			Long upd = jsonMap.get(item.getItemData());
			if (upd != null) {
				++updateCount;
			}
		}
		assertEquals(3, updateCount);
	}

	@Transactional
	@Test()
	public void testDeleteWatchlist() {
		List<WatchlistItem> createList = testGenUtils
				.createWatchlistItems(TEST_WL_ITEMS1);
		testTarget.createUpdateDelete(TEST_WL_NAME, TEST_WL_ENTITY, createList,
				null, WatchlistDataGenUtils.TEST_USER1_ID);
		Watchlist wl = testTarget.findByName(TEST_WL_NAME);
		assertNotNull(wl);
		List<WatchlistItem> items = testTarget.findWatchlistItems(wl
				.getWatchlistName());
		assertEquals(4, items.size());
		List<WatchlistItem> deleteItems = new LinkedList<WatchlistItem>();
		String deldata1 = addDeleteItem(deleteItems, items.get(0));
		String deldata2 = addDeleteItem(deleteItems, items.get(3));
		testTarget.createUpdateDelete(wl.getWatchlistName(),
				wl.getWatchlistEntity(), null, deleteItems,
				WatchlistDataGenUtils.TEST_USER1_ID);
		items = testTarget.findWatchlistItems(wl.getWatchlistName());
		assertNotNull(items);
		assertEquals(2, items.size());
		WatchlistItem item = items.get(0);
		assertTrue(!item.getItemData().equalsIgnoreCase(deldata1)
				|| !item.getItemData().equalsIgnoreCase(deldata2));
		item = items.get(1);
		assertTrue(!item.getItemData().equalsIgnoreCase(deldata1)
				|| !item.getItemData().equalsIgnoreCase(deldata2));
	}

	@Transactional
	@Test()
	public void testWatchlistSummary() {
		List<WatchlistItem> createList = testGenUtils
				.createWatchlistItems(TEST_WL_ITEMS1);
		testTarget.createUpdateDelete(TEST_WL_NAME, TEST_WL_ENTITY, createList,
				null, WatchlistDataGenUtils.TEST_USER1_ID);
		Watchlist wl = testTarget.findByName(TEST_WL_NAME);
		assertNotNull(wl);
		List<Watchlist> summaryList = testTarget.findAllSummary();
		assertTrue(summaryList.size() >= 1);// there may be existing Watch lists
		int matchcount = 0;
		for (Watchlist watchlist : summaryList) {
			if (TEST_WL_NAME.equals(watchlist.getWatchlistName())) {
				assertEquals(TEST_WL_ENTITY, watchlist.getWatchlistEntity());
				++matchcount;
			}
		}
		assertEquals(1, matchcount);
	}

	private String addDeleteItem(List<WatchlistItem> delList, WatchlistItem item) {
		String deldata = item.getItemData();
		WatchlistItem delItem = new WatchlistItem();
		delItem.setId(item.getId());
		delList.add(delItem);
		return deldata;
	}
};
