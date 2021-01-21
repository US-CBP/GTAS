package gov.gtas.security;

import gov.gtas.services.security.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MaxLoginAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private LoginService loginService;

    public MaxLoginAuthenticationProvider() {
        super();
        super.setPasswordEncoder(new BCryptPasswordEncoder());
    }

    @Autowired
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = (String)authentication.getPrincipal();
        try {
            Authentication auth = super.authenticate(authentication);
            loginService.resetFailedLoginAttemptCount(username);

            return auth;
        } catch (BadCredentialsException badCredentialsException) {
            loginService.addToFailAttempts(username);

            throw badCredentialsException;
        }

    }
}
