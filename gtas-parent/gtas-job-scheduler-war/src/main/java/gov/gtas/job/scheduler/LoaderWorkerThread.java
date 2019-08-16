/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LoaderWorkerThread implements Runnable {
    @Autowired
    private LoaderScheduler loader;

    private String text;
    private String fileName;
    private String[] primeFlightKeyArray;
    private String primeFlightKey;
    private Semaphore semaphore;

    private BlockingQueue<Message<?>> queue;
    private ConcurrentMap<String, LoaderWorkerThread> map;

    private static final Logger logger = LoggerFactory.getLogger(LoaderWorkerThread.class);

    public LoaderWorkerThread() {
    }

    public LoaderWorkerThread(BlockingQueue<Message<?>> queue, ConcurrentMap<String, LoaderWorkerThread> map, String[] primeFlightKey) {
        this.queue = queue;
        this.map = map;
        this.primeFlightKeyArray = primeFlightKey;
    }

    @Override
    public void run() {
        while (true) {
            Message<?> msg = null;
            try {
                msg = queue.poll(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.error("error polling queue", e);
			}
            if (msg != null) {
            	try {
					MessageHeaders headers = msg.getHeaders();
					String filename = (String) headers.get("filename");
					if (filename != null) {
						this.fileName = filename;
					} else {
						this.fileName = UUID.randomUUID().toString();
					}
					this.text = msg.getPayload().toString();
					logger.debug(Thread.currentThread().getName() + " FileName = " + fileName);
					try {
						processCommand();
					} catch (Exception e) {
						logger.error("Catastrophic failure, uncaught exception would cause thread destruction without queue destruction causing memory leak; Rerouting process");
						logger.error("Catastrophic failure!", e);
					}
				} finally {
            		semaphore.release();
				}
            } else {
                boolean deletedSuccessfully = destroyQueue();
                if (deletedSuccessfully) {
					logger.info("Self-Destruct: Queue Removed, Thread Destroyed");
					break;
                }
            }
        }
    }

    private void processCommand() {
        loader.receiveMessage(text, fileName, primeFlightKeyArray);
    }

    private synchronized boolean destroyQueue() {
        boolean deleted = false;
        if (this.queue.isEmpty()) {
            //remove the reference from the parent map at the same time as ending the thread, dereferencing the queue and GC-ing it.
            logger.debug("Prime key being removed: " + Arrays.toString(this.primeFlightKeyArray));
            logger.debug("Queue inside this thread: " + this.queue.hashCode());
            this.map.remove(this.primeFlightKey);
            this.semaphore.release(this.queue.size());
            this.queue = null;
            deleted = true;
        }
        return deleted;
    }

    synchronized boolean addMessageToQueue(Message<?> message) throws InterruptedException {
        if (this.queue == null) {
            semaphore.release(); //for now just release the permit.
            return false;
        } else {
            this.queue.put(message);
        }
        return true;
    }

    @Override
    public String toString() {
        return Thread.currentThread().getName() + " Filename: " + fileName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BlockingQueue<Message<?>> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<Message<?>> queue) {
        this.queue = queue;
    }

    public String[] getPrimeFlightKeyArray() {
        return primeFlightKeyArray;
    }

    public void setPrimeFlightKeyArray(String[] primeFlightKey) {
        this.primeFlightKeyArray = primeFlightKey;
    }

    public ConcurrentMap<String, LoaderWorkerThread> getMap() {
        return map;
    }

    public void setMap(ConcurrentMap<String, LoaderWorkerThread> map) {
        this.map = map;
    }

    public String getPrimeFlightKey() {
        return primeFlightKey;
    }

    public void setPrimeFlightKey(String primeFlightKey) {
        this.primeFlightKey = primeFlightKey;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}
