/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.aws;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class QueueService {

	private static AmazonSQS sqs;
	private final Logger logger = LoggerFactory.getLogger(QueueService.class);
	private boolean externalAccessFlag;

	static {
		sqs = new AmazonSQSClient();
	}

	private String queueUrl;

	/**
	 * Configures SQS Client using Region, Access Key, and Secret Key
	 * 
	 * @param region
	 *            Region of AWS SQS
	 * @param accessKey
	 *            Access Key of the IAM user
	 * @param secretKey
	 *            Secret Key of the IAM user
	 */
	public void configureCredentials(String region, String accessKey, String secretKey) {

		if (region != null && accessKey != null && secretKey != null) {
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
			sqs = AmazonSQSClientBuilder.standard().withRegion(region.trim())
					.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
			externalAccessFlag = true;
			logger.info("## Configured SQS access credentials successfully. ");
		}
		else
		{
			logger.info("## sqs.loader.usecredentials is set to Y but one of the following values are null: region, accessKey, or secretKey.");
		}
	}

	public QueueService(String queueUrl, String region) {
		configure(queueUrl, region);
	}

	public void configure(String queueUrl, String region) {
		if (queueUrl == null || region == null)
			return;

		this.queueUrl = queueUrl.trim();

		// override the default region
		sqs.setRegion(RegionUtils.getRegion(region.trim()));

	}

	public void sendMessage(String msg) {
		sqs.sendMessage(new SendMessageRequest(this.queueUrl, msg));
	}

	public List<Message> receiveMessages() {
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(this.queueUrl);
		return sqs.receiveMessage(receiveMessageRequest).getMessages();
	}

	public void deleteMessage(String receiptHandle) {
		sqs.deleteMessage(new DeleteMessageRequest(this.queueUrl, receiptHandle));
	}

	public List<String> listQueues() {
		return sqs.listQueues().getQueueUrls();
	}

	public boolean isExternalAccessFlag() {
		return externalAccessFlag;
	}

}
