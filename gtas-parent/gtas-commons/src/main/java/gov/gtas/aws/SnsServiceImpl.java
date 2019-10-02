/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.google.common.base.Strings;

import gov.gtas.services.SnsService;

@Service
public class SnsServiceImpl implements SnsService {
	private static final Logger logger = LoggerFactory.getLogger(SnsServiceImpl.class);

	@Override
	public String sendNotification(AmazonSNS amazonSNS, String message, String subject, String arn) {

		String messageId = null;
		try {
			messageId = this.publishMessage(amazonSNS, message, subject, arn);
		} catch (Exception e) {
			logger.error("could not publish to topic: {}, {}", arn, e);
		}

		return messageId;
	}

	/**
	 * Sends message to AWS SNS topic specified by the arn
	 * 
	 * @param message
	 * @param arn
	 * @return Unique identifier assigned to the published message.
	 */
	public String publishMessage(AmazonSNS amazonSNS, String message, String subject, String arn) {

		PublishRequest r = new PublishRequest().withTopicArn(arn).withMessage(message);

		/**
		 * Optional parameter to be used as the "Subject" line when the message is
		 * delivered to email endpoints.
		 * 
		 * If no subject is passed do not set empty/null value
		 */

		if (!Strings.isNullOrEmpty(subject)) {
			r = r.withSubject(subject);
		}

		return amazonSNS.publish(r).getMessageId();
	}
}
