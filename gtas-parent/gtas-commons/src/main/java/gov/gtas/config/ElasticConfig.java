package gov.gtas.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
@ComponentScan("gov.gtas")
@PropertySource("classpath:default.application.properties")
@PropertySource(value = "file:${catalina.home}/conf/application.properties", ignoreResourceNotFound = true)
public class ElasticConfig {

    @Resource
    private Environment env;

    private static final String ELASTIC_HOST = "elastic.hostname";
    private static final String ELASTIC_PORT = "elastic.port";

    public ElasticConfig() { }

    public String getElasticHost() {
        return env.getRequiredProperty(ELASTIC_HOST);
    }

    public String getElasticPort() {
        return env.getRequiredProperty(ELASTIC_PORT);
    }

}