package gov.gtas.jms.services;
/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.LookUpRepository;

/**
 * Class MessageSender allows the calling applications to send a java object 
 * which can be serialized to jason string and drop in the queue to receive from other end.
 * This will be developed as part of silicon valley requirement.Interested parties subscribe to the 
 * queue and receive the jason object from the message as payload.
 * 
 * @see gov.gtas.services.JmsSenderTest for usage details
 * 
 * */
@Component
public class MessageSender {
 
    @Autowired
    JmsTemplate jmsTemplateJason;
    
    @Autowired
    JmsTemplate jmsTemplateFile;
    
    @Autowired
    private LookUpRepository lookupRepo;
    
    @Autowired
    MessageConverter jacksonJmsMessageConverter;
    
    public void sendMessage(final Object o) {
    	
    	String queue = lookupRepo.getAppConfigOption(AppConfigurationRepository.QUEUE_OUT);
    	jmsTemplateJason.setMessageConverter(jacksonJmsMessageConverter);
    	if(o instanceof Flight){
    		Flight f =(Flight)o;
    		jmsTemplateJason.convertAndSend(queue, f);
    	}
    	else if(o instanceof Pnr){
    		Pnr p =(Pnr)o;
    		jmsTemplateJason.convertAndSend(queue, p);
    	}
    	else if(o instanceof Passenger){
    		Passenger p =(Passenger)o;
    		jmsTemplateJason.convertAndSend(queue, p);
    	}
    	else{
    		jmsTemplateJason.convertAndSend(queue, o);
    	}
   
    }
    
    public void sendFileContent(final String text) {
    	jmsTemplateFile.setDefaultDestinationName("GTAS_Q_IN");
    	jmsTemplateFile.send(new MessageCreator() {
    	      @Override
    	      public Message createMessage(Session session) throws JMSException {
    	    	Message message = session.createTextMessage(text);
    	        return message;
    	      }
    	    });
     }
}
