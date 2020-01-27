package gov.gtas.parsers.tamr.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;
import org.springframework.stereotype.Service;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import gov.gtas.model.Flight;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.parsers.tamr.jms.TamrQueueConfig;
import gov.gtas.parsers.tamr.TamrAdapterImpl;
import gov.gtas.parsers.tamr.model.TamrQuery;

@Component
@ConditionalOnProperty(prefix = "tamr", name = "enabled")
public class TamrMessageSender {

	private final Logger logger = LoggerFactory.getLogger(TamrMessageSender.class);
	private TamrAdapterImpl tamrAdapter = new TamrAdapterImpl();

	@Autowired
	JmsTemplate jmsTemplateFile;

	@Autowired
	TamrQueueConfig queueConfig;

	public ConnectionFactory connectionFactory() {
		// Add tamr connection details here.
		return new ActiveMQConnectionFactory("");
	}

	public boolean sendMessageToTamr(String queue, List<TamrPassenger> passengers) throws Exception {
		logger.info("############### Attempting to craft tamr message .... ################");
		jmsTemplateFile.setDefaultDestinationName(queue);
		jmsTemplateFile.setConnectionFactory(queueConfig.cachingConnectionFactory());

		TamrQuery tamrQuery = new TamrQuery(passengers);
		ObjectMapper mapper = new ObjectMapper();
		String tamrQueryJson = mapper.writer().writeValueAsString(tamrQuery);

		logger.info("Query:");
		logger.info(tamrQueryJson);

		jmsTemplateFile.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createTextMessage(tamrQueryJson);
				message.setJMSType("QUERY");
				return message;
			}
		});

		return true;
	}
}
