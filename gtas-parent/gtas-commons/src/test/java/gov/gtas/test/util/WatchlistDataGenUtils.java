/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.test.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;

/**
 * Generates test data for rules domain objects.
 */
public class WatchlistDataGenUtils {
    public static final String TEST_WATCHLIST_PREFIX = "TestWL";

    public static final int TEST_ROLE1_ID = 1;
    public static final String TEST_ROLE1_DESCRIPTION = "admin";
    public static final String TEST_USER1_ID = "test";

    public static final int TEST_ROLE2_ID = 99;
    public static final String TEST_ROLE2_DESCRIPTION = "readonly";
    public static final String TEST_USER2_ID = "jtang";

    private UserService userService;

    public WatchlistDataGenUtils(UserService usrSvc) {
        this.userService = usrSvc;
    }

    public void initUserData() {
        try {
            Set<RoleData> roles = new HashSet<RoleData>();
            roles.add(new RoleData(1, "ADMIN"));
            UserData userData = new UserData("jJone", "password", "JP", "Jones", 1, roles,null);
            userService.create(userData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<WatchlistItem> createWatchlistItems(String[] jsonArray) {
        List<WatchlistItem> ret = new LinkedList<WatchlistItem>();
        for (String json : jsonArray) {
            WatchlistItem item = new WatchlistItem();
            item.setItemData(json);
            ret.add(item);
        }
        return ret;
    }

}
