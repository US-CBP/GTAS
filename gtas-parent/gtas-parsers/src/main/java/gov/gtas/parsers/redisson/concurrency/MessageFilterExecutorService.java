package gov.gtas.parsers.redisson.concurrency;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MessageFilterExecutorService {

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
            e.printStackTrace();
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
            e.printStackTrace();
        }catch (Exception ex){
            ex.printStackTrace();
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
            e.printStackTrace();
        }

    }
}
