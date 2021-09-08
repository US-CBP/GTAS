package gov.gtas.job.scheduler;

import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Component;

public class ManualMover {
	
	private static final Logger logger = LoggerFactory.getLogger(ManualMover.class);

	private String brokerUrl;

	private String mainQueue;
	private ReentrantLock lock = new ReentrantLock();

	public ManualMover(String brokerUrl, String mainQueue) {
		this.brokerUrl = brokerUrl;
		this.mainQueue = mainQueue;
	}


	public void purgeQueue(String origin) throws JMSException {

		lock.lock();
		Message message = null;
		Connection connection = null;
		Session session = null;
		MessageConsumer consumer = null;
		MessageProducer producer = null;
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
			connectionFactory.setBrokerURL(brokerUrl);
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			Destination source = session.createQueue(origin);
			Destination destination = session.createQueue(mainQueue);
			consumer = session.createConsumer(source);
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			message = consumer.receive(1000);
			while (message != null) {
				producer.send(message);
				message.acknowledge();
				message = consumer.receive(1000);
			}
		} catch (Exception e) {
			if (e instanceof JmsException) {
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					logger.error("Failed to send message to " + text, e);
				} else {
					logger.error("Failed to send message to " + message, e);
				}
			} else {
				logger.error("", e);
			}
		} finally {
			try {
				producer.close();
			} catch (Exception ex) {
				logger.error("Producer failed to close!", ex);
			}
			try {
				consumer.close();
			} catch (Exception ex) {
				logger.error("Consumer failed to close!", ex);
			}
			try {
				session.close();
			} catch (Exception ex) {
				logger.error("Session failed to close!", ex);
			}
			try {
				connection.close();
			} catch (Exception ex) {
				logger.error("Connection failed to close!", ex);
			}
			lock.unlock();
		}
	}

}
