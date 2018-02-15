package gov.gtas.job.scheduler;

import gov.gtas.parsers.redisson.RedisLoader;
import gov.gtas.parsers.redisson.RedisLoaderStatistics;
import gov.gtas.parsers.redisson.jms.InboundQMessageSender;
import gov.gtas.parsers.util.FileUtils;
import gov.gtas.services.LoaderException;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

@Component
public class RedisLoaderScheduler {

    @Value("${message.dir.origin}")
    private String messageOriginDir;

    @Value("${message.dir.processed}")
    private String messageProcessedDir;

    @Value("${redis.connection.string}")
    private String redisConnectionString;

    @Value("${inbound.loader.jms.queue}")
    private String inboundLoaderQueue;

    @Value("${outbound.loader.jms.queue}")
    private String outboundLoaderQueue;

    @Autowired
    RedisLoader loader;

    @Autowired
    private InboundQMessageSender sender;

    private static RedissonClient client;

    @Scheduled(fixedDelayString = "${loader.fixedDelay.in.milliseconds}", initialDelayString = "${loader.initialDelay.in.milliseconds}")
    public void jobScheduling() throws IOException {
        Path dInputDir = Paths.get(messageOriginDir).normalize();
        File inputDirFile = dInputDir.toFile();
        Path dOutputDir = Paths.get(messageProcessedDir).normalize();
        File outputDirFile = dOutputDir.toFile();
        RedisLoaderStatistics stats = new RedisLoaderStatistics();
        Config config = new Config();
        //config.setTransportMode(TransportMode.EPOLL);
        config.useSingleServer().setAddress(redisConnectionString);

        client = Redisson.create(config);

        //processFiles(dInputDir, dOutputDir, stats);
        processAndQFiles(dInputDir, dOutputDir, stats);
        //loader.initializePNRFiles(client);

    }


    public void processFiles(Path incomingDir,Path outgoingDir, RedisLoaderStatistics stats) {

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
                        //processSingleFile(f, stats);
                        f.renameTo(new File(outgoingDir.toFile()
                                + File.separator + f.getName()));
                    });
            stream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void processAndQFiles(Path incomingDir,
                                 Path outgoingDir,
                                 RedisLoaderStatistics stats) {

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

//            Collections.sort(files,
//                    LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
            files.stream().forEach(
                    f -> {
                        try {
                            pushToInboundQueue(f);
                        }catch (LoaderException lex) {
                            lex.printStackTrace();
                        }catch (IOException ioex) {
                            ioex.printStackTrace();
                        }
                        f.renameTo(new File(outgoingDir.toFile()
                                + File.separator + f.getName()));
                        f.delete();
                    });
            stream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    private void pushToInboundQueue(File f) throws LoaderException, IOException{
        String filePath = f.getAbsolutePath();

            if (exceedsMaxSize(f)) {
                throw new LoaderException("exceeds max file size");
            }

            byte[] raw = FileUtils.readSmallFile(filePath);
            String tmp = new String(raw, StandardCharsets.US_ASCII);

            sender.sendFileContent(inboundLoaderQueue, tmp, f.getName());
        }


    private boolean exceedsMaxSize(File f) {
        final long MAX_SIZE = 4294967295l; // raw column can accommodate 4294967295 bytes
        double numBytes = f.length();
        return numBytes > MAX_SIZE;
    }

}
