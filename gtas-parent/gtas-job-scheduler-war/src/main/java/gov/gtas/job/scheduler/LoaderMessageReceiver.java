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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class LoaderMessageReceiver {
	//Uncomment along with executorService when threading is more capable in our persistence model
	@Autowired
	private LoaderWorkerThread worker;
	
	@Autowired
	private LoaderScheduler loader;
	
	private int maxNumOfThreads = 4;
	
	private ExecutorService exec = Executors.newFixedThreadPool(maxNumOfThreads);
	
	private static BlockingQueue<Message<?>> queue = new ArrayBlockingQueue<Message<?>>(1024);
	
	
	private static final String GTAS_LOADER_QUEUE = "GTAS_LOADER_Q";
	static final Logger logger = LoggerFactory.getLogger(LoaderMessageReceiver.class);
	
	@JmsListener(destination = GTAS_LOADER_QUEUE, concurrency = "10")
	public void receiveMessagesForLoader(Message<?> message, Session session, javax.jms.Message msg){
		logger.info("+++++++++++++++++IN LOADER QUEUE++++++++++++++++++++++++++++++++++++");
		MessageHeaders headers =  message.getHeaders();
		logger.info("Application : headers received : {}", headers);
		logger.info("Filename: "+headers.get("Filename"));
		try {
			msg.acknowledge();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//logger.info("+++++++++++++FILE CONTENT ++++++++++++++++++++++"+message.getPayload());
		String fileName = headers.get("Filename").toString();
		//loader.receiveMessage(message.getPayload().toString(), fileName);;
		//Uncomment when threading is supported for our loader process
		//logger.info("Worker Object Generated With Filename: "+ worker.getFileName());
		
		try {
			queue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//loader.receiveMessage(message.getPayload().toString(), fileName);
		worker.setQueue(queue);
		exec.execute(worker);
	}
}