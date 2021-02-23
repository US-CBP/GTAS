/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:config/scheduler/scheduler.properties")
@ConfigurationProperties(prefix = "report")
public class ReportEtlConfig extends EtlConfig {

	private Boolean enableEtl;

	private String etlName;

	private String execInterval;

	private String jobDir;

	private String logLevel;

	private String logDir;

	private String configFilePropertyName;

	private String configFile;

	public Boolean getEnableEtl() {
		return enableEtl;
	}

	public void setEnableEtl(Boolean enableEtl) {
		this.enableEtl = enableEtl;
	}

	public String getEtlName() {
		return etlName;
	}

	public void setEtlName(String etlName) {
		this.etlName = etlName;
	}

	public String getExecInterval() {
		return execInterval;
	}

	public void setExecInterval(String execInterval) {
		this.execInterval = execInterval;
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
