/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.error.ErrorUtils;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.summary.MessageAction;
import gov.gtas.summary.MessageSummary;
import gov.gtas.summary.MessageSummaryList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.util.FileUtils;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.repository.MessageRepository;

@Service
public class Loader {

	private static final Logger logger = LoggerFactory.getLogger(Loader.class);
	@Autowired
	private MessageRepository<Message> msgDao;

	@Autowired
	private ApisMessageService apisLoader;

	@Autowired
	private PnrMessageService pnrLoader;

	@Autowired
	private GenericLoading genericLoading;

	@Autowired
	private PendingHitDetailRepository pendingHitDetailRepository;

	@Value("${tamr.enabled}")
	private Boolean tamrEnabled;

	@Value("${additional.processing.enabled.passenger}")
	private Boolean additionalProcessing;


	/**
	 * Processes all the messages in a single file.
	 * 
	 * @param f
	 *            the file to process
	 * @param primeFlightKey
	 *            the key for determining which flight on the itinerary is the
	 *            current border crossing flight. A.K.A. Prime Flight
	 * @return array of integers containing loaded message count at index 0 and
	 *         failed message count at index 1.
	 */
	public ProcessedMessages processMessage(File f, String[] primeFlightKey) {
		String filePath = f.getAbsolutePath();
		MessageDto msgDto = new MessageDto();
		MessageLoaderService svc = null;
		boolean genericLoad = false;
		MessageSummaryList msl = null;
		String text = null;
		try {
			if (exceedsMaxSize(f)) {
				throw new LoaderException("exceeds max file size");
			}
			msgDto.setPrimeFlightKey(primeFlightKey);

			byte[] raw = FileUtils.readSmallFile(filePath);
			String tmp = new String(raw, StandardCharsets.UTF_8);
			text = ParseUtils.stripStxEtxHeaderAndFooter(tmp);
			String potentialMessageList = tmp.trim();
			if (maybeJSON(potentialMessageList)) {
				try {
					ObjectMapper om = new ObjectMapper();
					msl = om.readValue(potentialMessageList, MessageSummaryList.class);
					if ((msl.getMessageAction() == MessageAction.RAW || msl.getMessageAction() == MessageAction.RAW_APIS
					|| msl.getMessageAction() == MessageAction.RAW_PNR)) {
						// Raw messages are ALWAYS contained as the first message summary and are ALWAYS
						// pertaining to a file instead of a specific summary.
						// Raw messages will always have 1 and only 1 message.
						text = msl.getMessageSummaryList().get(0).getRawMessage();
					} else {
						genericLoad = true;
					}
				} catch (Exception ignored) {
					//We don't care if the message doesn't marshall. It might be a legitimate APIS/PNR edifact
				}
			}
			if (!genericLoad && text.contains("PAXLST")) {
				svc = apisLoader;
				msgDto.setMsgType("APIS");
			} else if (!genericLoad && text.contains("PNRGOV")) {
				svc = pnrLoader;
				msgDto.setMsgType("PNR");
			} else if (!genericLoad){
				throw new LoaderException("unrecognized file type");
			}
		} catch (LoaderException | IOException e) {
			logger.error("error processing message.", e);
			String stacktrace = ErrorUtils.getStacktrace(e);
			Message m = new Message();
			m.setError(stacktrace);
			m.setFilePath(filePath);
			m.setCreateDate(new Date());
			m = msgDao.save(m);
			MessageStatus messageStatus = new MessageStatus(m.getId(), MessageStatusEnum.FAILED_PRE_PROCESS);
			msgDto.setMessageStatus(messageStatus);
			ProcessedMessages processedMessages = new ProcessedMessages();
			List<MessageStatus> messageStatuses = new ArrayList<>();
			messageStatuses.add(messageStatus);
			processedMessages.setMessageStatusList(messageStatuses);
			return processedMessages;
		}


		List<MessageStatus> messageStatuses = new ArrayList<>();
		List<TamrPassenger> tamrPassengers = new ArrayList<>();
		List<MessageSummary> messageSummaries = new ArrayList<>();
		int successMsgCount = 0;
		int failedMsgCount = 0;
		if (genericLoad) {
			for (MessageSummary ms : msl.getMessageSummaryList()) {
				MessageInformation mi = genericLoading.load(ms, filePath);
				MessageStatus messageStatus = mi.getMessageStatus();
				if (messageStatus.isNoLoadingError()) {
					messageStatus.setMessageStatusEnum(MessageStatusEnum.LOADED);
					successMsgCount++;
					if (tamrEnabled) {
						tamrPassengers.addAll(mi.getTamrPassengers());
					}
					if (additionalProcessing) {
						messageSummaries.add(mi.getMessageSummary());
					}
					if (!mi.getPendingHitDetailsSet().isEmpty()) {
						pendingHitDetailRepository.saveAll(mi.getPendingHitDetailsSet());
					}
		 		} else {
					messageStatus.setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
					failedMsgCount++;
				}
				messageStatuses.add(messageStatus);
			}
		} else {
			msgDto.setRawMsgs(svc.preprocess(text));
			msgDto.setFilepath(filePath);
			List<String> rawMessages = msgDto.getRawMsgs();

			for (String rawMessage : rawMessages) {
				msgDto.setRawMsg(rawMessage);
				MessageDto parsedMessageDto = svc.parse(msgDto);
				if (parsedMessageDto.getMessageStatus().isNoLoadingError()) {
					MessageInformation messageInformation = svc.load(parsedMessageDto);
					MessageStatus messageStatus = messageInformation.getMessageStatus();
					if (tamrEnabled) {
						tamrPassengers.addAll(messageInformation.getTamrPassengers());
					}
					if (additionalProcessing) {
						messageSummaries.add(messageInformation.getMessageSummary());
					}
					messageStatuses.add(messageStatus);
					if (messageStatus.isNoLoadingError()) {
						successMsgCount++;
					} else {
						failedMsgCount++;
					}
				} else {
					messageStatuses.add(parsedMessageDto.getMessageStatus());
					failedMsgCount++;
				}
			}
		}
		ProcessedMessages processedMessages = new ProcessedMessages();
		processedMessages.setProcessed(new int[] { successMsgCount, failedMsgCount });
		processedMessages.setMessageStatusList(messageStatuses);
		processedMessages.setTamrPassengers(tamrPassengers);
		processedMessages.setMessageSummaries(messageSummaries);
		return processedMessages;
	}

	private boolean maybeJSON(String potentialMessageList) {
		return (potentialMessageList.startsWith("{") || potentialMessageList.startsWith("[")) &&
				(potentialMessageList.endsWith("}") || potentialMessageList.endsWith("]"));
	}

	private boolean exceedsMaxSize(File f) {
		final long MAX_SIZE = 4294967295L; // raw column can accommodate 4294967295 bytes
		double numBytes = f.length();
		return numBytes > MAX_SIZE;
	}
}
