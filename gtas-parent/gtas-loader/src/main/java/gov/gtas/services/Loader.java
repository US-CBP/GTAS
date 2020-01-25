/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

import gov.gtas.model.MessageStatusEnum;
import gov.gtas.parsers.tamr.model.TamrPassengerSendObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.gtas.error.ErrorUtils;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.util.FileUtils;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.repository.MessageRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Loader {

	private static final Logger logger = LoggerFactory.getLogger(Loader.class);
	@Autowired
	private MessageRepository<Message> msgDao;

	@Autowired
	private ApisMessageService apisLoader;

	@Autowired
	private PnrMessageService pnrLoader;

	@Value("${tamr.enabled}")
	private Boolean tamrEnabled;

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
	@Transactional
	public ProcessedMessages processMessage(File f, String[] primeFlightKey) {
		String filePath = f.getAbsolutePath();
		MessageDto msgDto = new MessageDto();
		MessageLoaderService svc;
		List<String> rawMessages;
		try {
			if (exceedsMaxSize(f)) {
				throw new LoaderException("exceeds max file size");
			}
			msgDto.setPrimeFlightKey(primeFlightKey);

			byte[] raw = FileUtils.readSmallFile(filePath);
			String tmp = new String(raw, StandardCharsets.UTF_8);
			String text = ParseUtils.stripStxEtxHeaderAndFooter(tmp);

			if (text.contains("PAXLST")) {
				svc = apisLoader;
				msgDto.setMsgType("APIS");
			} else if (text.contains("PNRGOV")) {
				svc = pnrLoader;
				msgDto.setMsgType("PNR");
			} else {
				throw new LoaderException("unrecognized file type");
			}

			msgDto.setRawMsgs(svc.preprocess(text));

		} catch (Exception e) {
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

		int successMsgCount = 0;
		int failedMsgCount = 0;
		msgDto.setFilepath(filePath);
		rawMessages = msgDto.getRawMsgs();
		List<MessageStatus> messageStatuses = new ArrayList<>();
		List<TamrPassengerSendObject> tamrPassengerSendObjectList = new ArrayList<>();
		for (String rawMessage : rawMessages) {
			msgDto.setRawMsg(rawMessage);
			MessageDto parsedMessageDto = svc.parse(msgDto);
			if (parsedMessageDto.getMessageStatus().isSuccess()) {
				MessageInformation messageInformation = svc.load(parsedMessageDto);
				MessageStatus messageStatus = messageInformation.getMessageStatus();
				if (tamrEnabled) {
					tamrPassengerSendObjectList.addAll(messageInformation.getTamrPassengerSendObjects());
				}
				messageStatuses.add(messageStatus);
				if (messageStatus.isSuccess()) {
					successMsgCount++;
				} else {
					failedMsgCount++;
				}
			} else {
				messageStatuses.add(parsedMessageDto.getMessageStatus());
				failedMsgCount++;
			}
		}
		ProcessedMessages processedMessages = new ProcessedMessages();
		processedMessages.setProcessed(new int[] { successMsgCount, failedMsgCount });
		processedMessages.setMessageStatusList(messageStatuses);
		processedMessages.setTamrPassengerSendObjectList(tamrPassengerSendObjectList);
		return processedMessages;
	}

	private boolean exceedsMaxSize(File f) {
		final long MAX_SIZE = 4294967295L; // raw column can accommodate 4294967295 bytes
		double numBytes = f.length();
		return numBytes > MAX_SIZE;
	}
}
