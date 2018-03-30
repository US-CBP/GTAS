/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class LoaderMessageReceiver {
	//Uncomment along with executorService when threading is more capable in our persistence model
/*	@Autowired
	private LoaderWorkerThread worker;*/
	
	@Autowired
	private LoaderScheduler loader;
	
	@Autowired
	private ApplicationContext	ctx;
	
	private int maxNumOfThreads = 5;
	
	private ExecutorService exec = Executors.newFixedThreadPool(maxNumOfThreads);
	
	private static ConcurrentMap<String, BlockingQueue<Message<?>>> bucketBucket = new ConcurrentHashMap<String, BlockingQueue<Message<?>>>();
	
	private static final String GTAS_LOADER_QUEUE = "GTAS_LOADER_Q";
	static final Logger logger = LoggerFactory.getLogger(LoaderMessageReceiver.class);
	
	@JmsListener(destination = GTAS_LOADER_QUEUE, concurrency = "10")
	public void receiveMessagesForLoader(Message<?> message, Session session, javax.jms.Message msg){
		logger.info("+++++++++++++++++IN LOADER QUEUE++++++++++++++++++++++++++++++++++++");
		MessageHeaders headers =  message.getHeaders();
		logger.info("Application : headers received : {}", headers);
		logger.info("Filename: "+headers.get("Filename"));

		//logger.info("+++++++++++++FILE CONTENT ++++++++++++++++++++++"+message.getPayload());
		String fileName = headers.get("Filename").toString();
		String primeFlight = headers.get("TVL").toString();
		
		//bucketBucket is a bucket of buckets. It holds a series of queues that are processed sequentially.
		//This solves the problem where-in which we cannot run the risk of trying to save/update the same flight at the same time. This is done by shuffling all identical flights into the same queue in order
		//to be processed sequentially. However, by processing multiple sequential queues at the same time, we in essence multi-thread the process for all non-identical prime flights
		BlockingQueue<Message<?>> potentialBucket = bucketBucket.get(primeFlight);
		if(potentialBucket == null){
			//Is not existing bucket, make bucket, stuff in bucketBucket,
			logger.info("New Queue Created For Prime Flight: " + primeFlight);
			BlockingQueue<Message<?>> queue = new ArrayBlockingQueue<Message<?>>(1024);
			queue.offer(message); //TODO: offer returns false if can't enter the queue, need to make sure we don't lose messages and have it wait for re attempt when there is space.
			bucketBucket.putIfAbsent(primeFlight, queue);
			//Only generate workers on a per queue basis
			LoaderWorkerThread worker = ctx.getBean(LoaderWorkerThread.class);
			worker.setQueue(queue);
			worker.setMap(bucketBucket); //give map reference and key in order to kill queue later
			worker.setPrimeFlightKey(primeFlight);
			exec.execute(worker);
		} else{
			//Is existing bucket, place same prime flight message into bucket
			logger.info("Existing Queue Found! Placing message inside...");
			potentialBucket.offer(message);
			//No need to execute worker here, if queue exists then worker is already on it.
		}
	}
}