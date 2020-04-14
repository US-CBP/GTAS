/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import gov.gtas.config.TestCommonServicesConfig;

/**
 * The configuration class can be imported into an XML configuration by:<br>
 * <context:annotation-config/>
 * <bean class="gov.gtas.config.TestCommonServicesConfig"/>
 */
@Configuration
@ComponentScan("gov.gtas")
@PropertySource({
    "classpath:default.application.properties",
    "classpath:tamr.application.properties"
})
@EnableJpaRepositories("gov.gtas")
@EnableTransactionManagement
@Import({ TestCommonServicesConfig.class })
public class TamrIntegrationTestConfig {
}
