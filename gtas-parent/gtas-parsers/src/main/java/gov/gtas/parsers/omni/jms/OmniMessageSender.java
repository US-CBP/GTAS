/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.parsers.omni.jms.OmniQueueConfig;
import gov.gtas.parsers.omni.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.*;

@Component
@ConditionalOnProperty(prefix = "omni", name = "enabled")
public class OmniMessageSender {
    private static ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(OmniMessageSender.class);

    private final int MAX_DEBUG_PRINT_ROWS = 5;

    private JmsTemplate jmsTemplate;

    private OmniQueueConfig queueConfig;

    public OmniMessageSender(OmniQueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }
    
    public void sendMessageToOmni(OmniMessageType messageType, Object messageObject) {
        String messageJson = "";
        try {
            if (Objects.equals(messageType, OmniMessageType.ASSESS_RISK_REQUEST)) {
                List<OmniRawProfile> omniRawProfileList = new ArrayList<>();
                OmniAssessPassengersRequest omniAssessPassengersRequest = new OmniAssessPassengersRequest();
                List<OmniPassenger> omniPassengerList = (List<OmniPassenger>) messageObject;
                for (OmniPassenger omniPassenger : omniPassengerList) {
                    omniRawProfileList.add(omniPassenger.getOmniRawProfile());
                }
                omniAssessPassengersRequest.setProfiles(omniRawProfileList);
                messageJson = objectMapper.writer().writeValueAsString(omniAssessPassengersRequest);
                // logger.info(" ========= Sending Kaizen this batch of OmniRawProfiles={}", messageJson);
                logger.info(" ========= Sending Kaizen batch of OmniRawProfiles ==========");
                debugPrintOmniRawProfileRequestPayload(omniRawProfileList);
            } else if (Objects.equals(messageType, OmniMessageType.UPDATE_DEROG_CATEGORY)) {
                OmniDerogPassengerUpdate omniDerogPassengerUpdate = (OmniDerogPassengerUpdate) messageObject;
                messageJson = objectMapper.writer().writeValueAsString(omniDerogPassengerUpdate);
                // logger.info(" ========= Sending Kaizen this batch of omniDerogPassengerUpdate={}", messageJson);
                logger.info(" ========= Sending Kaizen batch of OmniDerogPassengerUpdate =========");
                debugPrintOmniDerogPassengerUpdateRequestPayload(omniDerogPassengerUpdate);
            }

        } catch (JsonProcessingException e) {
            logger.error("Could not send {} message (type={}) to Omni: {}",
                    messageObject.getClass(), messageType, e);
            return;
        }

        if (messageJson.length() > 0) {
            this.sendTextMessageToOmni(messageType, messageJson);
        }
    }
    
    public void sendTextMessageToOmni(
            OmniMessageType messageType, String messageText) {
        if (jmsTemplate == null) {
            this.jmsTemplate = new JmsTemplate(
                    queueConfig.senderConnectionFactory());
        }
        logger.info("========= Sending {} message to Omni. =========", messageType);
        logger.debug(messageText);
 
        jmsTemplate.send("GTAS_TO_KAIZEN_Q", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage(messageText);
                message.setJMSType(messageType.toString());
                return message;
            }
        });
    }

    private void debugPrintOmniRawProfileRequestPayload(List<OmniRawProfile> omniRawProfileList) {
        try {
            int maxElementShown = MAX_DEBUG_PRINT_ROWS;
            int totalSize = omniRawProfileList.size();
            if (totalSize == 0) {
                return;
            }
            if (totalSize < maxElementShown) {
                maxElementShown = totalSize;
            }
            List<OmniRawProfile> displayPayloadBucket = new ArrayList<>();
            for (OmniRawProfile omniRawProfile : omniRawProfileList) {
                displayPayloadBucket.add(omniRawProfile);
            }
            String displayTitle = "========= Showing the first " + maxElementShown + " OmniRawProfiles sent to Kaizen =======";

            if (maxElementShown == 1) {
                displayTitle = "========= Showing the only OmniRawProfile sent to Kaizen ======";
            }

            String jsonPayload = objectMapper.writer().writeValueAsString(displayPayloadBucket);

            logger.info(displayTitle);

            logger.info(jsonPayload);

        } catch (Exception ex) {
            logger.error("debugPrintOmniRawProfileRequestPayload() - Got an exception: ", ex);
        }
    }

    private void debugPrintOmniDerogPassengerUpdateRequestPayload(OmniDerogPassengerUpdate omniDerogPassengerUpdate) {
        try {
            int maxElementShown = MAX_DEBUG_PRINT_ROWS;
            List<OmniRawProfile> omniRawProfileList = omniDerogPassengerUpdate.getProfiles();
            List<OmniLookoutCategory> lookoutCategoryList = omniDerogPassengerUpdate.getLookoutCategories();
            int totalSize = omniRawProfileList.size();
            if (totalSize == 0) {
                return;
            }
            if (totalSize < maxElementShown) {
                maxElementShown = totalSize;
            }
            List<OmniRawProfile> displayOmniRawProfileBucket = new ArrayList<>();
            List<OmniLookoutCategory> displayOmniLookoutCategoryBucket = new ArrayList<>();

            int i = 0;
            for (OmniRawProfile omniRawProfile : omniRawProfileList) {
                displayOmniRawProfileBucket.add(omniRawProfile);
                displayOmniLookoutCategoryBucket.add(lookoutCategoryList.get(i++));
            }

            String displayOmniRawProfileTitle = "========= Showing the first " + maxElementShown + " OmniRawProfiles sent to Kaizen =======";
            String displayOmniLookoutCategoryTitle = "========= Showing the first " + maxElementShown + " OmniLookoutCategories sent to Kaizen =======";

            if (maxElementShown == 1) {
                displayOmniRawProfileTitle = "========= Showing the only OmniRawProfile sent to Kaizen ======";
                displayOmniLookoutCategoryTitle = "========= Showing the only OmniLookoutCategory sent to Kaizen =======";
            }

            String jsonPayload = objectMapper.writer().writeValueAsString(displayOmniRawProfileBucket);

            logger.info(displayOmniRawProfileTitle);
            logger.info(jsonPayload);

            logger.info(displayOmniLookoutCategoryTitle);
            jsonPayload = objectMapper.writer().writeValueAsString(displayOmniLookoutCategoryBucket);
            logger.info(jsonPayload);

        } catch (Exception ex) {
            logger.error("debugPrintOmniDerogPassengerUpdateRequestPayload() - Got an exception: ", ex);
        }
    }
}
