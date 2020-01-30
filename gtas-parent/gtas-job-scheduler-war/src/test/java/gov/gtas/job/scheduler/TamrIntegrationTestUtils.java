/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import gov.gtas.parsers.tamr.jms.TamrMessageReceiver;
import gov.gtas.parsers.tamr.jms.TamrQueueConfig;


/**
 * Utilities for testing Tamr integration.
 */
@Component
public class TamrIntegrationTestUtils {

    private final Logger logger = LoggerFactory.getLogger(TamrIntegrationTestUtils.class);
    
    private JmsTemplate jmsTemplate;
    
    @Autowired
    private JmsListenerEndpointRegistry jmsListenerRegistry;
    
    @Autowired
    private TamrQueueConfig tamrQueueConfig;
    
    @Autowired
    private TamrMessageReceiver tamrMessageReceiver;
    
    private void configureJmsTemplate() {
        if (jmsTemplate == null) {
            this.jmsTemplate = new JmsTemplate(
                    tamrQueueConfig.senderConnectionFactory());
            this.jmsTemplate.setReceiveTimeout(1000);
        }
    }
    
    /**
     * Disables JMS listeners, since they are problematic for transactional
     * tests (anything run by the listener will be outside the test
     * transaction). After calling this, call
     * synchronouslyProcessMessagesFromTamr to process all messages from Tamr
     * within the test transaction.
     */
    public void disableJmsListeners() {
        for (MessageListenerContainer container: jmsListenerRegistry.getListenerContainers()) {
            if (container instanceof DefaultMessageListenerContainer) {
                ((DefaultMessageListenerContainer) container).shutdown();
            } else {
                container.stop();
            }
        }
    }
    
    /**
     * Returns a message that was sent to Tamr by GTAS, waiting up to 3
     * seconds for one to become available. If there is no message sent to
     * Tamr in the given timeout, returns null.
     */
    public TextMessage getMessageSentToTamr() {
        this.configureJmsTemplate();
        return (TextMessage) jmsTemplate.receive("InboundQueue");
    }
    
    public void sendMessageToGtasFromTamr(String messageType, String messageText) {
        jmsTemplate.send("OutboundQueue", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage(messageText);
                message.setJMSType(messageType);
                return message;
            }
        });
    }
    
    /**
     * Sends all messages received from Tamr to the TamrMessageReceiver.
     * Calling this method after disableJmsListeners() will ensure that
     * any receiver logic that accesses the database will run within the
     * test transaction.
     */
    public int synchronouslyProcessMessagesFromTamr() {
        this.configureJmsTemplate();
        int messagesProcessed = 0;
        Message message;
        while ((message = jmsTemplate.receive("OutboundQueue")) != null) {
            tamrMessageReceiver.receive(message);
            messagesProcessed += 1;
        }
        return messagesProcessed;
    }
    
}
