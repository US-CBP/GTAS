package gov.gtas.jms.services;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import gov.gtas.repository.LookUpRepository;

@Component
public class MessageReceiver {
    static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);
    
    //Enable it when you need to receive messages to process
    private static final String GTAS_QUEUE_IN = "GTAS_Q_IN";
    //Listen the out queue for testing purpose.Disable when you  are done with testing.
    private static final String GTAS_QUEUE = "GTAS_Q_OUT";
   
    @Autowired
    private LookUpRepository lookupRepo;
 
    @JmsListener(destination = GTAS_QUEUE)
    public void receiveMessage(final Message<?> message) throws JMSException {
        LOG.info("++++++++++++++++++Message Received+++++++++++++++++++++++++++++++++++");
        MessageHeaders headers =  message.getHeaders();
        LOG.info("Application : headers received : {}", headers);

		LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		LOG.info("Type "+headers.get("_type")+" "+message.getPayload());
		LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
    
    @JmsListener(destination = GTAS_QUEUE_IN)
    public void receivePnrApisFiles(final Message<?> message,String uploadDir) throws JMSException {
        LOG.info("+++++++++++++++++IN QUEUE++++++++++++++++++++++++++++++++++++");
        //String uploadDir = lookupRepo.getAppConfigOption(AppConfigurationRepository.UPLOAD_DIR);
        MessageHeaders headers =  message.getHeaders();
        LOG.info("Application : headers received : {}", headers);
 	
		LOG.info("+++++++++++++FILE CONTENT ++++++++++++++++++++++"+message.getPayload());
	}
  
}
