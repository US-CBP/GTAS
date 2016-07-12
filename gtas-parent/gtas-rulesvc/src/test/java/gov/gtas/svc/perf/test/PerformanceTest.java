/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.perf.test;

import java.util.List;

public interface PerformanceTest {
    List<String> runTest();
}
