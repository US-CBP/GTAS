/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import javax.jms.Session;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.job.wrapper.MessageWrapper;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.summary.EventIdentifier;
import gov.gtas.summary.MessageAction;
import gov.gtas.services.jms.AdditionalProcessingMessageSender;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.model.Pnr;
import gov.gtas.repository.MessageStatusRepository;
import gov.gtas.repository.PnrRepository;
import gov.gtas.summary.MessageSummaryList;
import gov.gtas.util.LobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;
import java.io.File;
import java.util.Date;

@Component
public class LoaderMessageReceiver {
	private final LoaderQueueThreadManager queueManager;
	private final PnrRepository pnrRepository;
	private final MessageStatusRepository messageStatusRepository;
	private final AdditionalProcessingMessageSender apms;

	static final Logger logger = LoggerFactory.getLogger(LoaderMessageReceiver.class);

	@Value("${message.dir.processed}")
	private String processedstr;

	@Value("${message.dir.working}")
	private String workingstr;

	@Value("${message.dir.error}")
	private String errorstr;

	@Value("${additional.processing.enabled.raw}")
	private Boolean additionalProcessingOn;

	@Value("${additional.processing.pnr}")
	private Boolean proccessPnr;

	@Value("${additional.processing.apis}")
	private Boolean proccessApis;

	@Value("${additional.processing.other}")
	private List<String> other;

	@Value("${additional.processing.queue}")
	private String addProcessQueue;

	private PendingHitDetailRepository pendingHitDetailRepository;

	@Autowired
	public LoaderMessageReceiver(LoaderQueueThreadManager queueManager,
								 PnrRepository pnrRepository,
								 MessageStatusRepository messageStatusRepository,
								 AdditionalProcessingMessageSender apms,
								 PendingHitDetailRepository pendingHitDetailRepository) {
		this.queueManager = queueManager;
		this.pnrRepository = pnrRepository;
		this.messageStatusRepository = messageStatusRepository;
		this.apms = apms;
		this.pendingHitDetailRepository = pendingHitDetailRepository;
	}

	@JmsListener(destination ="${inbound.loader.jms.queue}", concurrency = "10")
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
			MessageWrapper mw = new MessageWrapper(message, fileName);
			EventIdentifier eventIdentifier = queueManager.receiveMessages(mw);
			if (additionalProcessingOn && (eventIdentifier.getEventType().equals("PNR") && proccessPnr
					|| eventIdentifier.getEventType().equals("APIS") && proccessApis ||
			addProcessQueue != null && addProcessQueue.contains(eventIdentifier.getEventType()))) {
				MessageAction messageAction = eventIdentifier.getEventType().equals("APIS") ? MessageAction.RAW_APIS : MessageAction.RAW_PNR;

				String rawMessage;
				if (mw.getFromMessageInfo()) {
					ObjectMapper om = new ObjectMapper();
					MessageSummaryList msl = om.readValue((String)message.getPayload(), MessageSummaryList.class);
					if (eventIdentifier.getReceiverCanForward() != null && eventIdentifier.getReceiverCanForward()) {
						eventIdentifier.setReceiverCanForward(false); // only forward a message once.
						rawMessage = msl.getMessageSummaryList().get(0).getRawMessage();
						apms.sendRawMessage(addProcessQueue, rawMessage, eventIdentifier, messageAction);
					}
				} else {
					rawMessage = (String)message.getPayload();
					apms.sendRawMessage(addProcessQueue, rawMessage, eventIdentifier, messageAction);
				}
			}
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

	@JmsListener(destination = "${inbound.loader.pendinghits.queue}", concurrency = "10")
	public void pendingHitEndPoint(Message<?> message, Session session, javax.jms.Message msg) {
		String payload = message.getPayload().toString();
		ObjectMapper om = new ObjectMapper();
		try {
			PendingHitDetails phd = om.readValue(payload, PendingHitDetails.class);
			pendingHitDetailRepository.save(phd);
		} catch (Exception e) {
			logger.error("Unable to save pending hit detail! Error: " + e);
		}
	}
}