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
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;

import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.LookUpRepository;
import gov.gtas.services.SmsService;

@Service
public class SmsServiceImpl implements SmsService {
	private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
	private static AmazonSNS sns;
	static {
		sns = new AmazonSNSClient();
		sns.setRegion(Region.getRegion(Regions.GovCloud));
	}
	@Autowired
	private LookUpRepository lookupRepo;

	@Override
	public boolean sendMessage(String s) {
		String arn = null;
		try {
			arn = lookupRepo.getAppConfigOption(AppConfigurationRepository.SMS_TOPIC_ARN);
			PublishRequest r = new PublishRequest().withTopicArn(arn).withMessage(s);
			sns.publish(r);
		} catch (Exception e) {
			logger.error("could not publish to topic: " + arn, e);
			return false;
		}
		return true;
	}
}
