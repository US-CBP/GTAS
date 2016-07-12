/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * The Class JobSchedulerConfig.
 */
@Configuration
@EnableScheduling
@ComponentScan("gov.gtas.job.scheduler")
@PropertySource({ "classpath:jobScheduler.properties",
		"classpath:dashboardJobScheduler.properties" })
public class JobSchedulerConfig implements SchedulingConfigurer {

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

}
