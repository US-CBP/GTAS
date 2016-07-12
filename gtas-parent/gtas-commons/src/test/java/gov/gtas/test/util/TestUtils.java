/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.test.util;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandler;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.User;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Miscellaneous utility methods for testing.
 */
public class TestUtils {

    public static void insertAdminUser(UserService userService, String userId,
            String password, String firstName, String lastName) {
            Set<RoleData> roles = new HashSet<RoleData>();
            roles.add(new RoleData(1, "ADMIN"));
            UserData userData = new UserData(userId, password, firstName,
                    lastName, 1, roles,null);

            userService.create(userData);
    }
    /**
     * Fetches the user object and throws an unchecked exception if the user
     * cannot be found.
     * 
     * @param userId
     *            the ID of the user to fetch.
     * @return the user fetched from the DB.
     */
    public static User fetchUser(UserService userService, UserServiceUtil userServiceUtil, final String userId) {
        UserData userData = userService.findById(userId);
        final User user = userServiceUtil.mapUserEntityFromUserData(userData);
        if (user.getUserId() == null) {
            ErrorHandler errorHandler = ErrorHandlerFactory.getErrorHandler();
            throw errorHandler.createException(
                    CommonErrorConstants.INVALID_USER_ID_ERROR_CODE, userId);
        }
        return user;
    }


//  public String generateTestRuleTitle(int ruleIndx) {
//      StringBuilder bldr = new StringBuilder(TEST_RULE_TITLE_PREFIX);
//      bldr.append(ruleIndx).append('.');
//      bldr.append(ThreadLocalRandom.current().nextInt(1, 10));
//
//      return bldr.toString();
//  }

}
