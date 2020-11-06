/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

@Configuration
@ConditionalOnProperty(prefix = "omni", name = "enabled")
@EnableJms
public class OmniQueueConfig {

    private final Logger logger = LoggerFactory.getLogger(OmniQueueConfig.class);

    @Value("${omni.activemq.broker.url}")
    private String activeMQBrokerUrl;
    
    @Bean
    public JmsListenerContainerFactory<?> omniJmsListenerContainerFactory() {
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
}
