/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.jms;

import com.fasterxml.jackson.databind.JsonMappingException;
import gov.gtas.parsers.omni.OmniMessageHandlerService;
import gov.gtas.parsers.omni.model.OmniMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.*;

@Component
@ConditionalOnProperty(prefix = "omni", name = "enabled")
public class OmniMessageReceiver {

	private final Logger logger = LoggerFactory.getLogger(OmniMessageReceiver.class);

    private OmniMessageHandlerService omniMessageHandler;

    public OmniMessageReceiver(OmniMessageHandlerService omniMessageHandler) {
        this.omniMessageHandler = omniMessageHandler;
    }
    
    @JmsListener(destination = "KAIZEN_TO_GTAS_Q")
	public void receive(Message msg) {
		logger.debug("############### GTAS received data from KAIZEN SERVER.... ################");
		TextMessage textMessage = null;
		String jmsMessageType, messageText;

		if (msg == null) {
			logger.warn("Received null JMS message from Omni");
			return;
		}

		textMessage = (TextMessage) msg;
		try {
			jmsMessageType = textMessage.getJMSType();
			messageText = textMessage.getText();
		} catch (JMSException e) {
			logger.error("Error handling Omni JMS message: {}",
					e);
			return;
		}
		logger.debug("Message type: {}", jmsMessageType);

		if (Objects.equals(jmsMessageType, OmniMessageType.ASSESS_RISK_RESPONSE.getStringValue())) {
			omniMessageHandler.handlePassengersRiskAssessmentResponse(messageText);
		} else if (Objects.equals(jmsMessageType, OmniMessageType.UPDATE_DEROG_LAST_RUN_RESPONSE.getStringValue())) {
			omniMessageHandler.handleRetrieveLastRunResponse(messageText);
		}
	}
}
