package gov.loadeworker;

import java.util.concurrent.Executor;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Import(AsyncConfig.class)
public class JmsConfig {

	@Value("${spring.activemq.broker-url}")
	private String DEFAULT_BROKER_URL;
	
	@Value("${loader.name}:random")
	private String name;

	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("TaskName-");
		executor.initialize();
		return executor;
	}
	
	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
	    ActiveMQPrefetchPolicy policy = new ActiveMQPrefetchPolicy();
	    policy.setQueuePrefetch(1);
	    connectionFactory.setPrefetchPolicy(policy);
		return connectionFactory;
	}
	
	@Bean
	public DefaultJmsListenerContainerFactory loaderAppJMSFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setConcurrency("1-10");
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		factory.setTaskExecutor(getAsyncExecutor());
		factory.setAutoStartup(false);
		factory.setCacheLevel(DefaultMessageListenerContainer.CACHE_SESSION);
		return factory;
	}
}
