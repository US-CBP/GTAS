/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
@ComponentScan("gov.gtas")
@PropertySource("classpath:default.application.properties")
@PropertySource(value = "file:${catalina.home}/conf/application.properties", ignoreResourceNotFound = true)
@EnableWebMvc
public class AppConfiguration extends WebMvcConfigurerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AppConfiguration.class);

	@Value("${site.language}")
	String language;

	@Value("${ui.address}")
	String uiAddress;

	@Bean(name = "gtasMessageSource")
	public MessageSource messageSource() {
		GtasResourceBundleMessageSource messageSource = new GtasResourceBundleMessageSource();
		messageSource.setBasename("/WEB-INF/messages/messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver resolver = new CookieLocaleResolver();
		resolver.setDefaultLocale(new Locale(language));
		resolver.setCookieName("myLocaleCookie");
		resolver.setCookieMaxAge(4800);
		return resolver;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		registry.addInterceptor(interceptor);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins(uiAddress).allowedMethods("GET", "POST", "OPTIONS", "PUT")
				.allowedHeaders("Content-Type", "X-Requested-With", "accept", "Access-Control-Request-Method",
						"Access-Control-Request-Headers", "X-Auth-Token", "X-login-ajax-call", "Router")
				.exposedHeaders("Access-Control-Allow-Credentials", "Access-Control-Allow-Origin")
				.allowedMethods("GET","PUT","POST","DELETE","PATCH","OPTIONS")
				.allowCredentials(true).maxAge(3600);
	}
}
