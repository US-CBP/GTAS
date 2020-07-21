/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("gov.gtas")
@EnableJpaRepositories("gov.gtas")
@EnableSpringDataWebSupport
@EnableAspectJAutoProxy
public class WebAppConfig {
	// @Bean
	// public UrlBasedViewResolver setupViewResolver() {
	// UrlBasedViewResolver resolver = new UrlBasedViewResolver();
	// resolver.setPrefix("/WEB-INF/views/");
	// resolver.setSuffix(".jsp");
	// resolver.setViewClass(JstlView.class);
	//
	// return resolver;
	// }

}
