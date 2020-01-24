/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.destination.DestinationResolver;

//Uncomment in order in part to re-enable queues
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
        
//        factory.setDestinationResolver(new DestinationResolver() {
//            @Override
//            public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain)
//                    throws JMSException {
//                logger.info("session = {}, destinationName = {}, pubSubDomain = {}",
//                        session, destinationName, pubSubDomain);
//                // Create a destination
//                return session.createQueue("OutboundQueue");
//            }
//        });
        factory.setConnectionFactory(
                new ActiveMQConnectionFactory(activeMQBrokerUrl));
        factory.setSessionTransacted(true);
        factory.setConcurrency("5");
        return factory;
    }

}
