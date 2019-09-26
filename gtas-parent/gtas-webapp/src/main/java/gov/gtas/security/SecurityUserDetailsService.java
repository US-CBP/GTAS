/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;

/**
 *
 * UserDetails service that reads the user credentials from the database, using
 * a JPA repository.
 *
 */
@Service("userDetailsService")
public class SecurityUserDetailsService implements UserDetailsService {
	private static final Logger logger = LoggerFactory.getLogger(SecurityUserDetailsService.class);

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserData user = userService.findById(username);

		if (user == null || user.getActive() == 0) {
			String message = "Username not found: " + username;
			logger.info(message);
			throw new UsernameNotFoundException(message);
		}

		List<GrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getRoleDescription())));
		logger.info("Found user in database: " + username);

		return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
	}
}
