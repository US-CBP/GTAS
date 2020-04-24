/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler.controller;

import gov.gtas.job.scheduler.service.MessageReceiverService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageReceiverControllerTest {

	@Mock
	MessageReceiverService messageService;

	@InjectMocks
	MessageReceiverController messageReceiverController;

	@Test
	public void happyPath() {
		WebMessage webMessage = new WebMessage();
		webMessage.setMessagePayload("fooBarBaz");
		messageReceiverController.consumeMessage(webMessage);
		verify(messageService, times(1)).putMessageOnQueue(webMessage);
	}

	@Test
	public void nullMessage() {
		messageReceiverController.consumeMessage(null);
		verify(messageService, times(0)).putMessageOnQueue(any());
	}

	@Test
	public void nullPayload() {
		WebMessage webMessage = new WebMessage();
		messageReceiverController.consumeMessage(webMessage);
		verify(messageService, times(0)).putMessageOnQueue(webMessage);
	}
}
