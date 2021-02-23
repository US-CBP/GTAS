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

import gov.gtas.configuration.ReportEtlConfig;
import gov.gtas.scheduler.Constants;

@Component
public class ReportEtlRunnableTask extends EtlTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ReportEtlRunnableTask.class);
	private static boolean isProcessing = false;

	@Autowired
	private ReportEtlConfig reportAppConfig;

	private String command;

	@Override
	public void run() {

		if (!isProcessing && reportAppConfig.getEnableEtl()) {
			log.info(" COMMAND LINE: " + this.command);
			executeOnCommandLine(this.command);
		} else if (!reportAppConfig.getEnableEtl()) {
			log.info("\n");
			log.info("!!!!! THE SCHEDULER FOR THE REPORT ETL JOB IS DISABLED !!!!! See scheduelr.properties file. ");
			log.info("\n");

		}
	}

	public synchronized void executeOnCommandLine(String command) {
		int exitValue = -1;
		try {
			log.info("\n");
			log.info("==== ==== ==== ##  STARTING {} JOB ## ==== ==== ====", reportAppConfig.getEtlName());

			Runtime runtime = Runtime.getRuntime();
			Process process = null;
			if (reportAppConfig.getOpSystem() != null
					&& reportAppConfig.getOpSystem().trim().equalsIgnoreCase(Constants.LINUX)) {
				process = runtime.exec(new String[] { LINUX_CMD_CONST, "-c", command });
			} else if (reportAppConfig.getOpSystem() != null
					&& reportAppConfig.getOpSystem().trim().equalsIgnoreCase(WINDOWS)) {
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
					reportAppConfig.getEtlName(), exitValue);
			log.info("\n");

		} catch (Exception e) {
			String errorInfo = "An error has occurred when launching  " + reportAppConfig.getEtlName()
					+ " from GTAS ETL Scheduler.";
			log.error(errorInfo);
		}

	}

	@PostConstruct
	public String getCommand() {

		CommandArgs commandArgs = new CommandArgs();
		commandArgs.setConfigFile(reportAppConfig.getConfigFile());
		commandArgs.setConfigFilePropertyName(reportAppConfig.getConfigFilePropertyName());
		commandArgs.setJobDirectory(reportAppConfig.getJobDir());
		commandArgs.setLogDirectory(reportAppConfig.getLogDir());
		commandArgs.setLogLevel(reportAppConfig.getLogLevel());
		commandArgs.setOpSystem(reportAppConfig.getOpSystem());
		commandArgs.setPdiDirectory(reportAppConfig.getPdiDir());
		this.command = this.getEtlCommand(commandArgs);

		return this.command;

	}

	public void setCommand(String command) {
		this.command = command;
	}

}
