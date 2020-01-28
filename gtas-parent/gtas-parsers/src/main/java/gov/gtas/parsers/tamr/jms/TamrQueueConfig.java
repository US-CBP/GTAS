/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

@Configuration
@ConditionalOnProperty(prefix = "tamr", name = "enabled")
@EnableJms
public class TamrQueueConfig {

    private final Logger logger = LoggerFactory.getLogger(TamrQueueConfig.class);

    @Value("${tamr.activemq.broker.url}")
    private String activeMQBrokerUrl;
    
    @Bean
    public JmsListenerContainerFactory<?> tamrJmsListenerContainerFactory() {        
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        
        factory.setConnectionFactory(
                new ActiveMQConnectionFactory(activeMQBrokerUrl));
        factory.setSessionTransacted(true);
        factory.setConcurrency("5");
        return factory;
    }

    @Bean
    public ActiveMQConnectionFactory senderConnectionFactory() {
        ActiveMQConnectionFactory aMQConnection = new ActiveMQConnectionFactory();
        aMQConnection.setBrokerURL(activeMQBrokerUrl);

        return aMQConnection;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(senderConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(cachingConnectionFactory());
    }

}
