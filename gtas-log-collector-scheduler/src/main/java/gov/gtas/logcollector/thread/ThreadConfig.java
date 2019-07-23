
/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.logcollector.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;

@Configuration
public class ThreadConfig {

	@Bean
	public SyncTaskExecutor threadPoolTaskExecutor() {
		SyncTaskExecutor executor = new SyncTaskExecutor();

		return executor;
	}

}
