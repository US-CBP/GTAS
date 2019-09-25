/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.google.common.base.Strings;

import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.LookUpRepository;
import gov.gtas.services.SnsService;

@Service
public class SnsServiceImpl implements SnsService {
	private static final Logger logger = LoggerFactory.getLogger(SnsServiceImpl.class);

	@Autowired
	private LookUpRepository lookupRepo;

	@Override
	public String sendMessage(String message) {
		String arn = null;
		String messageId = null;
		try {
			arn = lookupRepo.getAppConfigOption(AppConfigurationRepository.SMS_TOPIC_ARN);
			messageId = this.publishMessage(message, "", arn);
		} catch (Exception e) {
			logger.error("could not publish to topic: " + arn, e);
			return messageId;
		}
		return messageId;
	}

	@Override
	public String sendNotification(String message) {
		String arn = null;
		String subject = null;
		String messageId = null;
		try {
			arn = lookupRepo.getAppConfigOption(AppConfigurationRepository.INTERPOL_SNS_NOTIFICATION_ARN);
			subject = lookupRepo.getAppConfigOption(AppConfigurationRepository.INTERPOL_SNS_NOTIFICATION_SUBJECT);
			messageId = this.publishMessage(message, subject, arn);
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
	public String publishMessage(String message, String subject, String arn) {

		AmazonSNSClientBuilder builder = AmazonSNSClientBuilder.standard()
				.withRegion(Region.getRegion(Regions.US_EAST_1).getName());

		AmazonSNS sns = builder.build();

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

		return sns.publish(r).getMessageId();
	}
}
