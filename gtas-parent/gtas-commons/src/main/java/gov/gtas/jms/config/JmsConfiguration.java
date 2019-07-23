package gov.gtas.jms.config;

import java.util.HashMap;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import gov.gtas.model.Passenger;

 
@Configuration
public class JmsConfiguration {

    @Value("${activemq.broker.url}")
    private String DEFAULT_BROKER_URL;

  
    @Bean
    public ActiveMQConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
        return connectionFactory;
    }
    
    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
      return new CachingConnectionFactory(connectionFactory());
    }
 
    @Bean
    public JmsTemplate jmsTemplateJason() {
      return new JmsTemplate(cachingConnectionFactory());
    }
    
    @Bean
    public JmsTemplate jmsTemplateFile() {
      return new JmsTemplate(cachingConnectionFactory());
    }
    
    
    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
       HashMap<String, Class<?>> idMapping = new HashMap<String, Class<?>>();
       idMapping.put(gov.gtas.model.Passenger.class.getName(), Passenger.class);
       converter.setTypeIdMappings(idMapping);
        return converter;
    }
    
//    @Bean
//    public JmsTemplate jmsTemplate(){
//        JmsTemplate template = new JmsTemplate();
//        template.setSessionTransacted(true);
//        template.setConnectionFactory(connectionFactory());
//        template.setDefaultDestinationName( lookupRepo.getAppConfigOption(AppConfigurationRepository.QUEUE_OUT));
//        return template;
//    }
   
}
