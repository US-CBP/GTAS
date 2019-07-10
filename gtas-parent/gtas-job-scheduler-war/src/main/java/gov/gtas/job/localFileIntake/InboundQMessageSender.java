/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.localFileIntake;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class InboundQMessageSender {

    private final
    JmsTemplate jmsTemplateFile;

    public InboundQMessageSender(JmsTemplate jmsTemplateFile) {
        this.jmsTemplateFile = jmsTemplateFile;
    }

    public void sendFileContent(final String queue, final String stringFile, String filename) {
        jmsTemplateFile.setDefaultDestinationName(queue);
        jmsTemplateFile.send(session -> {
            Message message = session.createObjectMessage(stringFile);
            message.setStringProperty("filename", filename);
            return message;
        });
    }
}
