package gov.gtas.parsers.redisson.concurrency;

import gov.gtas.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MessageFilterExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(MessageFilterExecutorService.class);

    ExecutorService executor = Executors.newFixedThreadPool(10);

    public void executeFile(File file) {

        executor.submit(() -> {
            new MessageFilterTask(file);
        });

        executor.shutdown();
        executor.shutdownNow();
        try {
            executor.awaitTermination(450, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("error executing file", e);
        }

    }

    public void executeFile(String file) {

        try {
        executor.submit(() -> {
            new MessageFilterTask(file);
        });

        executor.shutdown();
        executor.shutdownNow();

            executor.awaitTermination(450, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("error executing file.", e);
        }catch (Exception ex){
            logger.error("error executing file:", ex);
        }

    }

    public void execute() {

        executor.submit(() -> {
            new MessageFilterTask();
        });

        executor.shutdown();
        executor.shutdownNow();
        try {
            executor.awaitTermination(450, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("error executing file!", e);
        }

    }
}
