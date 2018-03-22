package gov.gtas.jms.config;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

 
@Configuration
/**Enable/ uncomment the below when the ActiveMQ is up and running.
 *Otherwise it will generate exception stack trace in the server console.
 **/
@EnableJms
public class MessagingListnerConfiguration {
 
	@Autowired
    ConnectionFactory connectionFactory;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1-1");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }
 
}
