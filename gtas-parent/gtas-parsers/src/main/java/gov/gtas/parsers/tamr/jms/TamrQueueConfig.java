/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DestinationResolver;

//Uncomment in order in part to re-enable queues
/*@Configuration
@EnableJms*/
public class TamrQueueConfig {

	@Bean
	public DefaultJmsListenerContainerFactory tamrJmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(
				// Add tamr connection details here.
				new ActiveMQConnectionFactory(""));
		factory.setDestinationResolver(new DestinationResolver() {

			@Override
			public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain)
					throws JMSException {

				// Create a destination
				return session.createQueue("OutboundQueue");

			}
		});
		factory.setSessionTransacted(true);
		factory.setConcurrency("5");
		return factory;
	}

}
