/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.gtas.scheduler.thread.CommandArgs;
import gov.gtas.scheduler.thread.EtlTask;

public class EtlTaskTest {

	EtlTask etlTask = new EtlTask();
	CommandArgs commandArgs = new CommandArgs();

	@Before
	public void before() {

		commandArgs.setConfigFile("/gtas-etl/config/config.properties");
		commandArgs.setConfigFilePropertyName("ETL-CONFIG-FILE-NAME");
		commandArgs.setJobDirectory("/gtas-etl/job");
		commandArgs.setLogDirectory("/gtas-etl/log");
		commandArgs.setLogLevel("Basic");
		commandArgs.setOpSystem("Linux");
		commandArgs.setPdiDirectory("/opt/pentaho/data-integration");
	}

	@Test
	public void TestAppConfig() {
		String etlCommand = etlTask.getEtlCommand(commandArgs);
		String expected = "/opt/pentaho/data-integration -file='/gtas-etl/job' -param:ETL-CONFIG-FILE-NAME='/gtas-etl/config/config.properties' -level=Basic >> /gtas-etl/log_";
		expected = expected + getCurrentTimeStamp() + ".log";
		Assert.assertEquals(expected, etlCommand);

	}

	private String getCurrentTimeStamp() {
		String timeStamp = null;

		timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());

		return timeStamp;
	}

}
