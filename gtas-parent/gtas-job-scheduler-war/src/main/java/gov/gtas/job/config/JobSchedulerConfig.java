/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.Resource;

/**
 * The Class JobSchedulerConfig.
 */
@Configuration
@EnableScheduling
@ComponentScan("gov.gtas.job.scheduler")
@PropertySource("classpath:default.application.properties")
@PropertySource(value = "file:${catalina.home}/conf/application.properties", ignoreResourceNotFound = true)
public class JobSchedulerConfig implements SchedulingConfigurer {


	private static final String NEO_4_J_RULE_ENGINE_ENABLED="neo4jRuleEngineEnabled";
	private static final String THREADS_ON_RULES = "threadsOnRules";
	private static final String THREADS_ON_LOADER= "threadsOnLoader";
	private static final String MAX_MESSAGES_PER_RULE_RUN="maxMessagesPerRuleRun";
	private static final String MAX_FLIGHTS_PER_RULE_RUN="maxFlightsPerRuleRun";
	private static final String MAX_FLIGHTS_PROCESSED_PER_THREAD="maxFlightsProcessedPerThread";
	private static final String MAX_PASSENGERS_PER_RULE_RUN="maxPassengersPerRuleRun";

	@Resource
	private Environment env;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.scheduling.annotation.SchedulingConfigurer#configureTasks
	 * (org.springframework.scheduling.config.ScheduledTaskRegistrar)
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	/**
	 * Task executor.
	 *
	 * @return the executor
	 */
	@Bean(destroyMethod = "shutdown")
	public Executor taskExecutor() {
		return Executors.newScheduledThreadPool(30);
	}

	public boolean getNeo4JRuleEngineEnabled() {
		return Boolean.parseBoolean(env.getRequiredProperty(NEO_4_J_RULE_ENGINE_ENABLED));
	}

	public int getThreadsOnRules() {
		return Integer.parseInt(env.getRequiredProperty(THREADS_ON_RULES));
	}

	public int getThreadsOnLoader() {
		return Integer.parseInt(env.getRequiredProperty(THREADS_ON_LOADER));
	}

	public int getMaxPassengersPerRuleRun() {
		return Integer.parseInt(env.getRequiredProperty(MAX_PASSENGERS_PER_RULE_RUN));
	}

	public int getMaxMessagesPerRuleRun() {
		return Integer.parseInt(env.getRequiredProperty(MAX_MESSAGES_PER_RULE_RUN));
	}

	public int getMaxFlightsPerRuleRun() {
		return Integer.parseInt(env.getRequiredProperty(MAX_FLIGHTS_PER_RULE_RUN));
	}

	public int getMaxFlightsProcessedPerThread() {
		return Integer.parseInt(env.getRequiredProperty(MAX_FLIGHTS_PROCESSED_PER_THREAD));
	}
}
