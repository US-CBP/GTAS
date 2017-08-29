/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.jms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

//@Component
public class MessageSender {
 
    //@Autowired
    JmsTemplate jmsTemplate;
 
    public void sendMessage(final File f) {
 
        jmsTemplate.send(new MessageCreator(){
                @Override
                public Message createMessage(Session session) throws JMSException{
                	BytesMessage bytesMessage = session.createBytesMessage();
					try {
						byte[] array = Files.readAllBytes(new File(f.getAbsolutePath()).toPath());
						bytesMessage.writeBytes(array);
						bytesMessage.setStringProperty("NAME", f.getName());
						bytesMessage.setJMSCorrelationID(f.getName());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
                	return bytesMessage;
                }
            });
    }
 
}
