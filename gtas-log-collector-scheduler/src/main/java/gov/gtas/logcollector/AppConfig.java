package gov.gtas.logcollector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

	@Value("${execInterval}")
	private String execInterval;

	@Value("${opSystem}")
	private String opSystem;

	@Value("${pdiDir}")
	private String pdiDir;

	@Value("${jobDir}")
	private String jobDir;

	@Value("${logLevel}")
	private String logLevel;

	@Value("${logDir}")
	private String logDir;

	@Value("${configFilePropertyName}")
	private String configFilePropertyName;

	@Value("${configFile}")
	private String configFile;

	public String getOpSystem() {
		return opSystem;
	}

	public void setOpSystem(String opSystem) {
		this.opSystem = opSystem;
	}

	public String getPdiDir() {
		return pdiDir;
	}

	public void setPdiDir(String pdiDir) {
		this.pdiDir = pdiDir;
	}

	public String getJobDir() {
		return jobDir;
	}

	public void setJobDir(String jobDir) {
		this.jobDir = jobDir;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getLogDir() {
		return logDir;
	}

	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}

	public String getExecInterval() {
		return execInterval;
	}

	public void setExecInterval(String execInterval) {
		this.execInterval = execInterval;
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

}
