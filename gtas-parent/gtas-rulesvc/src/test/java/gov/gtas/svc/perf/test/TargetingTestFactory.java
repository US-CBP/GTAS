/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.perf.test;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.UdrService;
import gov.gtas.svc.WatchlistService;
import gov.gtas.svc.perf.UdrRuleGenerator;
import gov.gtas.svc.perf.WatchlistRuleGenerator;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;

public class TargetingTestFactory implements PerformanceTestFactory,
        PerformanceTest {
    public static final String PASSENGER_WL_NAME = "PerfTest Passenger WL";
    public static final String DOCUMENT_WL_NAME = "PerfTest Document WL";

    private TargetingService targetingService;
    private WatchlistService watchlistService;
    private UdrService udrService;

    private int iterationCount = 10;

    private int udrCount = 100;
    private int passengerWlCount = 300;
    private int documentWlCount = 40;

    private boolean genData;

    @Override
    public List<String> runTest() {
        if (genData) {
            genPerformanceData();
            watchlistService.activateAllWatchlists();
            System.out
                    .println("*****************************************************************");
            System.out
                    .println("********************   ACTIVATION COMPLETE  *********************");
            System.out
                    .println("*****************************************************************");
        }
        List<String> ret = new LinkedList<String>();
        long max = 0;
        long min = Long.MAX_VALUE;
        long totalStart = System.currentTimeMillis();
        for (int i = 0; i < iterationCount; ++i) {
            long start = System.currentTimeMillis();
            targetingService.analyzeLoadedMessages(false);
            // Collection<TargetSummaryVo> res = ctx.getTargetingResult();
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > max)
                max = elapsed;
            if (elapsed < min)
                min = elapsed;
        }
        long totalElapsed = System.currentTimeMillis() - totalStart;
        ret.add(String.format("Min Time = %d, Max Time = %d", min, max));
        ret.add("Total Time = " + totalElapsed + ", Average Time = "
                + (totalElapsed / iterationCount));
        return ret;
    }

    @Override
    public PerformanceTest createTest(ConfigurableApplicationContext ctx) {
        targetingService = (TargetingService) ctx
                .getBean("targetingServiceImpl");
        watchlistService = (WatchlistService) ctx
                .getBean("watchlistServiceImpl");
        udrService = (UdrService) ctx.getBean("udrServiceImpl");
        return this;
    }

    private void genPerformanceData() {
        if (passengerWlCount > 0) {
            WatchlistRuleGenerator.generateWlRules(watchlistService,
                    PASSENGER_WL_NAME, EntityEnum.PASSENGER, passengerWlCount);
        }
        if (documentWlCount > 0) {
            WatchlistRuleGenerator.generateWlRules(watchlistService,
                    DOCUMENT_WL_NAME, EntityEnum.DOCUMENT, documentWlCount);
        }
        if (udrCount > 0) {
            UdrRuleGenerator.generateUdr(udrService, "PerfTestUdr", udrCount);
        }
        System.out
                .println("*****************************************************************");
        System.out
                .println("********************   GENERATION COMPLETE  *********************");
        System.out
                .println("*****************************************************************");
    }

    /**
     * @param iterationCount
     *            the iterationCount to set
     */
    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    /**
     * @param genData
     *            the genData to set
     */
    public void setGenData(boolean genData) {
        this.genData = genData;
    }

    /**
     * @param udrCount
     *            the udrCount to set
     */
    public void setUdrCount(int udrCount) {
        this.udrCount = udrCount;
    }

    /**
     * @param passengerWlCount
     *            the passengerWlCount to set
     */
    public void setPassengerWlCount(int passengerWlCount) {
        this.passengerWlCount = passengerWlCount;
    }

    /**
     * @param documentWlCount
     *            the documentWlCount to set
     */
    public void setDocumentWlCount(int documentWlCount) {
        this.documentWlCount = documentWlCount;
    }

}
