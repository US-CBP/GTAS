/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.services.Filter.FilterData;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.RoleService;
import gov.gtas.services.security.RoleServiceUtil;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
		CachingConfig.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserServiceIT {

	private static final String USER_ID = "test";
	private static final String FIRST_NAME = "Integration";
	private static final String LAST_NAME = "test";
	private static final String PASSWORD = "$2a$10$0rGc.QzA0MH7MM7OXqynJ.2Cnbdf9PiNk4ffi4ih6LSW3y21OkspG";
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final DateFormat dateFormat = new SimpleDateFormat(
			DATE_FORMAT);

	@Autowired
	UserService userService;

	@Autowired
	UserServiceUtil userServiceUtil;

	@Autowired
	RoleService roleService;

	@Autowired
	RoleServiceUtil roleServiceUtil;

	Set<RoleData> roles;

	@Before
	public void setUp() throws Exception {
		roles = roleService.findAll();

	}

	@Test
	public void testGetAllUser() {
		List<UserData> users = userService.findAll();
		assertNotNull(users);
		users.forEach(r -> r.getRoles().forEach(
				role -> System.out.println(role.getRoleDescription())));
	}

	@Test
	public void testGetSpecifUser() {
		UserData user = userService.findById("test");
		assertNotNull(user);
		user.getRoles()
				.forEach(r -> System.out.println(r.getRoleDescription()));
	}

	@Test
	@Transactional
	public void testCreateUserWithRoles() {
		// Arrange

		Stream<RoleData> streamRoles = roles.stream().filter(
				r -> r.getRoleId() == 2);
		Set<RoleData> authRoles = streamRoles.collect(Collectors.toSet());

		System.out.println(authRoles);
		UserData expectedUser = new UserData("iTest99", PASSWORD, "test", "99",
				1, authRoles, null);

		UserData actualUser = null;
		// Act
		try {
			actualUser = userService.create(expectedUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Assert
		assertEquals(expectedUser.getUserId(), actualUser.getUserId());
	}

	@Test
	@Transactional
	public void testCreateUserWithRolesAndFilter() {
		// Arrange

		Stream<RoleData> streamRoles = roles.stream().filter(
				r -> r.getRoleId() == 2 || r.getRoleId() == 5);
		Set<RoleData> authRoles = streamRoles.collect(Collectors.toSet());

		Set<String> originAirports = new HashSet<String>();

		originAirports.add("HFN");
		originAirports.add("HZK");
		originAirports.add("IFJ");

		Set<String> destinationAirports = new HashSet<String>();
		destinationAirports.add("KEF");
		destinationAirports.add("PFJ");
		destinationAirports.add("RKV");
		int etaStart = -2;
		int etaEnd = 2;

		FilterData filter = new FilterData("iTest99", "I", originAirports,
				destinationAirports, etaStart, etaEnd);

		UserData expectedUser = new UserData("iTest99", PASSWORD, "test", "99",
				1, authRoles, filter);

		UserData actualUser = null;
		// Act
		try {
			actualUser = userService.create(expectedUser);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Assert
		assertEquals(expectedUser.getUserId(), actualUser.getUserId());
		assertEquals(expectedUser.getFilter(), actualUser.getFilter());
	}

	@Test
	@Transactional
	public void testUpdateUserWithOutFilters() {

		// Arrange
		Stream<RoleData> streamRoles = roles.stream().filter(
				r -> r.getRoleId() == 2);
		Set<RoleData> authRoles = streamRoles.collect(Collectors.toSet());

		System.out.println(authRoles);
		UserData expectedUser = new UserData("iTest99", PASSWORD, "test", "99",
				1, authRoles, null);

		UserData actualUser = null;
		try {
			actualUser = userService.create(expectedUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// update lastname
		UserData expectedUserU = new UserData("iTest99", PASSWORD, "test",
				"100", 1, authRoles, null);

		UserData actualUserU = null;
		// Act
		try {
			actualUserU = userService.update(expectedUserU);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Assert
		assertEquals(expectedUserU.getLastName(), actualUserU.getLastName());
		assertEquals(expectedUserU.getRoles(), actualUserU.getRoles());
	}

	@Test
	@Transactional
	public void testUpdateUserWithFilters() {
		// Arrange

		Stream<RoleData> streamRoles = roles.stream().filter(
				r -> r.getRoleId() == 2 || r.getRoleId() == 5);
		Set<RoleData> authRoles = streamRoles.collect(Collectors.toSet());

		Set<String> originAirports = new HashSet<String>();
		originAirports.add("HFN");

		Set<String> destinationAirports = new HashSet<String>();
		destinationAirports.add("KEF");

		int etaStart = -2;
		int etaEnd = 2;

		FilterData filter = new FilterData("iTest99", "I", originAirports,
				destinationAirports, etaStart, etaEnd);

		UserData expectedUser = new UserData("iTest99", PASSWORD, "test", "99",
				1, authRoles, filter);

		UserData actualUser = null;
		// Act
		try {
			actualUser = userService.create(expectedUser);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Stream<RoleData> streamRolesU = roles.stream().filter(
				r -> r.getRoleId() == 2 || r.getRoleId() == 3);
		Set<RoleData> authRolesU = streamRolesU.collect(Collectors.toSet());
		originAirports.add("GKA");

		destinationAirports.add("LAE");
		etaStart = -3;
		etaEnd = 3;

		FilterData filterU = new FilterData("iTest99", "O", originAirports,
				destinationAirports, etaStart, etaEnd);

		System.out.println(authRoles);
		UserData expectedUserU = new UserData("iTest99", PASSWORD, "test",
				"99", 1, authRolesU, filterU);

		UserData actualUserU = null;
		// Act
		try {
			actualUserU = userService.update(expectedUserU);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Assert
		assertEquals(expectedUserU.getFilter(), actualUserU.getFilter());
		assertEquals(expectedUserU.getRoles(), actualUserU.getRoles());
	}

}
