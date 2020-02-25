/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler.services;

import gov.gtas.job.localFileIntake.InboundQMessageSender;
import gov.gtas.job.scheduler.controller.WebMessage;
import gov.gtas.job.scheduler.service.MessageReceiverServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageReceiverServiceImplTest {

	@Mock
	private InboundQMessageSender sender;

	@InjectMocks
	private MessageReceiverServiceImpl messageReceiverService;

	@Test(expected = IllegalArgumentException.class)
	public void testNullMessage() {
		messageReceiverService.putMessageOnQueue(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullPayload() {
		messageReceiverService.putMessageOnQueue(new WebMessage());
	}

	@Test
	public void testHappyPath() {
		WebMessage messagePayload = new WebMessage();
		messagePayload.setMessagePayload("foobarbaz");
		messageReceiverService.putMessageOnQueue(messagePayload);
		verify(sender, times(1)).sendFileContent(any(), eq(messagePayload.getMessagePayload()), any());
	}

}
