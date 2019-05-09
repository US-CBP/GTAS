/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
@ComponentScan("gov.gtas")
@PropertySource({"classpath:commonservices.properties"})
public class Neo4JConfig {

    @Resource
    private Environment env;

    private static final String PROPERTY_NAME_USERNAME = "neo4jusername";
    private static final String PROPERTY_NAME_PASSWORD = "neo4jpassword";

    public Neo4JConfig() {
    }


    public String neoUserName() {
        return env.getRequiredProperty(PROPERTY_NAME_USERNAME);
    }

    public String neoPassword() {
        return env.getRequiredProperty(PROPERTY_NAME_PASSWORD);
    }

}

