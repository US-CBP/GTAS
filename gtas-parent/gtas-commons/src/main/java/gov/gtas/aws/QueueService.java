/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.aws;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class QueueService {
    private static final String AWS_ACCESS_KEY_ID = "";
    private static final String AWS_SECRET_ACCESS_KEY = "";

    private AmazonSQS sqs;
    private String queueName;
    private String queueUrl;
    
    public QueueService() { }
    
    public QueueService(String queueName) {
        configure(queueName);
    }
    
    public void configure(String queueName) {
        this.queueName = queueName;
        BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
        this.sqs = new AmazonSQSClient(credentials);
        Region reg = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(reg);

        for (String queueUrl : listQueues()) {
            if (queueUrl.endsWith(this.queueName)) {
                this.queueUrl = queueUrl;
                break;
            }
        }
        
        if (StringUtils.isBlank(this.queueUrl)) {
            CreateQueueRequest createQueueRequest = new CreateQueueRequest(this.queueName);
            this.queueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
        }
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
}
