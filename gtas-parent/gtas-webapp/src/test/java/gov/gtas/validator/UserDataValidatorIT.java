/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import gov.gtas.common.WebAppConfig;
import gov.gtas.controller.config.TestMvcRestServiceWebConfig;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestMvcRestServiceWebConfig.class, WebAppConfig.class })
@WebAppConfiguration
public class UserDataValidatorIT {

    private static final String USER_ID = "vtammineni";
    private static final String PASSWORD = "passowrd";
    private static final String FIRST_NAME = "venu";
    private static final String LAST_NAME = "tammineni";
    private static final int ACTIVE = 1;
    private static final int ROLE_ID = 7;
    private static final String ROLE_DESCRIPTION = "ADMIN";
    private static final String TEST_OBJECT_NAME = "testUserData";

    private UserDataValidator userDataValidator;

    @Before
    public void setUp() {
        userDataValidator = new UserDataValidator();
    }

    @Test
    public void testSupports() {
        assertTrue(userDataValidator.supports(UserData.class));
    }

    /**
     * empty request test case
     */
    @Test
    public void testEmptyRequestFields() {
        Set<RoleData> roles=new HashSet<RoleData>();
        
        roles.add(new RoleData(ROLE_ID,ROLE_DESCRIPTION));      
        UserData testUserData = new UserData(null,PASSWORD,FIRST_NAME,LAST_NAME,ACTIVE, roles,null);

        Errors errors = new BeanPropertyBindingResult(testUserData, TEST_OBJECT_NAME);
        userDataValidator.validate(testUserData, errors);
        assertTrue("Should have errors", errors.hasErrors());
    }
    
    /**
     * valid input request test case.
     */
    @Test
    public void testValidRequest() {
        Set<RoleData> roles=new HashSet<RoleData>();
        
        roles.add(new RoleData(ROLE_ID,ROLE_DESCRIPTION));      
        UserData testUserData = new UserData(FIRST_NAME,PASSWORD,FIRST_NAME,LAST_NAME,ACTIVE, roles,null);

        Errors errors = new BeanPropertyBindingResult(testUserData, TEST_OBJECT_NAME);
        userDataValidator.validate(testUserData, errors);
        assertFalse("valid request", errors.hasErrors());
    }
}
