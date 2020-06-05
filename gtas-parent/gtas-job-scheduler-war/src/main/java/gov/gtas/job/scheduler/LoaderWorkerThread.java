/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.UnexpectedRollbackException;

@Component
@Scope("prototype")
public class LoaderWorkerThread implements Runnable {
	@Autowired
	private LoaderScheduler loader;

	@Value("${loader.worker.thread.timeout}")
	private Integer timeout;
	private String text;
	private String fileName;
	private String[] primeFlightKeyArray;
	private String primeFlightKey;
	private Semaphore semaphore;
	int lockExceptionCount = 0;

	private BlockingQueue<Message<?>> queue;
	private ConcurrentMap<String, LoaderWorkerThread> map;

	private static final Logger logger = LoggerFactory.getLogger(LoaderWorkerThread.class);

	public LoaderWorkerThread() {
	}

	public LoaderWorkerThread(BlockingQueue<Message<?>> queue, ConcurrentMap<String, LoaderWorkerThread> map,
			String[] primeFlightKey) {
		this.queue = queue;
		this.map = map;
		this.primeFlightKeyArray = primeFlightKey;
	}

	@Override
	public void run() {
		while (true) {
			Message<?> msg = null;
			try {
				msg = queue.poll(timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				logger.error("error polling queue", e);
				Thread.currentThread().interrupt();
			}
			if (msg != null) {
				try {

					MessageHeaders headers = msg.getHeaders();
					fileName = (String) headers.get("filename");

					logger.debug(Thread.currentThread().getName() + " FileName = " + fileName);
					try {
						processCommand();
						lockExceptionCount = 0; // reset tries on a per file bases.
					} catch (UnexpectedRollbackException | PessimisticLockingFailureException plfe) {
						/*
						 * The loader is in a long transaction. Occasionally there is contention for the
						 * locks. Breaking apart the transactions is not feasible at this time due to
						 * performance issues. As this is a rare issue we catch the
						 * PessimisticLockingFailureException and just retry the message.
						 */
						lockExceptionCount++;
						if (lockExceptionCount > 3) { // Arbitrarily pick 3 attempts (1.5 minutes or so) of no progress
														// before killing message.
							logger.error(
									"Unable to process message after 3 lock exceptions! There is probably an issue with "
											+ "the data structure!!! Prime flight: " + primeFlightKey,
									plfe);
						} else {
							logger.warn("LOCK FAIL COUNT  " + lockExceptionCount + " FOR PRIME FLIGHT KEY:"
									+ primeFlightKey + "PUTTING MESSAGE BACK ON QUEUE!");
							queue.add(msg);
						}
					} catch (Exception e) {
						logger.error("Catastrophic failure, " + "uncaught exception would cause"
								+ " thread destruction without queue destruction "
								+ "causing memory leak. Process Aborted! ", e);
					}
				} finally {
					// ALWAYS release a lock when finished with a message.
					// Loader Queue Thread Manager will ALWAYS acquire a lock for a message
					// and relies on the loader worker to release the lock.
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

	private void processCommand() throws Exception {
		loader.receiveMessage(text, fileName, primeFlightKeyArray);
	}

	// Prepares the loader thread queue manager to be GC'd, performing a check
	// against the queue to make sure
	// no elements have been added right before the method was called.
	private synchronized boolean destroyQueue() {
		boolean deleted = false;
		if (this.queue.isEmpty()) {
			// remove the reference from the parent map at the same time as ending the
			// thread, dereferencing the queue and GC-ing it.
			logger.debug("Prime key being removed: " + Arrays.toString(this.primeFlightKeyArray));
			logger.debug("Queue inside this thread: " + this.queue.hashCode());
			this.map.remove(this.primeFlightKey);
			this.semaphore.release(this.queue.size()); // This should always be 0 and release 0 permits.
			this.queue = null;
			deleted = true;
		}
		return deleted;
	}

	synchronized boolean addMessageToQueue(Message<?> message) throws InterruptedException {
		if (this.queue == null) {
			// Handling data race condition. Add message to queue called AFTER
			// destroyQueue() which causes
			// an attempt to add a message to a null queue and the worker being unable to
			// process the message.
			// Releasing the semaphore is the LoaderWorker's job so we release and return
			// with a false value.
			// Which lets the queue manager retry receiving the message.
			semaphore.release();
			return false;
		} else {
			// This is normal path and should be what usually(99.99%) happens.
			// Note this is a blocking operation which holds the worker's lock.
			// This is acceptable as the other contention for the worker's lock only happens
			// when
			// the queue is empty and destroyQueue is called.
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
