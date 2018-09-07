/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;

import java.io.File;

import gov.gtas.parsers.redisson.config.RedisLoaderConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
		CachingConfig.class, RedisLoaderConfig.class})
@PropertySource({ "classpath:redisloader.properties" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ApisMessageServiceIT extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private Loader svc;

	private File message;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		this.message = new File(classLoader.getResource(
				"apis-messages/airline2.edi").getFile());
	}

	@Test()
	@Transactional
	public void testRunService() {
		svc.processMessage(this.message, new String[]{"placeholder"});
	}
}
