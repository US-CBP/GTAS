/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;

/**
 *
 * The Spring Security configuration for the application - its a form login
 * config with authentication via session cookie (once logged in), with fallback
 * to HTTP Basic for non-browser clients.
 *
 * The CSRF token is put on the reply as a header via a filter.
 *
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppSecurityConfig.class);

	@Autowired
	private SecurityUserDetailsService userDetailsService;

	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/factory/**/*", "/admin/**/*",
				"/flights/**/*", "/pax/**/*", "/query-builder/**/*",
				"/watchlists/**/*", "/build/**/*", "/dashboard/**/*",
				"/dist/**/*", "/jqb/**/*", "/userSettings/**/*", "/cases/**/*","/onedaylookout/**/*",
				"/resources/**", "/common/**/*", "/login/**", "/admin/**",
				"/app.js", "WEB-INF/**/*", "/data/**");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(
				new BCryptPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		SavedRequestAwareAuthenticationSuccessHandler savedReqHandler = new SavedRequestAwareAuthenticationSuccessHandler();

		CsrfTokenResponseHeaderBindingFilter csrfTokenFilter = new CsrfTokenResponseHeaderBindingFilter();
		http.addFilterAfter(csrfTokenFilter, CsrfFilter.class).csrf()
				.csrfTokenRepository(csrfTokenRepository());

		http.csrf().disable();

		http.authorizeRequests()
				.antMatchers("/resources/*/**", "/resources/**/*",
						"/resources/**", "/common/**", "/login/**",
						"/authenticate")
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.formLogin()
				.loginProcessingUrl("/authenticate")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(
						new AjaxAuthenticationSuccessHandler(savedReqHandler))
				.failureHandler(new SimpleUrlAuthenticationFailureHandler())
				.loginPage("/login.html")
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login.html")
				.invalidateHttpSession(true)
				.permitAll();

		http.sessionManagement().maximumSessions(1).and()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);

		if ("true".equals(System.getProperty("httpsOnly"))) {
			LOGGER.info("launching the application in HTTPS-only mode");
			http.requiresChannel().anyRequest().requiresSecure();
		}
	}

	/**
	 * Util Method to add XSRF Token into the HTTP Header
	 */
	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-CSRF-TOKEN");
		return repository;
	}

}
