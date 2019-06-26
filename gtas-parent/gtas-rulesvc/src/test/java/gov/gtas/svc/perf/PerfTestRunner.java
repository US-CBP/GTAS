/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.perf;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.svc.perf.test.FetchUdrTest;
import gov.gtas.svc.perf.test.PerformanceTest;
import gov.gtas.svc.perf.test.TargetingTestFactory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * A Java application for performance/load testing of the Rule Engine and
 * Targeting Service. The following tests are implemented: 1. fetchUdrTest() -
 *
 */
public class PerfTestRunner {

    private static final Logger logger = LoggerFactory.getLogger(PerfTestRunner.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = null;
        try {
            ctx = new AnnotationConfigApplicationContext(
                    TestCommonServicesConfig.class, CachingConfig.class, RuleServiceConfig.class);
            if (args.length < 1) {
                logger.info("The test or command name must be provided! [udrtest,perf, perfall, cleanperf, clean]");
                System.exit(0);
            }
            runTest(ctx, args);
            ctx.close();
        } catch (Exception ex) {
            logger.error("Exception runing perf test runner!", ex);
        } finally {
            if (ctx != null)
                ctx.close();
        }
        System.exit(0);
    }

    private static void runTest(ConfigurableApplicationContext ctx,
            String[] args) {
        PerformanceTest test = createTest(ctx, args);
        if (test != null) {
            List<String> result = test.runTest();
            logger.info("******************************************************************");
            for (String line : result) {
                logger.info(line);
            }
            logger.info("******************************************************************");
        }
    }

    private static PerformanceTest createTest(
            ConfigurableApplicationContext ctx, String[] args) {
        PerformanceTest test = null;
        TargetingTestFactory factory = null;

        switch (args[0]) {
        case "udrtest":
            int parallelRequestCount = 10;
            if (args.length > 1) {
                parallelRequestCount = Integer.parseInt(args[1]);
            }
            FetchUdrTest testMaker = new FetchUdrTest(parallelRequestCount, 400);
            test = testMaker.createTest(ctx);
            break;
        case "cleanperf":
            cleanupRuleData(ctx);
        case "perfall":
            factory = new TargetingTestFactory();
            factory.setGenData(true);
            factory.setUdrCount(100);
            factory.setPassengerWlCount(300);
            factory.setDocumentWlCount(40);
        case "perf":
            if (factory == null) {
                factory = new TargetingTestFactory();
            }
            if (args.length > 1) {
                factory.setIterationCount(Integer.parseInt(args[1]));
            }
            test = factory.createTest(ctx);
            break;
        case "clean":
            cleanupRuleData(ctx);
            break;
        default:
            logger.info(">>>>> ERROR unknown test name:" + args[0]);
            break;
        }
        return test;
    }

    private static void cleanupRuleData(ConfigurableApplicationContext ctx) {
        RulePersistenceService rulePersistenceService = (RulePersistenceService) ctx
                .getBean("rulePersistenceServiceImpl");
        final EntityManager em = rulePersistenceService.getEntityManager();
        JpaTransactionManager transactionManager = (JpaTransactionManager) ctx
                .getBean("transactionManager");
        TransactionTemplate template = new TransactionTemplate(
                transactionManager);
        template.execute(new TransactionCallback<Integer>() {
            // the code in this method executes in a transaction context
            public Integer doInTransaction(TransactionStatus status) {
                Query q = em
                        .createQuery("delete from WatchlistItem it where it.id > 0");
                int del = q.executeUpdate();
                logger.info("Number wl item deleted = " + del);
                q = em.createQuery("delete from Watchlist wl where wl.id > 0");
                int del2 = q.executeUpdate();
                logger.info("Number watch list deleted = " + del2);
                q = em.createQuery("delete from RuleMeta rm where rm.id > 0");
                int del3 = q.executeUpdate();
                logger.info("Number RuleMeta deleted = " + del3);
                q = em.createQuery("delete from Rule r where r.id > 0");
                int del4 = q.executeUpdate();
                logger.info("Number Rules deleted = " + del4);
                q = em.createQuery("delete from UdrRule u where u.id > 0");
                int del5 = q.executeUpdate();
                logger.info("Number Udr deleted = " + del5);
                return del + del2 + del3 + del4 + del5;
            }
        });
    }

}
