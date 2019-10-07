/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

import gov.gtas.aws.HitNotificationConfig;
import gov.gtas.config.AsyncConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.model.HitsSummary;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AsyncConfig.class, TestCommonServicesConfig.class })
@Rollback(true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificationServiceIT {

	@Autowired
	private NotificatonService notificatonService;

	/***
	 * THIS TEST IS EXCLUDED FROM BEING RUN BY DEFAULT.
	 * 
	 * See excludes property of the maven-failsafe-plugin in
	 * gtas-parent/pom/.xml
	 * 
	 */

	@Test
	public void testSendHitNotification() {

		/**
		 * Update the arn name below to run the integration test manually
		 */
		final String arn = "";
		AmazonSNS amazonSNS = AmazonSNSClientBuilder.standard().build();
		HitsSummary s = new HitsSummary();
		s.setId(1L);
		HitNotificationConfig config = new HitNotificationConfig(amazonSNS, Arrays.asList(s), arn, "test", 4L);

		Set<String> messageIDs = this.notificatonService.sendHitNotifications(config);

		assertEquals(1, messageIDs.size());
	}
}
