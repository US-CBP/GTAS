/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.io.File;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.parsers.tamr.jms.TamrMessageReceiver;
import gov.gtas.parsers.tamr.jms.TamrQueueConfig;
import gov.gtas.services.LoaderStatistics;
import gov.gtas.svc.WatchlistService;


/**
 * Utilities for testing Tamr integration.
 */
@Component
public class TamrIntegrationTestUtils {

    private final Logger logger = LoggerFactory.getLogger(TamrIntegrationTestUtils.class);
    
    private JmsTemplate jmsTemplate;
    
    private ClassLoader classLoader = getClass().getClassLoader();
    
    private JmsListenerEndpointRegistry jmsListenerRegistry;
        
    private Optional<TamrMessageReceiver> tamrMessageReceiver;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private LoaderScheduler loaderScheduler;
    
    private WatchlistService watchlistService;

    @Value("${tamr.activemq.broker.url}")
    private String activeMQBrokerUrl;
    
    public TamrIntegrationTestUtils(
            JmsListenerEndpointRegistry jmsListenerRegistry,
            Optional<TamrMessageReceiver> tamrMessageReceiver,
            EntityManager entityManager,
            LoaderScheduler loaderScheduler,
            WatchlistService watchlistService) {
        this.jmsListenerRegistry = jmsListenerRegistry;
        this.tamrMessageReceiver = tamrMessageReceiver;
        this.entityManager = entityManager;
        this.loaderScheduler = loaderScheduler;
        this.watchlistService = watchlistService;
    }

    private void configureJmsTemplate() {
        if (jmsTemplate == null) {
            ActiveMQConnectionFactory aMQConnection =
                    new ActiveMQConnectionFactory();
            aMQConnection.setBrokerURL(activeMQBrokerUrl);
            this.jmsTemplate = new JmsTemplate(aMQConnection);
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
            tamrMessageReceiver.get().receive(message);
            messagesProcessed += 1;
        }
        return messagesProcessed;
    }
    
    private WatchlistItemSpec createWatchlistItemSpec(
            String firstName, String lastName, String dob) {
        return new WatchlistItemSpec(null, "create", new WatchlistTerm[] {
            new WatchlistTerm("firstName", "String", firstName),
            new WatchlistTerm("lastName", "String", lastName),
            new WatchlistTerm("dob", "String", dob)
        });
    }
    
    /**
     * Create some sample watchlist items.
     */
    public void createWatchlistItems() {
        WatchlistSpec spec = new WatchlistSpec("Watchlist",
                EntityEnum.PASSENGER.getEntityName());
        spec.addWatchlistItem(createWatchlistItemSpec(
                "RUBEN", "THEBAULT", "1945-01-11"));
        spec.addWatchlistItem(createWatchlistItemSpec(
                "KIM", "OSAYUWAME", "1971-09-16"));
        spec.addWatchlistItem(createWatchlistItemSpec(
                "ALICIA", "DAVIES", "1962-05-21"));
        watchlistService.createUpdateDeleteWatchlistItems("ADMIN", spec, 1L);
    }
    
    /**
     * Load a flight with the given path and prime flight key.
     */
    public void loadFlight(String messageFilePath, String[] primeFlightKey)
            throws Exception {
        File messageFile = new File(classLoader.getResource(
                messageFilePath).getFile());
        LoaderStatistics stats = new LoaderStatistics();
        loaderScheduler.processSingleFile(messageFile, stats, primeFlightKey);
    }
    
}
