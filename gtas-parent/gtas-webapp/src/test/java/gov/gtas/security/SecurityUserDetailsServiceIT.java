/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.security;

import static org.junit.Assert.assertTrue;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.User;
import gov.gtas.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
        CachingConfig.class })
@WebAppConfiguration
@Transactional
public class SecurityUserDetailsServiceIT {
    @Autowired
    UserRepository userDao;

    @Autowired
    SecurityUserDetailsService userDetailsService;

    @Before
    public void setUp() {
        User u = new User();
        u.setFirstName("mike");
        u.setLastName("cope");
        u.setUserId("copenham");
        BCryptPasswordEncoder e = new BCryptPasswordEncoder();
        String pw = e.encode("password");
        u.setPassword(pw);
        userDao.save(u);
    }

    @Test
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
    public void testMyUser() {
        UserDetails u = userDetailsService.loadUserByUsername("copenham");
        System.out.println(u);
    }
}
