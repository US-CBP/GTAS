package gov.gtas.logcollector.thread;

import static gov.gtas.logcollector.Constants.FILE_IND;
import static gov.gtas.logcollector.Constants.LEVEL;
import static gov.gtas.logcollector.Constants.LINUX_PARAM;
import static gov.gtas.logcollector.Constants.LINUX_PARAM_DELIMITER;
import static gov.gtas.logcollector.Constants.LINUX_PARAM_PREFIX;
import static gov.gtas.logcollector.Constants.LINUX_PARAM_SUFFIX;
import static gov.gtas.logcollector.Constants.LOG_FILE_EXT;
import static gov.gtas.logcollector.Constants.LOG_REDIRECTOR;
import static gov.gtas.logcollector.Constants.WINDOWS;
import static gov.gtas.logcollector.Constants.WINDOWS_CMD_CONST;
import static gov.gtas.logcollector.Constants.WINDOWS_PARAM;
import static gov.gtas.logcollector.Constants.WINDOWS_PARAM_DELIMITER;
import static gov.gtas.logcollector.Constants.WINDOWS_PARAM_PREFIX;
import static gov.gtas.logcollector.Constants.WINDOWS_PARAM_SUFFIX;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.gtas.logcollector.AppConfig;
import gov.gtas.logcollector.Constants;

@Component
public class ExecutableCommand {
	
	@Autowired
	private AppConfig appConfig;
	
	private String command;
	
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
