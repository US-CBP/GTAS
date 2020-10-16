package gov.gtas.services.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.summary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Component
public class AdditionalProcessingMessageSender {

    private static final Logger logger = LoggerFactory.getLogger(AdditionalProcessingMessageSender.class);

    private final JmsTemplate jmsTemplateFile;

    @Autowired
    public AdditionalProcessingMessageSender(
            JmsTemplate jmsTemplateFile) {
        this.jmsTemplateFile = jmsTemplateFile;
    }

    public void sendProcessedMessage(String queueName, MessageSummaryList messageSummaryList, SummaryMetaData smd) {
        jmsTemplateFile.setDefaultDestinationName(queueName);
        EventIdentifier eventIdentifier = messageSummaryList.getEventIdentifier();
        jmsTemplateFile.send(queueName, session -> {
            ObjectMapper mapper = new ObjectMapper();
            String messageJson;
            try {
                messageJson = mapper.writer().writeValueAsString(messageSummaryList);
            } catch (JsonProcessingException e) {
                messageJson = e.getMessage();
                logger.error("ERROR WRITING JSON! NO ADDITIONAL PROCESSING!");
            }
            Message fwd = session.createTextMessage(messageJson);
            setEventIdentifierProps(eventIdentifier, fwd);
            fwd.setStringProperty("action", messageSummaryList.getMessageAction().toString());
            fwd.setObjectProperty("countryList", smd.getCountryList());
            fwd.setStringProperty("countryGroupName", smd.getCountryGroupName());
            return fwd;
        });
    }


    public void sendRawMessage(String queueName, String payload, EventIdentifier eventIdentifier, MessageAction messageAction) {
        jmsTemplateFile.setDefaultDestinationName(queueName);
        jmsTemplateFile.send(session -> {
            MessageSummaryList msRawList = new MessageSummaryList();
            MessageSummary messageSummary =  new MessageSummary();
            messageSummary.setRawMessage(payload);
            messageSummary.setEventIdentifier(eventIdentifier);
            messageSummary.setAction(messageAction);
            msRawList.setEventIdentifier(eventIdentifier);
            msRawList.getMessageSummaryList().add(messageSummary);
            msRawList.setMessageAction(messageAction);
            ObjectMapper mapper = new ObjectMapper();
            String messageJson;
            try {
                messageJson = mapper.writer().writeValueAsString(msRawList);
            } catch (JsonProcessingException e) {
                messageJson = e.getMessage();
                messageSummary.setAction(MessageAction.ERROR);
                logger.error("ERROR WRITING JSON! NO ADDITIONAL PROCESSING!");
            }
            TextMessage fwd = session.createTextMessage(messageJson);
            setEventIdentifierProps(eventIdentifier, fwd);
            fwd.setStringProperty("action", messageAction.toString());
            return fwd;
        });
    }


    private void setEventIdentifierProps(EventIdentifier eventIdentifier, Message fwd) throws JMSException {
        fwd.setStringProperty("eventType", eventIdentifier.getEventType());
        fwd.setStringProperty("destCountry", eventIdentifier.getCountryDestination());
        fwd.setStringProperty("originCountry", eventIdentifier.getCountryOrigin());
        fwd.setStringProperty("identifier", eventIdentifier.getIdentifier());
        fwd.setObjectProperty("identifierList", eventIdentifier.getIdentifierArrayList());
    }
}
