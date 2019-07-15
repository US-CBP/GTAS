/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration 
@ComponentScan("gov.gtas") 
@PropertySource("classpath:application.properties")
@EnableWebMvc
public class AppConfiguration extends WebMvcConfigurerAdapter {  
	 private static final Logger logger = LoggerFactory
	            .getLogger(AppConfiguration.class);
    @Bean(name="gtasMessageSource")
    public MessageSource messageSource() {
        GtasResourceBundleMessageSource messageSource = new GtasResourceBundleMessageSource();
        messageSource.setBasename("/WEB-INF/messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
    
    @Bean
    public LocaleResolver localeResolver(){
    	
    	Properties prop = new Properties();
    	InputStream input = null;
    	String language="en";
    	try {
    		input =this.getClass().getClassLoader().getResourceAsStream("application.properties");
    		prop.load(input);
    		language=prop.getProperty("site.language");
    		
    	} catch (IOException ex) {
    		//logger.error("error!", e);
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				//logger.error("error!", e);
    			}
    		}
    	}
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
    
} 
