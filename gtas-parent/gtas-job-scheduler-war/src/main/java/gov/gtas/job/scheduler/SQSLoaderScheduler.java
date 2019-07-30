/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.util.List;

import gov.gtas.job.localFileIntake.InboundQMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.model.Message;

import gov.gtas.aws.QueueService;

/*
 * A scheduled task to process messages from AWS SQS queue. 
 * 
 * The initial delays in milliseconds and the fixed period  between invocations 
 * can be configured in the jobScheduler.properties file, as well as the flag to disable/enable it
 * 
 * Below is the default configuration for the scheduler
 * 
 * sqs.loader.fixedDelay.in.milliseconds=1000
 * sqs.loader.initialDelay.in.milliseconds=1000
 * sqs.loader.enabled=false
 * sqs.loader.queue= <AWS SQS URL> 
 * sqs.loader.region=<REGION NAME WHERE THE SQS IS HOSTED>
 * 
 */
@Component
public class SQSLoaderScheduler {

	private final Logger logger = LoggerFactory.getLogger(SQSLoaderScheduler.class);

	private QueueService queueService = null;

	@Value("${inbound.loader.jms.queue}")
	private String inboundLoaderQueue;

	@Value("${sqs.loader.enabled}")
	private String enabled;

	@Value("${sqs.loader.queue}")
	private String queue;
	
	@Value("${sqs.loader.region}")
	private String region;
	
	@Autowired
	private InboundQMessageSender sender;

	@Scheduled(fixedDelayString = "${sqs.loader.fixedDelay.in.milliseconds}", initialDelayString = "${sqs.loader.initialDelay.in.milliseconds}")
	public void startPullForMessages() {

		if (enabled == null || !Boolean.parseBoolean(enabled)) {
			return;
		}
		try {

			logger.debug("Scheduler pulling for messages ..... ");
			
			if(this.queueService == null) {
				this.queueService = new QueueService(this.queue, this.region);
			}
			
			List<Message> messages = queueService.receiveMessages();

			messages.forEach(p -> {

				/*
				 * push the incoming messages to redis
				 * 
				 */
				sender.sendFileContent(inboundLoaderQueue, p.getBody(), p.getMessageId());

				queueService.deleteMessage(p.getReceiptHandle());
			});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}