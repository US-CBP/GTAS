/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.scheduler.thread;

public class CommandArgs {

	private String pdiDirectory;
	private String jobDirectory;
	private String logLevel;
	private String logDirectory;
	private String configFilePropertyName;
	private String configFile;
	private String opSystem;

	public String getPdiDirectory() {
		return pdiDirectory;
	}

	public void setPdiDirectory(String pdiDirectory) {
		this.pdiDirectory = pdiDirectory;
	}

	public String getJobDirectory() {
		return jobDirectory;
	}

	public void setJobDirectory(String jobDirectory) {
		this.jobDirectory = jobDirectory;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getLogDirectory() {
		return logDirectory;
	}

	public void setLogDirectory(String logDirectory) {
		this.logDirectory = logDirectory;
	}

	public String getConfigFilePropertyName() {
		return configFilePropertyName;
	}

	public void setConfigFilePropertyName(String configFilePropertyName) {
		this.configFilePropertyName = configFilePropertyName;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getOpSystem() {
		return opSystem;
	}

	public void setOpSystem(String opSystem) {
		this.opSystem = opSystem;
	}

}
