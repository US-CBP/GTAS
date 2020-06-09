package gov.gtas.job.scheduler.summary.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.job.scheduler.EventIdentifier;
import gov.gtas.job.scheduler.summary.MessageAction;
import gov.gtas.job.scheduler.summary.MessageSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class AdditionalProcessingMessageSender {

    private static final Logger logger = LoggerFactory.getLogger(AdditionalProcessingMessageSender.class);

    private final JmsTemplate jmsTemplateFile;

    @Autowired
    public AdditionalProcessingMessageSender(JmsTemplate jmsTemplateFile) {
        this.jmsTemplateFile = jmsTemplateFile;
    }

    public void sendFileContent(String addProcessString, org.springframework.messaging.Message<?> message, EventIdentifier eventIdentifier, MessageAction messageAction) {
        jmsTemplateFile.setDefaultDestinationName(addProcessString);
        jmsTemplateFile.send(session -> {
            MessageSummary messageSummary =  new MessageSummary();
            messageSummary.setRawMessage((String)message.getPayload());
            messageSummary.setEventIdentifier(eventIdentifier);
            messageSummary.setAction(messageAction);
            ObjectMapper mapper = new ObjectMapper();
            String messageJson;
            try {
                messageJson = mapper.writer().writeValueAsString(messageSummary);
            } catch (JsonProcessingException e) {
                messageJson = e.getMessage();
                messageSummary.setAction(MessageAction.ERROR);
                logger.error("ERROR WRITING JSON! NO ADDITIONAL PROCESSING!");
            }
            Message fwd = session.createObjectMessage(messageJson);
            fwd.setStringProperty("eventType", eventIdentifier.getEventType());
            fwd.setStringProperty("destCountry", eventIdentifier.getCountryDestination());
            fwd.setStringProperty("originCountry", eventIdentifier.getCountryOrigin());
            fwd.setStringProperty("identifier", eventIdentifier.getIdentifier());
            fwd.setObjectProperty("identifierList", eventIdentifier.getIdentifierArrayList());
            fwd.setStringProperty("action", messageAction.toString());
            return fwd;
        });
    }
}
