package gov.loadeworker;

import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJms
@EnableAsync
@SpringBootApplication(scanBasePackages={"gov.gtas.model", "gov.gtas.repository", "gov.gtas.services","gov.gtas.parsers", "gov.gtas.parserconfig", "gov.gtas.aws", "gov.gtas.elasticsearchconfig", "gov.gtas.svc", "gov.gtas.neo4jconfig"
		,"gov.gtas.parserconfig", "gov.loadeworker"})
@EnableJpaRepositories("gov.gtas")
@EnableTransactionManagement
@EnableScheduling
@EntityScan(basePackages = { "gov.gtas.model", "gov.gtas.repository", "gov.gtas.services", "gov.gtas.parsers", "gov.gtas.parserconfig",
		"gov.gtas.parserconfig", "gov.loaderworker", "gov.gtas.aws", "gov.gtas.elasticsearchconfig", "gov.gtas.svc", "gov.gtas.neo4jconfig", "gov.gtas.rule"})
public class LoaderWorkerApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(LoaderWorkerApplication.class);
		//app.setLazyInitialization(true);
		System.setProperty("user.timezone", "UTC");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		app.run(args);
		
	}
	
}
