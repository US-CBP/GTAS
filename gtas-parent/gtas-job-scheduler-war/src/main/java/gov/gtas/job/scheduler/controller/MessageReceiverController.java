/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler.controller;

import gov.gtas.job.scheduler.service.MessageReceiverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/*
* Do not open this endpoint unless the scheduler is secured!
* */

@ConditionalOnProperty(prefix = "scheduler", name = "web.receiver.on")
@RestController
public class MessageReceiverController {

	private static Logger logger = LoggerFactory.getLogger(MessageReceiverController.class);

	MessageReceiverService messageReceiverService;

	public MessageReceiverController(MessageReceiverService messageReceiverService) {
		this.messageReceiverService = messageReceiverService;
	}

	@PostMapping(value = "/api/message", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void consumeMessage(@RequestBody WebMessage messagePayload) {
		if (messagePayload == null || messagePayload.getMessagePayload() == null) {
			logger.error("Object messagePayload is null or contents of message payload is null");
		} else {
			messageReceiverService.putMessageOnQueue(messagePayload);
		}
	}
}
