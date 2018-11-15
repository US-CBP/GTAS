/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.aws;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
    private static AmazonSQS sqs;
    static {
        sqs = new AmazonSQSClient();
        // This needs to be configured from the properties file
        sqs.setRegion(Region.getRegion(Regions.GovCloud));    	
    }
    private String queueName;
    private String queueUrl;
    
    public QueueService() { }
    
    public QueueService(String queueName) {
        configure(queueName);
    }
    
    public void configure(String queueName) {
        this.queueName = queueName;
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
