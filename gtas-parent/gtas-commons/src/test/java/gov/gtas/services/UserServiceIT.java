/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.RoleService;
import gov.gtas.services.security.RoleServiceUtil;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserDisplayData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class, CachingConfig.class })
@Rollback(true)
public class UserServiceIT {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceIT.class);

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
	@Transactional
	public void testGetAllUser() {
		List<UserDisplayData> users = userService.findAll();
		assertNotNull(users);
	}

	@Test
	@Transactional
	public void testGetSpecifUser() {
		UserData user = userService.findById("test");
		assertNull(user);
	}

	@Test
	@Transactional
	public void testCreateUserWithRoles() {
		// Arrange

		Stream<RoleData> streamRoles = roles.stream().filter(r -> r.getRoleId() == 2);
		Set<RoleData> authRoles = streamRoles.collect(Collectors.toSet());

		logger.info(authRoles.toString());
		UserData expectedUser = new UserData("iTest99", "password", "test", "99", 1, authRoles, "", false, false, false, "1111111111");

		UserData actualUser = null;
		// Act
		try {
			actualUser = userService.create(expectedUser);
		} catch (Exception e) {
			logger.error("error!", e);
		}
		// Assert
		assertEquals(expectedUser.getUserId(), actualUser.getUserId());
	}

	@Test
	@Transactional
	public void testCreateUserWithRolesAndFilter() {
		// Arrange

		Stream<RoleData> streamRoles = roles.stream().filter(r -> r.getRoleId() == 2 || r.getRoleId() == 5);
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

		UserData expectedUser = new UserData("iTest99", "password", "test", "99", 1, authRoles,"", false, false, false, "1111111111");

		UserData actualUser = null;
		// Act
		try {
			actualUser = userService.create(expectedUser);
		} catch (Exception e) {
			logger.error("error!", e);
			;
			;
		}

		// Assert
		assertEquals(expectedUser.getUserId(), actualUser.getUserId());
	}

	@Test
	@Transactional
	public void testUpdateUserWithOutFilters() {

		// Arrange
		Stream<RoleData> streamRoles = roles.stream().filter(r -> r.getRoleId() == 2);
		Set<RoleData> authRoles = streamRoles.collect(Collectors.toSet());

		logger.info(authRoles.toString());
		UserData expectedUser = new UserData("iTest99", "password", "test", "99", 1, authRoles, "", false, false, false, "1111111111");

		try {
			userService.create(expectedUser);
		} catch (Exception e) {
			logger.error("error!", e);
		}
		// update lastname
		UserData expectedUserU = new UserData("iTest99", "password", "test", "100", 1, authRoles, "", false, false, false, "1111111111");

		UserData actualUserU = null;
		// Act
		try {
			actualUserU = userService.update(expectedUserU);
		} catch (Exception e) {
			logger.error("error!", e);
		}

		// Assert
		assertEquals(expectedUserU.getLastName(), actualUserU.getLastName());
		assertEquals(expectedUserU.getRoles(), actualUserU.getRoles());
	}

	@Test
	@Transactional
	public void testUpdateUserWithFilters() {
		// Arrange

		Stream<RoleData> streamRoles = roles.stream().filter(r -> r.getRoleId() == 2 || r.getRoleId() == 5);
		Set<RoleData> authRoles = streamRoles.collect(Collectors.toSet());

		Set<String> originAirports = new HashSet<String>();
		originAirports.add("HFN");

		Set<String> destinationAirports = new HashSet<String>();
		destinationAirports.add("KEF");

		int etaStart = -2;
		int etaEnd = 2;

		UserData expectedUser = new UserData("iTest99", "password", "test", "99", 1, authRoles, "", false, false, false, "1111111111");

		// Act
		try {
			userService.create(expectedUser);
		} catch (Exception e) {
			logger.error("error!", e);
			;
			;
		}

		Stream<RoleData> streamRolesU = roles.stream().filter(r -> r.getRoleId() == 2 || r.getRoleId() == 3);
		Set<RoleData> authRolesU = streamRolesU.collect(Collectors.toSet());
		originAirports.add("GKA");

		destinationAirports.add("LAE");
		etaStart = -3;
		etaEnd = 3;

		logger.info(authRoles.toString());
		UserData expectedUserU = new UserData("iTest99", "password", "test", "99", 1, authRolesU, "", false, false, false, "1111111111");

		UserData actualUserU = null;
		// Act
		try {
			actualUserU = userService.update(expectedUserU);
		} catch (Exception e) {
			logger.error("error!", e);
		}

		// Assert
		assertEquals(expectedUserU.getRoles(), actualUserU.getRoles());
	}


	@Test
	@Transactional
	public void testUpdateUserWithFakeRoles() {
		Set<RoleData> rolesToSave = new HashSet<RoleData>();
		RoleData validRole = roles.stream().filter(r -> r.getRoleId() == 2).findFirst().get();
		RoleData invalidRole = new RoleData(300, "XXXX");
		rolesToSave.add(validRole);
		rolesToSave.add(invalidRole);
		String userId = "fakeroles";


		UserData expectedUser = new UserData(userId, "password", "test", "99", 1, rolesToSave, "fake@email.what", false, false, false, "1111111111");
		UserData actualUser = null;

		try {
			userService.create(expectedUser);
		} catch (Exception e) {
			logger.error("error!", e);
		}

		try {
			actualUser = userService.findById(userId);
		} catch (Exception e) {
			logger.error("error!", e);
		}

		// one role was saved
		assertEquals(actualUser.getRoles().stream().filter(r -> r != null).count(), 1);

		// role save was the valid role
		assertEquals(validRole, (actualUser.getRoles()).stream().filter(role -> role != null).findFirst().get());
	}


	@Test
	@Transactional
	public void testUpdateUserPassword() {
		String userId = "passwordonly";
		String password = "TeStPa$$w0rd";
		Set<RoleData> rolesToSave = new HashSet<RoleData>();
		RoleData validRole = roles.stream().filter(r -> r.getRoleId() == 2).findFirst().get();
		rolesToSave.add(validRole);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String updatedPassword = passwordEncoder.encode("UpD@tedPa55W0rd");

		UserData expectedUser = new UserData(userId, password, "test", "99", 1, rolesToSave, "fake@email.what", false, false, false, "1111111111");
		UserData actualUser = null;

		try {
			userService.create(expectedUser);
		} catch (Exception e) {
			logger.error("error!", e);
		}

		try {
			actualUser = userService.updatePassword(userId, updatedPassword);
		} catch (Exception e) {
			logger.error("error!", e);
		}

		//password is updated
		assertEquals(actualUser.getPassword(), updatedPassword);
		//everything else stays the same
		assertEquals(actualUser.getFirstName(), expectedUser.getFirstName());
		assertEquals(actualUser.getLastName(), expectedUser.getLastName());
		assertEquals(actualUser.getEmail(), expectedUser.getEmail());
		assertEquals(actualUser.getRoles(), expectedUser.getRoles());
		assertEquals(actualUser.getPhoneNumber(), expectedUser.getPhoneNumber());
		assertEquals(actualUser.getActive(), expectedUser.getActive());
		assertEquals(actualUser.getArchived(), expectedUser.getArchived());
		assertEquals(actualUser.getEmailEnabled(), expectedUser.getEmailEnabled());
		assertEquals(actualUser.getHighPriorityEmail(), expectedUser.getHighPriorityEmail());
	}


	@Test
	@Transactional
	public void testAdminRoleIsAssignedAloneOnCreate() {
		String userId = "adminroleonly";
		String password = "sdfsdf";
		Set<RoleData> rolesToSave = roles.stream().filter(r -> r.getRoleId() < 4).collect(Collectors.toSet());
		RoleData admin = roles.stream().filter(r -> r.getRoleId() == 1).findFirst().get();

		UserData expectedUser = new UserData(userId, password, "test", "99", 1, rolesToSave, "fake@email.what", false, false, false, "1111111111");
		UserData actualUser = null;

		try {
			actualUser = userService.create(expectedUser);
		} catch (Exception e) {
			logger.error("error!", e);
		}

		// one role was saved
		assertEquals(actualUser.getRoles().stream().filter(r -> r != null).count(), 1);

		// saved role is admin
		assertEquals(actualUser.getRoles().stream().filter(r -> r != null).findFirst().get(), admin);

	}


	@Test
	@Transactional
	public void testAdminRoleIsAssignedAloneOnUpdate() {
		String userId = "adminroleonly";
		String password = "sdfsdf";
		RoleData admin = roles.stream().filter(r -> r.getRoleId() == 1).findFirst().get();
		RoleData nonadmin = roles.stream().filter(r -> r.getRoleId() == 2).findFirst().get();
		Set<RoleData> rolesToSave = new HashSet<RoleData>();

		// add nonadmin role
		rolesToSave.add(nonadmin);
		UserData expectedUser = new UserData(userId, password, "test", "99", 1, rolesToSave, "fake@email.what", false, false, false, "1111111111");
		UserData actualUser = null;


		try {
			userService.create(expectedUser);
		} catch (Exception e) {
			logger.error("error!", e);
		}

		// add admin role and update user
		rolesToSave.add(admin);
		UserData updatedUser = new UserData(userId, password, "test", "99", 1, rolesToSave, "fake@email.what", false, false, false, "1111111111");
		try {
			actualUser = userService.updateByAdmin(updatedUser);
		} catch (Exception e) {
			logger.error("error!", e);
		}

		// one role was saved
		assertEquals(actualUser.getRoles().stream().filter(r -> r != null).count(), 1);

		// saved role is admin
		assertEquals(actualUser.getRoles().stream().filter(r -> r != null).findFirst().get(), admin);

	}

}
