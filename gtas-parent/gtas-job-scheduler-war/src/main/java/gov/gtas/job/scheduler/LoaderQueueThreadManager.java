/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import gov.gtas.job.config.JobSchedulerConfig;
import gov.gtas.summary.EventIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import gov.gtas.parsers.exception.ParseException;

@Component
public class LoaderQueueThreadManager {

    private final ApplicationContext ctx;

    private static final int DEFAULT_THREADS_ON_LOADER = 5;

    private ExecutorService exec;

    private static final int DEFAULT_PERMITS = 5000;

    private static ConcurrentMap<String, LoaderWorkerThread> bucketBucket = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(LoaderQueueThreadManager.class);

    private final Semaphore semaphore;

    private final EventIdentifierFactory eventIdentifierFactory;

    @Autowired
    public LoaderQueueThreadManager(ApplicationContext ctx,
                                    @Value("${loader.permits}") Integer loaderPermits, JobSchedulerConfig jobSchedulerConfig, EventIdentifierFactory eventIdentifierFactory) {

        this.ctx = ctx;
        this.eventIdentifierFactory = eventIdentifierFactory;
        if (loaderPermits == null || loaderPermits.equals(0)) {
            logger.warn("no permits set up, using default of " + DEFAULT_PERMITS);
            this.semaphore = new Semaphore(DEFAULT_PERMITS);
        } else {
            this.semaphore = new Semaphore(loaderPermits);
        }
        /*
         * Fail safe and fall back to 5 number of threads when the database
         * configuration is set incorrectly
         */
        int maxNumOfThreads = DEFAULT_THREADS_ON_LOADER;

        try {

            maxNumOfThreads = jobSchedulerConfig.getThreadsOnLoader();

        } catch (Exception e) {
            logger.error(String.format(
                    "Failed to load application configuration: THREADS_ON_LOADER from application.properties... Number of threads set to use %1$s",
                    DEFAULT_THREADS_ON_LOADER));
        }

        this.exec = Executors.newFixedThreadPool(maxNumOfThreads);
    }

    EventIdentifier receiveMessages(Message<?> message) throws ParseException, InterruptedException {

        EventIdentifier eventIdentifier = eventIdentifierFactory.createEventIdentifier(message);
        String[] primeFlightKeyArray = eventIdentifier.getIdentifierArrayList().toArray(new String[0]);
        // Construct label for individual buckets out of concatenated array values from
        // prime flight key generation
        String primeFlightKey = eventIdentifier.getIdentifier();
        // bucketBucket is a bucket of buckets. It holds a series of queues that are
        // processed sequentially.
        // This solves the problem where-in which we cannot run the risk of trying to
        // save/update the same flight at the same time. This is done
        // by shuffling all identical flights into the same queue in order to be
        // processed sequentially. However, by processing multiple
        // sequential queues at the same time, we in essence multi-thread the process
        // for all non-identical prime flights
        AtomicBoolean firstMessage = new AtomicBoolean(false);
        logger.debug("Available permits: " + semaphore.availablePermits());

        // Here we will acquire a lock as a new message has come in. The Loader Worker
        // will release the lock when it is done processing the message.
        semaphore.acquire();
        LoaderWorkerThread primeFlightWorkerThread = bucketBucket.computeIfAbsent(primeFlightKey, m -> {
            LoaderWorkerThread worker = ctx.getBean(LoaderWorkerThread.class);
            logger.info("New Queue Created For Prime Flight: " + primeFlightKey);
            worker.setQueue(new ArrayBlockingQueue<>(1024));
            worker.setMap(bucketBucket); // give map reference and key in order to kill queue later
            worker.setPrimeFlightKeyArray(primeFlightKeyArray);
            worker.setPrimeFlightKey(primeFlightKey);
            worker.setSemaphore(semaphore);
            firstMessage.set(true);
            return worker;
        });
        // There solves the race condition in which the queue is being torn
        // down/destroyed and then the message is
        // added to the queue.
        // addMessageToQueue returns false when the thread has is being destroyed.
        // If the worker has been destroyed then re-run receiveMessage, which will
        // create a new worker thread
        // and process correctly.
        boolean addedToQueue = primeFlightWorkerThread.addMessageToQueue(message);
        if (!addedToQueue) {
            logger.error("MESSAGE NOT PROCESSED-REPROCESSING");
            receiveMessages(message);
            return eventIdentifier;
        }
        if (firstMessage.get()) {
            exec.execute(primeFlightWorkerThread);
        }
        return eventIdentifier;
    }
}
