/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Authentication success handler for integration with SPA applications that
 * need to login using Ajax instead of a form post.
 *
 * Detects if its a ajax login request, and if so sends a customized response in
 * the body, otherwise defaults to the existing behaviour for none-ajax login
 * attempts.
 */
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(AjaxAuthenticationSuccessHandler.class);

	private AuthenticationSuccessHandler defaultHandler;

	public AjaxAuthenticationSuccessHandler(AuthenticationSuccessHandler defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		if ("true".equals(request.getHeader("X-Login-Ajax-call"))) {
			response.getWriter().print("ok");
			response.getWriter().flush();

		} else {
			defaultHandler.onAuthenticationSuccess(request, response, authentication);
		}
	}
}
