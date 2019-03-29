/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import javax.jms.Session;

import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.model.Pnr;
import gov.gtas.repository.MessageStatusRepository;
import gov.gtas.repository.PnrRepository;
import gov.gtas.util.LobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LoaderMessageReceiver {
	private final LoaderQueueThreadManager queueManager;
	private final PnrRepository pnrRepository;
	private final MessageStatusRepository messageStatusRepository;

	private static final String GTAS_LOADER_QUEUE = "GTAS_LOADER_Q";
	static final Logger logger = LoggerFactory.getLogger(LoaderMessageReceiver.class);

	@Autowired
	public LoaderMessageReceiver(LoaderQueueThreadManager queueManager,
								 PnrRepository pnrRepository,
								 MessageStatusRepository messageStatusRepository) {
		this.queueManager = queueManager;
		this.pnrRepository = pnrRepository;
		this.messageStatusRepository = messageStatusRepository;
	}

	@JmsListener(destination = GTAS_LOADER_QUEUE, concurrency = "10")
	public void receiveMessagesForLoader(Message<?> message, Session session, javax.jms.Message msg)  {
		logger.debug("+++++++++++++++++IN LOADER QUEUE++++++++++++++++++++++++++++++++++++");
		MessageHeaders headers =  message.getHeaders();
		logger.debug("Application : headers received : {}", headers);
		logger.debug("Filename: "+headers.get("Filename"));
		try {
			queueManager.receiveMessages(message);
		} catch (Exception e) {
			logger.warn("Failed to parsed message. Is border crossing information corrupt? Error is: " + e);
			String failedMessageString = message.getPayload().toString();
			Pnr failedMessage = new Pnr();
			failedMessage.setCreateDate(new Date());
			failedMessage.setRaw(LobUtils.createClob(failedMessageString));
			failedMessage.setError(e.toString());
			Object fileName = headers.get("Filename");
			if (fileName != null) {
				failedMessage.setFilePath(fileName.toString());
			}
			failedMessage = pnrRepository.save(failedMessage);
			MessageStatus messageStatus = new MessageStatus(failedMessage.getId(), MessageStatusEnum.FAILED_PRE_PARSE);
			messageStatusRepository.save(messageStatus);
		}
	}
}