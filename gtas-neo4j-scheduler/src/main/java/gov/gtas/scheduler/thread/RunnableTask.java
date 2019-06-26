/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.scheduler.thread;

import static gov.gtas.scheduler.Constants.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.gtas.scheduler.AppConfig;
import gov.gtas.scheduler.Constants;

@Component
public class RunnableTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(RunnableTask.class);
	private static boolean isProcessing = false;

	@Autowired
	private AppConfig appConfig;

	private String command;

	@Override
	public void run() {

		if (!isProcessing) {
			log.info(" COMMAND LINE: " + this.command);
			executeOnCommandLine(this.command);
		}
	}

	public synchronized void executeOnCommandLine(String command) {
		int exitValue = -1;
		try {
			log.info(" *** LAUNCHING PDI ETL JOB FROM SCHEDULER **** ");

			Runtime runtime = Runtime.getRuntime();
			Process process = null;
			if (appConfig.getOpSystem() != null && appConfig.getOpSystem().trim().equalsIgnoreCase(Constants.LINUX)) {
				process = runtime.exec(new String[] { LINUX_CMD_CONST, "-c", command });
			} else if (appConfig.getOpSystem() != null && appConfig.getOpSystem().trim().equalsIgnoreCase(WINDOWS)) {
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
			log.info("***  END OF ETL JOB FROM SCHEDULER .....EXIT VALUE = " + exitValue);
			process.destroy();

		} catch (Exception e) {
			log.error("An error has occurred when launching the PDI ETL job from the gtas-neo4j-job-scheduler");
			e.printStackTrace();
		}

	}

	@PostConstruct
	public String getCommand() {

		String pdiDirectory = appConfig.getPdiDir();
		String jobDirectory = appConfig.getJobDir();
		String logLevel = appConfig.getLogLevel();
		String logDirectory = appConfig.getLogDir();
		String configFilePropertyName = appConfig.getConfigFilePropertyName();
		String configFile = appConfig.getConfigFile();

		if (appConfig.getOpSystem() != null && appConfig.getOpSystem().trim().equalsIgnoreCase(WINDOWS)) {

			this.command = WINDOWS_CMD_CONST + " " + pdiDirectory + "  " + " " + WINDOWS_PARAM_PREFIX + FILE_IND
					+ WINDOWS_PARAM_DELIMITER + jobDirectory + "  ";
			this.command = command + "\"" + WINDOWS_PARAM_PREFIX + WINDOWS_PARAM + WINDOWS_PARAM_DELIMITER
					+ configFilePropertyName + WINDOWS_PARAM_SUFFIX + configFile + "\"  ";
			this.command = command + WINDOWS_PARAM_PREFIX + LEVEL + WINDOWS_PARAM_DELIMITER + logLevel + " "
					+ LOG_REDIRECTOR + " " + logDirectory + "_" + getCurrentTimeStamp() + LOG_FILE_EXT;

		} else if (appConfig.getOpSystem() != null
				&& appConfig.getOpSystem().trim().equalsIgnoreCase(Constants.LINUX)) {

			this.command = pdiDirectory + " " + LINUX_PARAM_PREFIX + FILE_IND + LINUX_PARAM_SUFFIX + "'" + jobDirectory
					+ "' ";
			this.command = command + LINUX_PARAM_PREFIX + LINUX_PARAM + LINUX_PARAM_DELIMITER + configFilePropertyName
					+ LINUX_PARAM_SUFFIX + "'" + configFile + "' ";
			this.command = command + LINUX_PARAM_PREFIX + LEVEL + LINUX_PARAM_SUFFIX + logLevel + " " + LOG_REDIRECTOR
					+ " " + logDirectory + "_" + getCurrentTimeStamp() + LOG_FILE_EXT;

		}

		return this.command;

	}

	public void setCommand(String command) {
		this.command = command;
	}

	private String getCurrentTimeStamp() {
		String timeStamp = null;

		timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());

		return timeStamp;
	}

}
