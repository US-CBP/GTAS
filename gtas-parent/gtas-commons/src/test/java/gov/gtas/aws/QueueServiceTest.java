/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */

package gov.gtas.aws;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueueServiceTest {

	private QueueService queueService;
	private String url;
	private String region;

	@Before
	public void before() {
		url = "http://www.xxxx.com";
		region = "us-east-1";
	}

	@Test
	public void configureCredentialsTest() {
		queueService = new QueueService(url, region);
		queueService.configureCredentials(region, "AAAA", "BBB");
		Assert.assertTrue(queueService.isExternalAccessFlag());
	}

	@Test
	public void configureCredentialsNotUsedTest() {
		queueService = new QueueService(url, region);
		Assert.assertFalse(queueService.isExternalAccessFlag());
	}

}
