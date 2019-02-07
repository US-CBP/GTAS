/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.gtas.model.MessageStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.error.ErrorUtils;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.util.FileUtils;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.repository.MessageRepository;
import gov.gtas.services.search.ElasticHelper;

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
    protected ElasticHelper indexer;

    private boolean isElasticEnabled = true; //TODO: put this in property file
    
    /**
     * Processes all the messages in a single file.
     * 
     * @param f
     *            the file to process
     * @param primeFlightKey
     * 			  the key for determining which flight on the itinerary is the current border crossing flight. A.K.A. Prime Flight
     * @return array of integers containing loaded message count at index 0 and
     *         failed message count at index 1.
     */
    public ProcessedMessages processMessage(File f, String[] primeFlightKey) {
        String filePath = f.getAbsolutePath();
        MessageDto msgDto = new MessageDto();
        MessageLoaderService svc = null;
        List<String> rawMessages = null;
        try {
            if (exceedsMaxSize(f)) {
                throw new LoaderException("exceeds max file size");
            }
            msgDto.setPrimeFlightKey(primeFlightKey);
            
            byte[] raw = FileUtils.readSmallFile(filePath);
            String tmp = new String(raw, StandardCharsets.US_ASCII);
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
            MessageStatus messageStatus = new MessageStatus(m.getId(), MessageStatusEnum.FAILED_PARSING);
            msgDto.setMessageStatus(messageStatus);
            ProcessedMessages processedMessages = new ProcessedMessages();
            List<MessageStatus> messageStatuses = new ArrayList<>();
            messageStatuses.add(messageStatus);
            processedMessages.setMessageStatusList(messageStatuses);
            return processedMessages;
        }
        if (isElasticEnabled){
        	indexer.initClient();
			if (indexer.isDown()) {
				svc.setUseIndexer(false);
			} else {
				svc.setUseIndexer(true);
			}
    	}
        
        int successMsgCount = 0;
        int failedMsgCount = 0;
        msgDto.setFilepath(filePath);
        rawMessages = msgDto.getRawMsgs();
        List<MessageStatus> messageStatuses = new ArrayList<>();
        for (String rawMessage : rawMessages) {
        	msgDto.setRawMsg(rawMessage);
            MessageDto parsedMessageDto = svc.parse(msgDto);
            if (parsedMessageDto != null && parsedMessageDto.getMsgVo() != null) {
                MessageStatus messageStatus = svc.load(parsedMessageDto);
                messageStatuses.add(messageStatus);
                if (messageStatus.isSuccess()) {
                successMsgCount++;
            } else {
                failedMsgCount++;
            }
            } else {
                failedMsgCount++;
        }
        }
        ProcessedMessages processedMessages = new ProcessedMessages();
        processedMessages.setProcessed(new int[] {successMsgCount, failedMsgCount});
        processedMessages.setMessageStatusList(messageStatuses);
        return processedMessages;
    }

    private boolean exceedsMaxSize(File f) {
        final long MAX_SIZE = 4294967295L; // raw column can accommodate 4294967295 bytes
        double numBytes = f.length();
        return numBytes > MAX_SIZE;
    }
}
