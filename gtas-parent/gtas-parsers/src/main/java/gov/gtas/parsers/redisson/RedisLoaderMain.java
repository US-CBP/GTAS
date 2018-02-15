package gov.gtas.parsers.redisson;

import gov.gtas.jms.services.MessageSender;
import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.pnrgov.PnrGovParser;
import gov.gtas.parsers.util.FileUtils;
import gov.gtas.parsers.vo.PnrVo;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

@Component
public class RedisLoaderMain {

    private static File toBeProcessedPNRFile;
    private static Path incomingDir = FileSystems.getDefault().getPath("C:\\Message");
    private static Path outgoingDir = FileSystems.getDefault().getPath("C:\\Messageold");
    private static final String MESSAGE_SEGMENT_BEGIN = "UNH";
    private static final String MESSAGE_SEGMENT_END = "UNT";
    private static String messagePayload;
    private static Date transDate;
    private static EdifactLexer lexer;
    private MessageSender messageSender;

    public static void main(String[] args) {

        initializePNRFiles();

    }

    private static void initializePNRFiles() {

        RedissonFilter redFilter = new RedissonFilter();

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
                            ex.printStackTrace();
                        }
                        //processSingleFile(f, stats);
                        f.renameTo(new File(outgoingDir.toFile()
                                + File.separator + f.getName()));
                    });
            stream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static void getMessagePayloadAndTimestamp() throws Exception {
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


    private static boolean exceedsMaxSize(File f) {
        final long MAX_SIZE = 4294967295l; // raw column can accommodate 4294967295 bytes
        double numBytes = f.length();
        return numBytes > MAX_SIZE;
    }

    //@TODO -- will tackle this later, once we have redis filter logic locked down

//    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.exit(0);
//        }
//
//        RedisLoaderScheduler rds = new RedisLoaderScheduler();
//
//        try (ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(RedisLoaderConfig.class)) {
//            //loader = ctx.getBean(Loader.class);
//            RedisLoaderStatistics stats = new RedisLoaderStatistics();
//
//            rds.processFiles(new File(args[0]).toPath(), new File(args[1]).toPath(), stats);
//
////            writeAuditLog(ctx, stats);
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//    }


}