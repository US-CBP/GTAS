package gov.gtas.parsers.redisson.concurrency;

import gov.gtas.model.Message;
import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.redisson.RedissonFilter;
import gov.gtas.parsers.util.FileUtils;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.parsers.vo.MessageVo;
import gov.gtas.parsers.vo.PnrVo;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class MessageFilterTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MessageFilterTask.class);

    private File fileToBeProcessed;
    private static final String MESSAGE_SEGMENT_BEGIN="UNH";
    private static final String MESSAGE_SEGMENT_END="UNT";
    private String fileToBeProcessedString;
    private RedissonClient redissonClient;

    public MessageFilterTask() {
    }

    public MessageFilterTask(File file) {
        this.fileToBeProcessed = file;
    }

    public MessageFilterTask(String file) {
        this.fileToBeProcessedString = file;
    }

    public MessageFilterTask(String file, RedissonClient client) {
        this.fileToBeProcessedString = file;
        this.redissonClient = client;
    }


    @Override
    public void run() {
        if(this.fileToBeProcessedString == null){return;}
        logger.info("Thread "+this.toString()+" began processing");
        EdifactLexer lexer = new EdifactLexer(this.fileToBeProcessedString);
        String payload = lexer.getMessagePayload(MESSAGE_SEGMENT_BEGIN, MESSAGE_SEGMENT_END);
        RedissonFilter filter = new RedissonFilter(redissonClient);
        filter.redisObjectLookUpPersist(payload, new Date());
    }

    public void run(String payload){}


    private int[] parseFiles(File f) {
        String filePath = f.getAbsolutePath();
        EdifactParser<PnrVo> parser = null;
        List<String> rawMessages = null;
        int successMsgCount = 0;
        int failedMsgCount = 0;

        try {
            if (exceedsMaxSize(f)) {
                throw new Exception("exceeds max file size");
            }

            byte[] raw = FileUtils.readSmallFile(filePath);
            String tmp = new String(raw, StandardCharsets.US_ASCII);
            String text = ParseUtils.stripStxEtxHeaderAndFooter(tmp);

            //rawMessages = svc.preprocess(text);

        } catch (Exception e) {
            logger.error("error parsing files!", e);;;
//            String stacktrace = ErrorUtils.getStacktrace(e);
//            Message m = new Message();
//            m.setError(stacktrace);
//            m.setFilePath(filePath);
//            m.setCreateDate(new Date());
//            m.setStatus(MessageStatus.FAILED_PARSING);
//            msgDao.save(m);
//            return null;
        }

//        indexer.initClient();
//        if (indexer.isDown()) {
//            svc.setUseIndexer(false);
//        } else {
//            svc.setUseIndexer(true);
//        }

//
//        svc.setFilePath(filePath);
//        for (String rawMessage : rawMessages) {
//            MessageVo parsedMessage = svc.parse(rawMessage);
//            if (parsedMessage != null && svc.load(parsedMessage)) {
//                successMsgCount++;
//            } else {
//                failedMsgCount++;
//            }
//        }
        return new int[]{successMsgCount, failedMsgCount};
    }

    private boolean exceedsMaxSize(File f) {
        final long MAX_SIZE = 4294967295l; // raw column can accommodate 4294967295 bytes
        double numBytes = f.length();
        return numBytes > MAX_SIZE;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .toString();
    }
}
