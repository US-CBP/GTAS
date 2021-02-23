/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:config/run/scheduler.properties")
@ConfigurationProperties
public abstract class EtlConfig {

	@Value("${opSystem}")
	private String opSystem;

	@Value("${pdiDir}")
	private String pdiDir;

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

}
