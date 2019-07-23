/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.logcollector;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.gtas.logcollector.thread.RunnableTask;

@Component
public class LogCollectorScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(LogCollectorScheduledTasks.class);

	@Autowired
	private SyncTaskExecutor syncTaskExecutor;

	@Autowired
	private RunnableTask runnableTask;

	@Autowired
	AppConfig appConfig;

	@Scheduled(fixedDelayString = "${execInterval}000")
	public void loadDataToNeo4j() {

		log.info("Starting the thread to execute the PDI job ....");
		syncTaskExecutor.execute(runnableTask);

	}

	@PostConstruct
	public void logProperties() {

		log.info("--------SCHEDULER PROPERTIES FROM PROPERTIES FILE -----");
		log.info("- execInterval: 	" + appConfig.getExecInterval());
		log.info("- opSystem: 		" + appConfig.getOpSystem());
		log.info("- pdiDir: 		" + appConfig.getPdiDir());
		log.info("- jobDir: 		" + appConfig.getJobDir());
		log.info("- logLevel: 		" + appConfig.getLogLevel());
		log.info("- logDir: 		" + appConfig.getLogDir());
		log.info("- configFilePropertyName: 		" + appConfig.getConfigFilePropertyName());
		log.info("- configFile: 		" + appConfig.getConfigFile());
		log.info("----------------------------------");

	}
}
