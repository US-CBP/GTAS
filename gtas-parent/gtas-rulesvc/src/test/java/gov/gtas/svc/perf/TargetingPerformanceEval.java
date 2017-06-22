/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.perf;

import gov.gtas.config.RuleServiceConfig;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.services.watchlist.WatchlistPersistenceService;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.UdrService;
import gov.gtas.svc.WatchlistService;
import gov.gtas.svc.util.RuleExecutionContext;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Load tests for the Rule Engine using Watch list rules.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuleServiceConfig.class)
@TransactionConfiguration(defaultRollback = false)
public class TargetingPerformanceEval {
    public static final String PASSENGER_WL_NAME = "PerfTest Passenger WL";
    public static final String DOCUMENT_WL_NAME = "PerfTest Document WL";

    @Autowired
    TargetingService targetingService;

    @Autowired
    WatchlistService watchlistService;

    @Autowired
    UdrService udrService;

    @Autowired
    WatchlistPersistenceService watchlistPersistenceService;

    @Resource
    private ApisMessageRepository apisMessageRepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    // @Test
    @Transactional
    public void genPerformanceData() {
        WatchlistRuleGenerator.generateWlRules(watchlistService,
                PASSENGER_WL_NAME, EntityEnum.PASSENGER, 100);
        WatchlistRuleGenerator.generateWlRules(watchlistService,
                DOCUMENT_WL_NAME, EntityEnum.DOCUMENT, 20);
        // UdrRuleGenerator.generateUdr(udrService, "PerfTestUdr", 200);
        System.out
                .println("*****************************************************************");
        System.out
                .println("********************   GENERATION COMPLETE  *********************");
        System.out
                .println("*****************************************************************");
    }

    // @Test
    public void activatePerformanceData() {
        watchlistService.activateAllWatchlists();
        System.out
                .println("*****************************************************************");
        System.out
                .println("********************   ACTIVATION COMPLETE  *********************");
        System.out
                .println("*****************************************************************");
    }

    @Test
    @Transactional
    public void runPerformance() {
        long start = System.currentTimeMillis();
        RuleExecutionContext ctx = targetingService
                .analyzeLoadedMessages(false);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(String.format(
                "******* result count = %d, elapsed millis = %d", ctx
                        .getTargetingResult().size(), elapsed));
    }
}
