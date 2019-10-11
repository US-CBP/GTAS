/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.jms;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.parsers.tamr.model.TamrResponse;
import gov.gtas.repository.PassengerIDTagRepository;

@Component
public class TamrMessageReceiver {

	private final Logger logger = LoggerFactory.getLogger(TamrMessageReceiver.class);

	@Autowired
	private PassengerIDTagRepository passengerIDTagRepository;
	//Commented out listener, uncomment for tamr integration
	//@JmsListener(containerFactory = "tamrJmsListenerContainerFactory", destination = "OutboundQueue")
	public void receive(javax.jms.Message msg) {
		logger.info("############### TAMR data received .... ################");
		TextMessage textMessage = null;

		if (msg != null) {
			logger.info("javax.jms.Message not null");
			textMessage = (TextMessage) msg;
		}
		if (textMessage != null) {
			try {
				TamrResponse res = convert(textMessage.getText());
				if (res != null && res.getTravelerQuery() != null)
					res.getTravelerQuery().forEach(p -> {
						logger.info("Run update -> Tamr: " + p.getTamrId() + ", gtasId: " + p.getGtasId());
						this.passengerIDTagRepository.updateTamrId(p.gtasId, p.getTamrId());
					});
			} catch (JMSException e) {
				logger.error("caught JMSException");
			}
		}
	}

	public TamrResponse convert(String response) {
		ObjectMapper mapper = new ObjectMapper();

		TamrResponse result = null;

		try {
			result = mapper.readValue(response, TamrResponse.class);
		} catch (JsonParseException e) {
			logger.info(e.getMessage());
			return null;
		} catch (JsonMappingException e) {
			logger.info(e.getMessage());
			return null;
		} catch (IOException e) {
			logger.info(e.getMessage());
			return null;
		}

		return result;
	}
}
