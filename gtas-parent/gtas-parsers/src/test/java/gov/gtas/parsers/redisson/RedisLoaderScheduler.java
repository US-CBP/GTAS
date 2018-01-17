/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.redisson;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RedisLoaderScheduler {


    @Value("${message.dir.origin}")
    private String messageOriginDir;

    @Value("${message.dir.processed}")
    private String messageProcessedDir;

    @Scheduled(fixedDelayString = "${loader.fixedDelay.in.milliseconds}", initialDelayString = "${loader.initialDelay.in.milliseconds}")
    public void jobScheduling() {
        Path dInputDir = Paths.get(messageOriginDir).normalize();
        File inputDirFile = dInputDir.toFile();
        Path dOutputDir = Paths.get(messageProcessedDir).normalize();
        File outputDirFile = dOutputDir.toFile();
        RedisLoaderStatistics stats = new RedisLoaderStatistics();

        processFiles(dInputDir, dOutputDir, stats);

    }


    private void processFiles(Path incomingDir,Path outgoingDir, RedisLoaderStatistics stats) {

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

}
