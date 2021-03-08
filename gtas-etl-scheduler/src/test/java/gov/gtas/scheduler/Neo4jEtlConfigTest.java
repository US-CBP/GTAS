package gov.gtas.scheduler;

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.gtas.configuration.Neo4jEtlConfig;

public class Neo4jEtlConfigTest {

	Neo4jEtlConfig appConfig = new Neo4jEtlConfig();

	@Before
	public void before() {

		appConfig.setConfigFile("FILE-1");
		appConfig.setConfigFilePropertyName("PROP-1");
		appConfig.setEtlName("ETL-1");
		appConfig.setExecInterval("INTERVAL-1");
		appConfig.setJobDir("JOB-1");
		appConfig.setLogDir("LOG-1");
		appConfig.setLogLevel("LOG-LEVEL-1");
		appConfig.setOpSystem("OS-1");
		appConfig.setPdiDir("PDI-DIR-1");
	}

	@Test
	public void TestAppConfig() {

		Assert.assertEquals("FILE-1", appConfig.getConfigFile());
		Assert.assertEquals("PROP-1", appConfig.getConfigFilePropertyName());
		Assert.assertEquals("ETL-1", appConfig.getEtlName());
		Assert.assertEquals("INTERVAL-1", appConfig.getExecInterval());
		Assert.assertEquals("JOB-1", appConfig.getJobDir());
		Assert.assertEquals("LOG-1", appConfig.getLogDir());
		Assert.assertEquals("LOG-LEVEL-1", appConfig.getLogLevel());
		Assert.assertEquals("OS-1", appConfig.getOpSystem());
		Assert.assertEquals("PDI-DIR-1", appConfig.getPdiDir());

	}

}
