/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.neo4jconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
//@ComponentScan("gov.gtas")
//@PropertySource("classpath:default.application.properties")
//@PropertySource(value = "file:${catalina.home}/conf/application.properties", ignoreResourceNotFound = true)
public class Neo4JConfig {

	@Resource
	private Environment env;

	private static final String PROPERTY_NAME_USERNAME = "neo4jusername";
	private static final String PROPERTY_NAME_CRED = "neo4jpassword";
	private static final String NEO_4_J_ENABLED = "neo4jEnabled";
	private static final String NEO_4_J_GRAPH_DB_URL = "neo4jUrl";
	private static final String NEO_4_J_RULE_ENGINE_ENABLED="neo4jRuleEngineEnabled";

	public Neo4JConfig() {
	}

	public boolean getNeo4JRuleEngineEnabled() {
		return Boolean.parseBoolean(env.getRequiredProperty(NEO_4_J_RULE_ENGINE_ENABLED));
	}

	public String getNeo4JGraphDbUrl() {
		return env.getRequiredProperty(NEO_4_J_GRAPH_DB_URL);
	}

	public String neoUserName() {
		return env.getRequiredProperty(PROPERTY_NAME_USERNAME);
	}

	public boolean enabled() {
		return Boolean.parseBoolean(env.getRequiredProperty(NEO_4_J_ENABLED));
	}

	public String neoPassword() {
		return env.getRequiredProperty(PROPERTY_NAME_CRED);
	}

}
