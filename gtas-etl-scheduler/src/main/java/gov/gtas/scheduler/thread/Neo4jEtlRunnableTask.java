/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.scheduler.thread;

import static gov.gtas.scheduler.Constants.LINUX_CMD_CONST;
import static gov.gtas.scheduler.Constants.WINDOWS;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.gtas.configuration.Neo4jEtlConfig;
import gov.gtas.scheduler.Constants;

@Component
public class Neo4jEtlRunnableTask extends EtlTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(Neo4jEtlRunnableTask.class);
	private static boolean isProcessing = false;

	@Autowired
	private Neo4jEtlConfig neo4jEtlConfig;

	private String command;

	@Override
	public void run() {

		if (!isProcessing && neo4jEtlConfig.getEnableEtl()) {
			log.info(" COMMAND LINE: " + this.command);
			executeOnCommandLine(this.command);
		} else if (!neo4jEtlConfig.getEnableEtl()) {
			log.info("\n");
			log.info("!!!!! THE SCHEDULER FOR THE NEO4J ETL JOB IS DISABLED !!!!! See scheduelr.properties file. ");
			log.info("\n");

		}
	}

	public synchronized void executeOnCommandLine(String command) {
		int exitValue = -1;
		try {
			log.info("\n");
			log.info("==== ==== ==== ##  STARTING {} JOB ## ==== ==== ====", neo4jEtlConfig.getEtlName());
			Runtime runtime = Runtime.getRuntime();
			Process process = null;
			if (neo4jEtlConfig.getOpSystem() != null
					&& neo4jEtlConfig.getOpSystem().trim().equalsIgnoreCase(Constants.LINUX)) {
				process = runtime.exec(new String[] { LINUX_CMD_CONST, "-c", command });
			} else if (neo4jEtlConfig.getOpSystem() != null
					&& neo4jEtlConfig.getOpSystem().trim().equalsIgnoreCase(WINDOWS)) {
				process = runtime.exec(command);
			}

			InputStream stderr = process.getErrorStream();
			InputStreamReader inputStreamReader = new InputStreamReader(stderr);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				log.info(line);
				isProcessing = true;
			}
			exitValue = process.waitFor();
			isProcessing = false;
			process.destroy();
			log.info("==== ==== ==== ### END OF {} JOB. EXIT VALUE = {}  ### ==== ==== ====",
					neo4jEtlConfig.getEtlName(), exitValue);
			log.info("\n");

		} catch (Exception e) {
			String errorInfo = "An error has occurred when launching  " + neo4jEtlConfig.getEtlName()
					+ " from GTAS ETL Scheduler.";
			log.error(errorInfo);
		}

	}

	@PostConstruct
	public String getCommand() {

		CommandArgs commandArgs = new CommandArgs();
		commandArgs.setConfigFile(neo4jEtlConfig.getConfigFile());
		commandArgs.setConfigFilePropertyName(neo4jEtlConfig.getConfigFilePropertyName());
		commandArgs.setJobDirectory(neo4jEtlConfig.getJobDir());
		commandArgs.setLogDirectory(neo4jEtlConfig.getLogDir());
		commandArgs.setLogLevel(neo4jEtlConfig.getLogLevel());
		commandArgs.setOpSystem(neo4jEtlConfig.getOpSystem());
		commandArgs.setPdiDirectory(neo4jEtlConfig.getPdiDir());
		this.command = this.getEtlCommand(commandArgs);

		return this.command;

	}

	public void setCommand(String command) {
		this.command = command;
	}

}
