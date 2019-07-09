/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.parsers.exception.ParseException;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes =  {TestCommonServicesConfig.class,
		CachingConfig.class})
@Rollback(true)
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
	public void testRunService() throws ParseException {
		svc.processMessage(this.message, new String[]{"placeholder"});
	}
	
	@Test
	@Transactional
	public void testProgressiveFlightWithDomesticContinuance() {
		this.message = new File(
				getClass().getClassLoader().getResource("apis-messages/multiple_flight_leg_apis.txt").getFile());
		svc.processMessage(this.message, new String[] { "JFK", "CDG", "QQ", "0827", "1218340800000", "1218412800000" });
	}
}
