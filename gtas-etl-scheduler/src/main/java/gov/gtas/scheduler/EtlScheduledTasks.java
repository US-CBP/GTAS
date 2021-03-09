/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.scheduler;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.gtas.configuration.Neo4jEtlConfig;
import gov.gtas.configuration.ReportEtlConfig;
import gov.gtas.scheduler.thread.ReportEtlRunnableTask;
import gov.gtas.scheduler.thread.Neo4jEtlRunnableTask;

@Component
public class EtlScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(EtlScheduledTasks.class);

	@Autowired
	private SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

	@Autowired
	private Neo4jEtlRunnableTask runnableTask;

	@Autowired
	private ReportEtlRunnableTask reportEtlRunnableTask;

	@Autowired
	Neo4jEtlConfig neo4jEtlConfig;

	@Autowired
	ReportEtlConfig reportEtlConfig;

	/*
	 * @Autowired ReportAppConfig reportAppConfig;
	 */

	@Scheduled(fixedDelayString = "${neo4j.execInterval}000")
	public void startEtl() {

		simpleAsyncTaskExecutor.execute(runnableTask);
		simpleAsyncTaskExecutor.execute(reportEtlRunnableTask);

	}

	@PostConstruct
	public void logProperties() {
		log.info("----------------------------------");
		log.info("--------COMMON SCHEDULER PROPERTIES FROM scheduler.properties FILE -----");
		log.info("- opSystem: 		" + neo4jEtlConfig.getOpSystem());
		log.info("- pdiDir: 		" + neo4jEtlConfig.getPdiDir());

		log.info("\n");
		log.info("--------NEO4J ETL SCHEDULER PROPERTIES FROM scheduler.properties FILE -----");
		log.info("- enableEtl: 			" + neo4jEtlConfig.getEnableEtl());
		log.info("- etlName: 			" + neo4jEtlConfig.getEtlName());
		log.info("- jobDir: 			" + neo4jEtlConfig.getJobDir());
		log.info("- logDir: 			" + neo4jEtlConfig.getLogDir());
		log.info("- execInterval: 		" + neo4jEtlConfig.getExecInterval());
		log.info("- logLevel: 			" + neo4jEtlConfig.getLogLevel());
		log.info("- configFilePropertyName: " + neo4jEtlConfig.getConfigFilePropertyName());
		log.info("- configFile: 		" + neo4jEtlConfig.getConfigFile());
		log.info("\n");

		log.info("--------REPORT ETL SCHEDULER PROPERTIES FROM scheduler.properties FILE  -----");
		log.info("- enableEtl: 		" + reportEtlConfig.getEnableEtl());
		log.info("- etlName: 		" + reportEtlConfig.getEtlName());
		log.info("- etlName: 		" + reportEtlConfig.getJobDir());
		log.info("- logDir: 		" + reportEtlConfig.getLogDir());
		log.info("- execInterval: 	" + reportEtlConfig.getExecInterval());
		log.info("- logLevel: 		" + reportEtlConfig.getLogLevel());
		log.info("- configFilePropertyName: " + neo4jEtlConfig.getConfigFilePropertyName());
		log.info("- configFile: 	" + neo4jEtlConfig.getConfigFile());
		log.info("\n");
		log.info("----------------------------------");

	}
}
