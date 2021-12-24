package gov.loadeworker;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import gov.gtas.model.LoaderWorker;
import gov.gtas.repository.LoaderWorkerRepository;
import gov.gtas.services.LoaderQueueThreadManager;

@Component
public class HealthCheckScheduler {
	
	private static final Logger logger = LoggerFactory.getLogger(HealthCheckScheduler.class);
	
	private final LoaderWorkerRepository loaderWorkerRepository;
	
	private final String name;
	
	private final LoaderQueueThreadManager loaderQueueThreadManager;
		
	
	private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;
	
	private String queueName = "";
	
	public HealthCheckScheduler(LoaderWorkerRepository loaderWorkerRepository, 
			@Value("${loader.name}")String name, LoaderQueueThreadManager loaderQueueThreadManager, 
			JmsListenerEndpointRegistry jmsListenerEndpointRegistry) {
		this.loaderWorkerRepository = loaderWorkerRepository;
		this.loaderQueueThreadManager = loaderQueueThreadManager;
		if (StringUtils.isBlank(name)) {
			this.name = UUID.randomUUID().toString();
		} else {
			this.name = name;
		}
		this.jmsListenerEndpointRegistry = jmsListenerEndpointRegistry;
		
	}


	@Scheduled(fixedDelayString = "${loader.delay}", initialDelayString = "15000")
	public void jobScheduling() {
		int bucketCount = LoaderQueueThreadManager.getBucketBucket().size();
		int semaphoreCount = loaderQueueThreadManager.getSemaphore().availablePermits();
		
		LoaderWorker lw = loaderWorkerRepository.findByWorkerName(name);
		
		if (lw == null) {
			lw = new LoaderWorker();
			lw.setCreatedAt(new Date());
			lw.setCreatedBy("LoaderWorker");
			lw.setActive(false);
		} else if (StringUtils.isBlank(lw.getAssignedQueue())){
			lw.setActive(false);
			logger.info("No queue assigned and no action taken, queue not consuming messages!");
		} else if (!queueName.equals(lw.getAssignedQueue())) {
     		AbstractMessageListenerContainer amlc = (AbstractMessageListenerContainer)jmsListenerEndpointRegistry.getListenerContainer("message_reciever");
			if (!StringUtils.isBlank(queueName)) {
				logger.info("Disassociating from queue name: " + queueName + " and initializing new connection to " + lw.getAssignedQueue() + " ...");
	     		amlc.setDestinationName(lw.getAssignedQueue());
	     		queueName = lw.getAssignedQueue();
				logger.info("Queue "+ lw.getAssignedQueue() + " has been started!");
			} else {
				logger.info("New queue assigned! Initializing new connection to " + lw.getAssignedQueue() + " ...");
	     		amlc.setDestinationName(lw.getAssignedQueue());
	     		queueName = lw.getAssignedQueue();
	     		amlc.start();
				logger.info("Queue "+ lw.getAssignedQueue() + " has been initialized!");
			}
			lw.setActive(true);
		} else if (queueName.equals(lw.getAssignedQueue())) {
			logger.info("Updating health check numbers for active queue:  "  + lw.getAssignedQueue());
			lw.setActive(true);
		}
		lw.setPermitsFree(semaphoreCount);
		lw.setBucketCount(bucketCount);
		lw.setUpdatedAt(new Date());
		lw.setWorkerName(name);
		lw.setUpdatedBy("LoaderWorker");
		loaderWorkerRepository.save(lw);
		logger.info("Health check completed for " + name + " with bucket count of : " + bucketCount + " permits free of: " + lw.getPermitsFree() 
		+ " attached to queue: " + lw.getAssignedQueue());
	}
}
