/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * The configuration class can be imported into an XML configuration by:<br>
 * <context:annotation-config/> <bean
 * class="gov.gtas.config.CommonServicesConfig"/>
 */
@Configuration
@ComponentScan("gov.gtas")
@PropertySource("classpath:commonservices.properties")
@PropertySource("classpath:hibernate.properties")
@PropertySource(value = "file:${catalina.home}/conf/application.properties", ignoreResourceNotFound = true)
@EnableJpaRepositories("gov.gtas")
@EnableTransactionManagement
@Import(AsyncConfig.class)
public class CommonServicesConfig {

    private static Logger logger = LoggerFactory.getLogger(CommonServicesConfig.class);

    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "hibernate.connection.driver_class";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "hibernate.connection.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "hibernate.connection.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "hibernate.connection.username";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
    private static final String PROPERTY_NAME_HIBERNATE_CACHE_FACTORY = "hibernate.cache.region.factory_class";
    private static final String PROPERTY_NAME_CONFIGURATION_RESOURCE_PATH = "hibernate.cache.provider_configuration_file_resource_path";
    private static final String PROPERTY_NAME_HIBERNATE_QUERY_CACHE = "hibernate.cache.use_query_cache";
    private static final String PROPERTY_NAME_HIBERNATE_USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
    private static final String PROPERTY_NAME_SHAREDCACHE_MODE = "javax.persistence.sharedCache.mode";
    private static final String PROPERTY_NAME_HIBERNATE_JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";
    private static final String PROPERTY_NAME_HIBERNATE_USE_SQL_COMMENTS = "hibernate.use_sql_comments";
    private static final String PROPERTY_NAME_HIBERNATE_ORDER_INSERTS = "hibernate.order_inserts";
    private static final String PROPERTY_NAME_HIBERNATE_ORDER_UPDATES = "hibernate.order_updates";
    private static final String PROPERTY_NAME_HIBERNATE_JDBC_BATCH_VERSION_DATA = "hibernate.jdbc.batch_versioned_data";
    private static final String HIBERNATE_TIMEOUT = "hibernate.timeout";

    private static final String PROPERTY_NAME_C3P0_MIN_SIZE = "c3p0.min_size";
    private static final String PROPERTY_NAME_C3P0_MAX_SIZE = "c3p0.max_size";
    private static final String PROPERTY_NAME_C3P0_MAX_IDLETIME = "c3p0.max_idletime";
    private static final String PROPERTY_NAME_C3P0_MAX_STATEMENTS = "c3p0.max_statements";
    private static final String PROPERTY_NAME_C3P0_MAX_CONNECT = "c3p0.idleConnectionTestPeriod";

    private static final String PROPERTY_NAME_HIBERNATE_CONNECTION_CHARSET = "hibernate.connection.charSet";

    @SuppressWarnings("Duplicates")
    private Properties hibProperties() {
        Properties properties = new Properties();
        properties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        properties.put(PROPERTY_NAME_HIBERNATE_QUERY_CACHE,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_QUERY_CACHE));
        properties.put(PROPERTY_NAME_SECOND_LEVEL_CACHE,
                env.getRequiredProperty(PROPERTY_NAME_SECOND_LEVEL_CACHE));
        properties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
        properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
        properties.put(PROPERTY_NAME_HIBERNATE_CACHE_FACTORY,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CACHE_FACTORY));
        properties
                .put(PROPERTY_NAME_CONFIGURATION_RESOURCE_PATH,
                        env.getRequiredProperty(PROPERTY_NAME_CONFIGURATION_RESOURCE_PATH));
        properties.put(PROPERTY_NAME_SHAREDCACHE_MODE,
                env.getRequiredProperty(PROPERTY_NAME_SHAREDCACHE_MODE));
        properties.put(PROPERTY_NAME_HIBERNATE_USE_MINIMAL_PUTS, env
                .getRequiredProperty(PROPERTY_NAME_HIBERNATE_USE_MINIMAL_PUTS));
        properties.put(PROPERTY_NAME_HIBERNATE_JDBC_BATCH_SIZE, env
                .getRequiredProperty(PROPERTY_NAME_HIBERNATE_JDBC_BATCH_SIZE));
        properties.put(PROPERTY_NAME_HIBERNATE_USE_SQL_COMMENTS, env
                .getRequiredProperty(PROPERTY_NAME_HIBERNATE_USE_SQL_COMMENTS));
        properties.put(PROPERTY_NAME_HIBERNATE_ORDER_INSERTS,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_ORDER_INSERTS));
        properties.put(PROPERTY_NAME_HIBERNATE_ORDER_UPDATES,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_ORDER_UPDATES));
        properties
                .put(PROPERTY_NAME_HIBERNATE_JDBC_BATCH_VERSION_DATA,
                        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_JDBC_BATCH_VERSION_DATA));
        properties
                .put(PROPERTY_NAME_HIBERNATE_CONNECTION_CHARSET,
                        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CONNECTION_CHARSET));

        return properties;
    }

    @Resource
    private Environment env;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		pspc.setIgnoreUnresolvablePlaceholders(true);
		return pspc;
	}

    @Bean
    public DataSource dataSource() {
        // DriverManagerDataSource dataSource mvcn= new DriverManagerDataSource();
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
    	try {
            dataSource.setDriverClass(env
                    .getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        } catch (PropertyVetoException pve) {
            logger.error("Unable to get required property!", pve);
    	}
        dataSource.setJdbcUrl(env
                .getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
        dataSource.setUser(env
                .getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
        dataSource.setPassword(env
                .getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
        dataSource.setMinPoolSize(Integer.parseInt(env
                .getRequiredProperty(PROPERTY_NAME_C3P0_MIN_SIZE)));
        dataSource.setMaxPoolSize(Integer.parseInt(env
                .getRequiredProperty(PROPERTY_NAME_C3P0_MAX_SIZE)));
        dataSource.setMaxIdleTime(Integer.parseInt(env
                .getRequiredProperty(PROPERTY_NAME_C3P0_MAX_IDLETIME)));
        dataSource.setMaxStatements(Integer.parseInt(env
                .getRequiredProperty(PROPERTY_NAME_C3P0_MAX_STATEMENTS)));
        dataSource.setIdleConnectionTestPeriod(Integer.parseInt(env
                .getRequiredProperty(PROPERTY_NAME_C3P0_MAX_CONNECT)));
        dataSource.setCheckoutTimeout(Integer.parseInt(env.
                getRequiredProperty(HIBERNATE_TIMEOUT)));
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

        entityManagerFactoryBean.setJpaProperties(hibProperties());

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
