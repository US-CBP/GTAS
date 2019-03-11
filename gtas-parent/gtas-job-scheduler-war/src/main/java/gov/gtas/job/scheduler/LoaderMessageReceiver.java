/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
public class LoaderMessageReceiver {
	@Autowired
	private LoaderQueueThreadManager queueManager; 

	private static final String GTAS_LOADER_QUEUE = "GTAS_LOADER_Q";
	static final Logger logger = LoggerFactory.getLogger(LoaderMessageReceiver.class);
	
	@JmsListener(destination = GTAS_LOADER_QUEUE, concurrency = "10")
	public void receiveMessagesForLoader(Message<?> message, Session session, javax.jms.Message msg){
		logger.debug("+++++++++++++++++IN LOADER QUEUE++++++++++++++++++++++++++++++++++++");
		MessageHeaders headers =  message.getHeaders();
		logger.debug("Application : headers received : {}", headers);
		logger.debug("Filename: "+headers.get("Filename"));
		
		queueManager.receiveMessages(message);
	}
}