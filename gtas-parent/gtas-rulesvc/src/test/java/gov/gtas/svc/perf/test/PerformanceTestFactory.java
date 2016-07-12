/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.perf.test;

import org.springframework.context.ConfigurableApplicationContext;

public interface PerformanceTestFactory {
    PerformanceTest createTest(ConfigurableApplicationContext ctx);
}
