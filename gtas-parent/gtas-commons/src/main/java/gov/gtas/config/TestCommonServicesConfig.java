/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.config;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The configuration class can be imported into an XML configuration by:<br>
 * <context:annotation-config/> <bean
 * class="gov.gtas.config.TestCommonServicesConfig"/>
 */
@Configuration
@ComponentScan("gov.gtas")
@PropertySource({ "classpath:commonservices.properties" })
@EnableJpaRepositories("gov.gtas")
@EnableTransactionManagement
@Import(AsyncConfig.class)
public class TestCommonServicesConfig {

    private static Logger logger = LoggerFactory.getLogger(TestCommonServicesConfig.class);

    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";

    @Resource
    private Environment env;

    /**
     * Do not remove the <code>JndiBean</bean> jndiBean field below.
     * 
     * Since there is no Java EE server running at the unit test level 
     * the JNDI connection was constructed manually and injected here to be used '
     * the unit and integration tests.
     */
    @SuppressWarnings("unused")
    private TestJndiBean jndiBean = new TestJndiBean();

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		pspc.setIgnoreUnresolvablePlaceholders(true);
		return pspc;
	}
    
    @Bean
    @Autowired
    public DataSource dataSource() {
    	
    	DataSource dataSource = null;
    	
    	JndiTemplate jndi = new JndiTemplate();
    	
    	try {
    		dataSource = (DataSource) jndi.lookup("java:comp/env/jdbc/gtasDataSource");
    	} catch(NamingException e) {
    		logger.error("NamingException for java:comp/env/jdbc/gtasDataSource", e);
    	}
    	
    	return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean
                .setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean
                .setPackagesToScan(env
                        .getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));

        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory()
                .getObject());
        return transactionManager;
    }
    
    

}
