/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.security;

import gov.gtas.common.WebAppConfig;
import gov.gtas.controller.config.TestMvcRestServiceWebConfig;
import gov.gtas.model.User;
import gov.gtas.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestMvcRestServiceWebConfig.class, WebAppConfig.class })
@WebAppConfiguration
@Rollback(true)
public class SecurityUserDetailsServiceIT {

	private static final Logger logger = LoggerFactory.getLogger(SecurityUserDetailsServiceIT.class);
	@Autowired
	UserRepository userDao;

	@Autowired
	SecurityUserDetailsService userDetailsService;

	@Before
	public void setUp() {
		User u = new User();
		u.setFirstName("jj");
		u.setLastName("jones");
		u.setUserId("jJone");
		BCryptPasswordEncoder e = new BCryptPasswordEncoder();
		String pw = e.encode("password");
		u.setPassword(pw);
		userDao.save(u);
	}

	@Test
	@Transactional
	public void testDoesNotExist() {
		boolean pass = false;
		try {
			userDetailsService.loadUserByUsername("none");
		} catch (UsernameNotFoundException e) {
			pass = true;
		}
		assertTrue(pass);
	}

	@Test
	@Transactional
	public void testMyUser() {
		UserDetails u = userDetailsService.loadUserByUsername("admin");
		assertNotNull(u);
		logger.info(u.toString());
	}
}
