package gov.gtas.parsers.redisson;

import gov.gtas.jms.services.MessageSender;
import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.util.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
public class RedisLoader {

    private static final Logger logger = LoggerFactory.getLogger(RedisLoader.class);
    private static File toBeProcessedPNRFile;
    private static Path incomingDir = FileSystems.getDefault().getPath("C:\\Message");
    private static Path outgoingDir = FileSystems.getDefault().getPath("C:\\Messageold");
    private static final String MESSAGE_SEGMENT_BEGIN = "UNH";
    private static final String MESSAGE_SEGMENT_END = "UNT";
    private static String messagePayload;
    private static Date transDate;
    private static EdifactLexer lexer;
    private MessageSender messageSender;

//    public static void main(String[] args) {
//
//        initializePNRFiles();
//
//    }

    public void initializePNRFiles(RedissonClient client) {

        RedissonFilter redFilter = new RedissonFilter(client);

        DirectoryStream.Filter<Path> filter = entry -> {
            File f = entry.toFile();
            return !f.isHidden() && f.isFile();
        };
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                incomingDir, filter)) {

            final Iterator<Path> iterator = stream.iterator();
            List<File> files = new ArrayList<>();
            for (int i = 0; iterator.hasNext(); i++) {
                files.add(iterator.next().toFile());
            }
            Collections.sort(files,
                    LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
            files.stream().forEach(
                    f -> {
                        toBeProcessedPNRFile = f;
                        try {
                            getMessagePayloadAndTimestamp();
                            redFilter.redisObjectLookUpPersist(messagePayload,transDate);
                        } catch (Exception ex) {
                            logger.error("error processing file", ex);
                        }
                        //processSingleFile(f, stats);
                        f.renameTo(new File(outgoingDir.toFile()
                                + File.separator + f.getName()));
                    });
            stream.close();
        } catch (IOException ex) {
            logger.error("error initializing pnr files", ex);
        }
    }


    private void getMessagePayloadAndTimestamp() throws Exception {
        if (toBeProcessedPNRFile != null) {
            String filePath = toBeProcessedPNRFile.getAbsolutePath();
            List<String> rawMessages = null;

            if (exceedsMaxSize(toBeProcessedPNRFile)) {
                throw new Exception("exceeds max file size");
            }

            byte[] raw = FileUtils.readSmallFile(filePath);
            String tmp = new String(raw, StandardCharsets.US_ASCII);

            //             PnrVo pnrVo = new PnrGovParser().parse(tmp);
            //           transDate = pnrVo.getTransmissionDate();
            lexer = new EdifactLexer(tmp);
            messagePayload = lexer.getMessagePayload(MESSAGE_SEGMENT_BEGIN, MESSAGE_SEGMENT_END);
        }else {
            transDate = null;
            messagePayload = null;
        }
    }


    private boolean exceedsMaxSize(File f) {
        final long MAX_SIZE = 4294967295l; // raw column can accommodate 4294967295 bytes
        double numBytes = f.length();
        return numBytes > MAX_SIZE;
    }

    //@TODO -- will tackle this later, once we have redis filter logic locked down


}
