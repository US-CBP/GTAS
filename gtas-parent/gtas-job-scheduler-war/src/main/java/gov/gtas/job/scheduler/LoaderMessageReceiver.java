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
import gov.gtas.services.LoaderException;
import gov.gtas.util.LobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import gov.gtas.job.scheduler.Utils;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;
import java.io.File;
import java.util.Date;

@Component
public class LoaderMessageReceiver {
	private final LoaderQueueThreadManager queueManager;
	private final PnrRepository pnrRepository;
	private final MessageStatusRepository messageStatusRepository;

	private static final String GTAS_LOADER_QUEUE = "GTAS_LOADER_Q";
	static final Logger logger = LoggerFactory.getLogger(LoaderMessageReceiver.class);

	@Value("${message.dir.processed}")
	private String processedstr;

	@Value("${message.dir.working}")
	private String workingstr;

	@Value("${message.dir.error}")
	private String errorstr;

	@Autowired
	public LoaderMessageReceiver(LoaderQueueThreadManager queueManager, PnrRepository pnrRepository,
			MessageStatusRepository messageStatusRepository) {
		this.queueManager = queueManager;
		this.pnrRepository = pnrRepository;
		this.messageStatusRepository = messageStatusRepository;
	}

	@JmsListener(destination = GTAS_LOADER_QUEUE, concurrency = "10")
	public void receiveMessagesForLoader(Message<?> message, Session session, javax.jms.Message msg) {
		final String filenameprop = "filename";
		MessageHeaders headers = message.getHeaders();
		String fileName = headers.get(filenameprop) != null ? headers.get(filenameprop).toString()
				: UUID.randomUUID().toString();
		String payload = message.getPayload().toString();

		logger.debug("+++++++++++++++++IN LOADER QUEUE++++++++++++++++++++++++++++++++++++");
		logger.debug("Application : headers received : {}", headers);
		logger.debug("Filename: " + fileName);

		File workingfile = Utils.writeToDisk(fileName, payload, workingstr);

		try {
			queueManager.receiveMessages(message);
		} catch (Exception e) {
			logger.warn("Failed to parsed message. Is border crossing information corrupt? Error is: " + e);
			Pnr failedMessage = new Pnr();
			failedMessage.setCreateDate(new Date());
			failedMessage.setRaw(LobUtils.createClob(payload));
			failedMessage.setError(e.toString());
			failedMessage.setFilePath(workingfile.getAbsolutePath());

			failedMessage = pnrRepository.save(failedMessage);
			MessageStatus messageStatus = new MessageStatus(failedMessage.getId(), MessageStatusEnum.FAILED_PRE_PARSE);
			messageStatusRepository.save(messageStatus);
		}
	}
}