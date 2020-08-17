package gov.gtas.services.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class OmniLocalGtasSender {
    private static final Logger log = LoggerFactory.getLogger(OmniLocalGtasSender.class);
    private static final String GTAS_INBOUND_QUEUE = "KAIZEN_TO_GTAS_Q";

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(String message) {
        log.info("sending message=\"" + message + "\"");
        jmsTemplate.convertAndSend(GTAS_INBOUND_QUEUE, message);
    }

    public boolean send(String messageType, String messageContent) throws Exception {
        log.info("############### Attempting to craft GTAS message .... ################");
        log.info("############### Sending to Queue: " +  GTAS_INBOUND_QUEUE + " .... ################");

        jmsTemplate.setDefaultDestinationName(GTAS_INBOUND_QUEUE);

        jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage(messageContent);
                message.setJMSType(messageType);
                return message;
            }
        });
        return true;
    }
}