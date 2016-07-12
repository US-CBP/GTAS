/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.security.filters;


import gov.gtas.security.service.UserDaoImpl;

import java.util.Date;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;


@Component("authenticationProvider")
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    UserDaoImpl userDetailsDao;
 
    @Autowired
    @Qualifier("userDetailsService")
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }
    
    
    @Override
    public Authentication authenticate(Authentication authentication) 
          throws AuthenticationException {
 
      try {
          
        Authentication auth = super.authenticate(authentication);
        return auth;
 
      } catch (BadCredentialsException e) { 
        throw e;
      } catch (LockedException e){
 
      throw new LockedException("");
    }
 
    }
 
    
}
