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
    private static final String ELASTIC_SSL_KEY = "elastic.ssl.key";
    private static final String ELASTIC_SSL_CA =  "elastic.ssl.certificate_authorities";
    private static final String ELASTIC_SSL_CERT = "elastic.ssl.certificate";
    private static final String ELASTIC_SSL_VERIFICATION_MODE = "elastic.ssl.verification_mode";
    private static final String ELASTIC_CLUSTER_NAME = "elastic.cluster.name";
    private static final String ELASTIC_NODE_NAME = "elastic.node.name";
    private static final String ELASTIC_USERNAME = "elastic.username";
    private static final String ELASTIC_PWD = "elastic.password";
    private static final String SSL_ENABLED = "elastic.ssl.enabled";

    public ElasticConfig() { }

    public String getElasticHost() {
        return env.getRequiredProperty(ELASTIC_HOST);
    }

    public String getElasticPort() {
        return env.getRequiredProperty(ELASTIC_PORT);
    }

    public String getElasticSslKey() {
        return env.getRequiredProperty(ELASTIC_SSL_KEY);
    }

    public String getElasticSslCa() {
        return env.getRequiredProperty(ELASTIC_SSL_CA);
    }

    public String getElasticSslCert() {
        return env.getRequiredProperty(ELASTIC_SSL_CERT);
    }

    public String getElasticSslVerificationMode() {
        return env.getRequiredProperty(ELASTIC_SSL_VERIFICATION_MODE);
    }

    public String getElasticClusterName() {
        return env.getRequiredProperty(ELASTIC_CLUSTER_NAME);
    }

    public String getElasticNodeName() {
        return env.getRequiredProperty(ELASTIC_NODE_NAME);
    }

    public String getElasticCredentials() {
        return env.getRequiredProperty(ELASTIC_USERNAME) + ":" + env.getRequiredProperty(ELASTIC_PWD);
    }

    public boolean getSslEnabled() {
        return Boolean.parseBoolean(env.getRequiredProperty(SSL_ENABLED));
    }

}