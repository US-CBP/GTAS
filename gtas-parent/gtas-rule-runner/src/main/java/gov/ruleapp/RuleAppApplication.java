package gov.ruleapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJms
@EnableAsync
@SpringBootApplication(scanBasePackages={"gov.gtas.model", "gov.gtas.repository", "gov.gtas.services","gov.gtas.parsers", "gov.gtas.parserconfig", "gov.ruleapp", "gov.gtas.aws", "gov.gtas.elasticsearchconfig", "gov.gtas.svc", "gov.gtas.neo4jconfig", "gov.gtas.rule"})
@EnableJpaRepositories("gov.gtas")
@EnableTransactionManagement
@EntityScan(basePackages = { "gov.gtas.model", "gov.gtas.repository", "gov.gtas.services", "gov.gtas.parsers",
		"gov.gtas.parserconfig", "gov.ruleapp", "gov.gtas.aws", "gov.gtas.elasticsearchconfig", "gov.gtas.svc", "gov.gtas.neo4jconfig", "gov.gtas.rule"})
public class RuleAppApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RuleAppApplication.class);
		//app.setLazyInitialization(true);
		app.run(args);
	}

}
