/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.logcollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GtasLogCollectorApplication {

	private static final Logger log = LoggerFactory.getLogger(GtasLogCollectorApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(GtasLogCollectorApplication.class, args);

		log.info(" THE GTAS-LOG-COLLECTOR APPLICATION IS STARTING...... ");

	}
}
