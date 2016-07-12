/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.config;

import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Sets up the Spring Security filter chain
 *
 */
@Order(2)
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
}

