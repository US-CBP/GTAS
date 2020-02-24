/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler.service;

import gov.gtas.job.localFileIntake.InboundQMessageSender;
import gov.gtas.job.scheduler.controller.WebMessage;
import gov.gtas.services.LoaderRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageReceiverServiceImpl implements MessageReceiverService {

	private final InboundQMessageSender sender;

	@Value("${outbound.loader.jms.queue}")
	private String outboundLoaderQueue;

	public MessageReceiverServiceImpl(InboundQMessageSender sender) {
		this.sender = sender;
	}

	@Override
	public void putMessageOnQueue(WebMessage messagePayload) {
		if (messagePayload == null || messagePayload.getMessagePayload() == null) {
			throw new LoaderRuntimeException("Message payload or contents of message payload is null");
		} else {
			String fileContent = messagePayload.getMessagePayload();
			String fileName = UUID.randomUUID().toString();
			sender.sendFileContent(outboundLoaderQueue, fileContent, fileName);
		}
	}
}
