/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.jms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import gov.gtas.services.Loader;

@Component
public class MessageReceiver {
    static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);
    private static final String GTAS_QUEUE = "GTAS_Q";
	
     
    @Autowired
    Loader gtasLoader;
     
    @JmsListener(destination = GTAS_QUEUE)
    public void receiveMessage(final Message<byte[]> message) throws JMSException {
        LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        MessageHeaders headers =  message.getHeaders();
        String fName="\\test\\"+(String)headers.get("NAME");
        LOG.info("Application : headers received : {}", headers);
        byte[] b = message.getPayload();

		try {
			Path file=Paths.get(fName);
			Files.write(file, b);
			LOG.info("+++++++++++++IS FILE++++++++++++++++++++++"+file.toFile().exists());
			gtasLoader.processMessage(file.toFile());
			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {


		}

	}
  
}
