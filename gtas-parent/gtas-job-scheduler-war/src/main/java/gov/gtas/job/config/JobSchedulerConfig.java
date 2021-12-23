/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.config;

import java.util.Arrays;
import java.util.List;
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


	private static final String NEO_4_J_RULE_ENGINE_ENABLED="neo4j-rule-engine.enabled";
	private static final String EXECUTOR_THREADS ="executor-threads";
	private static final String THREADS_ON_RULES = "threads-on-rules";
	private static final String THREADS_ON_LOADER= "threads-on-loader";
	private static final String MAX_MESSAGES_PER_RULE_RUN="max-messages-per-rule-run";
	private static final String MAX_FLIGHTS_PER_RULE_RUN="max-flights-per-rule-run";
	private static final String MAX_FLIGHTS_PROCESSED_PER_THREAD="max-flights-processed-per-thread";
	private static final String MAX_PASSENGERS_PER_RULE_RUN="max-passengers-per-rule-run";
	private static final String OUT_PROCESS_LIMIT = "message-out-process-limit";
	private static final String RETENTION_HOURS_MASK_APIS="retention-hours-mask-apis";
	private static final String RETENTION_HOURS_DELETE_APIS="retention-hours-delete-apis";
	private static final String RETENTION_HOURS_MASK_PNR="retention-hours-mask-pnr";
	private static final String RETENTION_HOURS_DELETE_PNR="retention-Hours-delete-pnr";
	private static final String MESSAGE_STATUS_FOR_PNR_MASK_RETENTION = "message-status-mask-retention-pnr";
	private static final String MESSAGE_STATUS_FOR_PNR_DELETION_RETENTION = "message-status-deletion-retention-pnr";
	private static final String MESSAGE_STATUS_FOR_APIS_MASK_RETENTION = "message-status-mask-retention-apis";
	private static final String MESSAGE_STATUS_FOR_APIS_DELETION_RETENTION = "message-status-deletion-retention-apis";
	private static final String RUN_DATA_RETENTION_APIS_JOB = "run-data-retention-apis-job";
	private static final String RUN_DATA_RETENTION_PNR_JOB = "run-data-retention-pnr-job";
	private static final String MESSAGE_PASSENGER_OUT_PROCESS_THREAD_LIMIT = "message-passenger-out-process-thread-limit";
	private static final String RETAIN_HITS = "retain-hits";

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
		int executorThreads = Integer.parseInt(env.getRequiredProperty(EXECUTOR_THREADS));
		return Executors.newScheduledThreadPool(executorThreads);
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

	public int getRetentionHoursMaskAPIS() {
		return Integer.parseInt(env.getRequiredProperty(RETENTION_HOURS_MASK_APIS));
	}

	public int getRetentionHoursMaskPNR() {
		return Integer.parseInt(env.getRequiredProperty(RETENTION_HOURS_MASK_PNR));
	}

	public int getRetentionHoursDeleteAPIS() {
		return Integer.parseInt(env.getRequiredProperty(RETENTION_HOURS_DELETE_APIS));
	}

	public int getRetentionHoursDeletePNR() {
		return Integer.parseInt(env.getRequiredProperty(RETENTION_HOURS_DELETE_PNR));
	}

	public List<String> getMessageStatusMaskRetentionPNR() {
		String props = env.getRequiredProperty(MESSAGE_STATUS_FOR_PNR_MASK_RETENTION);
		String[] status = props.split(",");
		return   Arrays.asList(status);
	}

	public List<String> getMessageStatusDeletionRetentionAPIS() {
		String props = env.getRequiredProperty(MESSAGE_STATUS_FOR_APIS_DELETION_RETENTION);
		String[] status = props.split(",");
		return   Arrays.asList(status);
	}

	public List<String> getMessageStatusDeleteRetentionPNR() {
		String props = env.getRequiredProperty(MESSAGE_STATUS_FOR_PNR_DELETION_RETENTION);
		String[] status = props.split(",");
		return   Arrays.asList(status);
	}

	public List<String> getMessageStatusMaskRetentionAPIS() {
		String props = env.getRequiredProperty(MESSAGE_STATUS_FOR_APIS_MASK_RETENTION);
		String[] status = props.split(",");
		return   Arrays.asList(status);
	}

	public boolean isPnrRetentionDataJob() {
		return Boolean.parseBoolean(env.getRequiredProperty(RUN_DATA_RETENTION_PNR_JOB));
	}

	public boolean isAPISRetentionDataJob() {
		return Boolean.parseBoolean(env.getRequiredProperty(RUN_DATA_RETENTION_APIS_JOB));

	}

	public int messageOutProcessLimit() {
		return Integer.parseInt(env.getRequiredProperty(OUT_PROCESS_LIMIT));

	}

	public int messagePassengerOutProcessThreadLimit() {
		return Integer.parseInt(env.getRequiredProperty(MESSAGE_PASSENGER_OUT_PROCESS_THREAD_LIMIT));
	}

    public boolean getRetainHits() {
		return Boolean.parseBoolean(env.getRequiredProperty(RETAIN_HITS));

	}
}
