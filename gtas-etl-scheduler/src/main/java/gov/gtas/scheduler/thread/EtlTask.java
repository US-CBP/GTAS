/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.scheduler.thread;

import static gov.gtas.scheduler.Constants.FILE_IND;
import static gov.gtas.scheduler.Constants.LEVEL;
import static gov.gtas.scheduler.Constants.LINUX_PARAM;
import static gov.gtas.scheduler.Constants.LINUX_PARAM_DELIMITER;
import static gov.gtas.scheduler.Constants.LINUX_PARAM_PREFIX;
import static gov.gtas.scheduler.Constants.LINUX_PARAM_SUFFIX;
import static gov.gtas.scheduler.Constants.LOG_FILE_EXT;
import static gov.gtas.scheduler.Constants.LOG_REDIRECTOR;
import static gov.gtas.scheduler.Constants.WINDOWS;
import static gov.gtas.scheduler.Constants.WINDOWS_CMD_CONST;
import static gov.gtas.scheduler.Constants.WINDOWS_PARAM;
import static gov.gtas.scheduler.Constants.WINDOWS_PARAM_DELIMITER;
import static gov.gtas.scheduler.Constants.WINDOWS_PARAM_PREFIX;
import static gov.gtas.scheduler.Constants.WINDOWS_PARAM_SUFFIX;

import java.text.SimpleDateFormat;
import java.util.Date;

import gov.gtas.scheduler.Constants;

public class EtlTask {

	public String getEtlCommand(CommandArgs commandArgs) {
		String etlCommand = "";

		if (commandArgs.getOpSystem() != null && commandArgs.getOpSystem().trim().equalsIgnoreCase(WINDOWS)) {

			etlCommand = WINDOWS_CMD_CONST + " " + commandArgs.getPdiDirectory() + "  " + " " + WINDOWS_PARAM_PREFIX
					+ FILE_IND + WINDOWS_PARAM_DELIMITER + commandArgs.getJobDirectory() + "  ";
			etlCommand = etlCommand + "\"" + WINDOWS_PARAM_PREFIX + WINDOWS_PARAM + WINDOWS_PARAM_DELIMITER
					+ commandArgs.getConfigFilePropertyName() + WINDOWS_PARAM_SUFFIX + commandArgs.getConfigFile()
					+ "\"  ";
			etlCommand = etlCommand + WINDOWS_PARAM_PREFIX + LEVEL + WINDOWS_PARAM_DELIMITER + commandArgs.getLogLevel()
					+ " " + LOG_REDIRECTOR + " " + commandArgs.getLogDirectory() + "_" + getCurrentTimeStamp()
					+ LOG_FILE_EXT;

		} else if (commandArgs.getOpSystem() != null
				&& commandArgs.getOpSystem().trim().equalsIgnoreCase(Constants.LINUX)) {

			etlCommand = commandArgs.getPdiDirectory() + " " + LINUX_PARAM_PREFIX + FILE_IND + LINUX_PARAM_SUFFIX + "'"
					+ commandArgs.getJobDirectory() + "' ";
			etlCommand = etlCommand + LINUX_PARAM_PREFIX + LINUX_PARAM + LINUX_PARAM_DELIMITER
					+ commandArgs.getConfigFilePropertyName() + LINUX_PARAM_SUFFIX + "'" + commandArgs.getConfigFile()
					+ "' ";
			etlCommand = etlCommand + LINUX_PARAM_PREFIX + LEVEL + LINUX_PARAM_SUFFIX + commandArgs.getLogLevel() + " "
					+ LOG_REDIRECTOR + " " + commandArgs.getLogDirectory() + "_" + getCurrentTimeStamp() + LOG_FILE_EXT;

		}

		return etlCommand;

	}

	private String getCurrentTimeStamp() {
		String timeStamp = null;

		timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());

		return timeStamp;
	}

}
