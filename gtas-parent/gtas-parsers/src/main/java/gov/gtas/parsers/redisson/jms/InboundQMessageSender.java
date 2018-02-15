package gov.gtas.parsers.redisson.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.File;

@Component
public class InboundQMessageSender {

    @Autowired
    JmsTemplate jmsTemplateFile;

    @Autowired
    MessageConverter jacksonJmsMessageConverter;


    public void sendFileContent(final String queue, final File file) {
        jmsTemplateFile.setDefaultDestinationName(queue);
        jmsTemplateFile.convertAndSend(queue, file);
//        jmsTemplateFile.send(new MessageCreator() {
//            @Override
//            public Message createMessage(Session session) throws JMSException {
//                Message message = session.createObjectMessage(file);
//                return message;
//            }
//        });
    }

    public void sendFileContent(final String queue, final String string) {
        jmsTemplateFile.setDefaultDestinationName(queue);
        jmsTemplateFile.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createObjectMessage(string);
                return message;
            }
        });
    }
}
