/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.enumtype.WatchlistEditEnum;
import gov.gtas.error.CommonServiceException;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.json.JsonServiceResponse.ServiceResponseDetailAttribute;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.Role;
import gov.gtas.model.User;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;
import gov.gtas.services.watchlist.WatchlistPersistenceService;
import gov.gtas.util.DateCalendarUtils;
import gov.gtas.util.SampleDataGenerator;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;

/**
 * Integration tests for the UDR management service.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuleServiceConfig.class)
@Rollback(true)
public class WatchlistServiceIT {
	private static final String WL_NAME1 = "Hello WL 1";
	private static final String WL_KB_NAME = "Test WL KB";

	@Autowired
	WatchlistService wlService;

	@Autowired
	WatchlistPersistenceService wlPersistenceService;

	@Autowired
	RuleManagementService ruleManagementService;

	@Autowired
	UserService userService;

	@Autowired
	UserServiceUtil userServiceUtil;

	@Test
	@Transactional
	public void testCreateWatchlist() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createUpdateDeleteWatchlistItems(user.getUserId(), spec, 1L);
		assertEquals(Status.SUCCESS, resp.getStatus());
		List<ServiceResponseDetailAttribute> respDetails = resp.getResponseDetails();
		assertEquals(3, respDetails.size());// wl id, wl name, list of
											// inserted/updated ids
		Watchlist wl = wlPersistenceService.findByName(WL_NAME1);
		assertNotNull(wl);
		assertNotNull(wl.getId());
		assertEquals(WL_NAME1, wl.getWatchlistName());
		assertEquals(EntityEnum.PASSENGER, wl.getWatchlistEntity());
		assertEquals(user, wl.getWatchListEditor());
		assertTrue(DateCalendarUtils.dateRoundedEquals(new Date(), wl.getEditTimestamp(), Calendar.HOUR));
		List<WatchlistItem> items = wlPersistenceService.findWatchlistItems(WL_NAME1);
		assertEquals(2, items.size());
		for (WatchlistItem itm : items) {
			assertNotNull(itm.getItemRuleData());
			assertEquals(WL_NAME1, itm.getWatchlist().getWatchlistName());
			String itmData = itm.getItemData();
			assertTrue(!StringUtils.isEmpty(itmData));
			assertTrue(itmData.matches("\\{.*\\}"));
		}

		List<AuditRecord> logs = wlPersistenceService.findLogEntriesForWatchlist(WL_NAME1);
		assertNotNull(logs);
		assertEquals(2, logs.size());
	}

	@Test
	@Transactional
	public void testUpdateDeleteWatchlistItem() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createUpdateDeleteWatchlistItems(user.getUserId(), spec, 1L);
		assertEquals(Status.SUCCESS, resp.getStatus());
		spec = wlService.fetchWatchlist(WL_NAME1);
		assertNotNull(spec);
		List<WatchlistItemSpec> items = spec.getWatchlistItems();
		assertNotNull(items);
		assertEquals(2, items.size());
		items.get(0).setAction(WatchlistEditEnum.U.getOperationName());
		items.get(1).setAction(WatchlistEditEnum.D.getOperationName());

		resp = wlService.createUpdateDeleteWatchlistItems(user.getUserId(), spec, 1L);
		assertEquals(Status.SUCCESS, resp.getStatus());

		List<WatchlistItem> updItems = wlPersistenceService.findWatchlistItems(WL_NAME1);
		assertEquals(1, updItems.size());
		WatchlistItem itm = updItems.get(0);
		assertNotNull(itm.getItemRuleData());
		assertEquals(WL_NAME1, itm.getWatchlist().getWatchlistName());
		String itmData = itm.getItemData();
		assertTrue(!StringUtils.isEmpty(itmData));
		assertTrue(itmData.matches("\\{.*\\}"));

		List<AuditRecord> logs = wlPersistenceService.findLogEntriesForWatchlist(WL_NAME1);
		assertNotNull(logs);
		assertEquals(4, logs.size());
	}

	@Test
	@Transactional
	public void testUpdateWatchlistItemError() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createUpdateDeleteWatchlistItems(user.getUserId(), spec, 1L);
		assertEquals(Status.SUCCESS, resp.getStatus());
		spec = wlService.fetchWatchlist(WL_NAME1);
		assertNotNull(spec);
		List<WatchlistItemSpec> items = spec.getWatchlistItems();
		assertNotNull(items);
		assertEquals(2, items.size());
		items.get(0).setAction(WatchlistEditEnum.U.getOperationName());
		items.get(0).setId(2341L);
		items.get(1).setAction(WatchlistEditEnum.D.getOperationName());
		try {
			wlService.createUpdateDeleteWatchlistItems(user.getUserId(), spec, 1L);
			fail("Expecting exception");
		} catch (CommonServiceException cse) {
			assertEquals(WatchlistConstants.MISSING_DELETE_OR_UPDATE_ITEM_ERROR_CODE, cse.getErrorCode());
		}
	}

	@Test
	@Transactional
	public void testDeleteWatchlistItemError() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createUpdateDeleteWatchlistItems(user.getUserId(), spec, 1L);
		assertEquals(Status.SUCCESS, resp.getStatus());
		spec = wlService.fetchWatchlist(WL_NAME1);
		assertNotNull(spec);
		List<WatchlistItemSpec> items = spec.getWatchlistItems();
		assertNotNull(items);
		assertEquals(2, items.size());
		items.get(0).setAction(WatchlistEditEnum.U.getOperationName());
		items.get(1).setAction(WatchlistEditEnum.D.getOperationName());
		items.get(1).setId(2341L);
		try {
			wlService.createUpdateDeleteWatchlistItems(user.getUserId(), spec, 1L);
			fail("Expecting exception");
		} catch (CommonServiceException cse) {
			assertEquals(WatchlistConstants.MISSING_DELETE_OR_UPDATE_ITEM_ERROR_CODE, cse.getErrorCode());
		}
	}

	@Test
	@Transactional
	public void testKnowledgeBaseForWl() {
		User user = createUser();
		WatchlistSpec spec = SampleDataGenerator.newWlWith2Items(WL_NAME1);
		JsonServiceResponse resp = wlService.createUpdateDeleteWatchlistItems(user.getUserId(), spec, 1L);
		assertEquals(Status.SUCCESS, resp.getStatus());
		resp = wlService.activateAllWatchlists(WL_KB_NAME);
		assertEquals(Status.SUCCESS, resp.getStatus());
		String drl = ruleManagementService.fetchDrlRulesFromKnowledgeBase(WL_KB_NAME);
		assertNotNull(drl);
	}

	private User createUser() {
		String ROLE_NAME = "user";
		String USER_FNAME = "Patrick";
		String USER_LASTNAME = "Henry";
		String USER_ID = "phenry";
		Set<RoleData> roles = new HashSet<RoleData>();
		roles.add(new RoleData(1, "ADMIN"));

		UserData usr = new UserData(USER_ID, "password", USER_FNAME, USER_LASTNAME, 1, roles,"", false, false);
		Role role = new Role();
		role.setRoleDescription(ROLE_NAME);

		User user = userServiceUtil.mapUserEntityFromUserData(userService.create(usr));

		return user;
	}
}
