/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MessageLoadingJobScheduler {

    public static void main(String[] args) {
        @SuppressWarnings("resource")
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(
                CommonServicesConfig.class, CachingConfig.class);
        @SuppressWarnings("unused")
        FileReader mover = (FileReader) ctx.getBean("fileReader");
    }
}
