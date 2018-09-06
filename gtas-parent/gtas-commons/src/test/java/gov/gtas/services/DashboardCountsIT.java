/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertNotNull;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.ApisStatistics;
import gov.gtas.model.PnrStatistics;
import gov.gtas.model.YTDAirportStatistics;
import gov.gtas.model.YTDRules;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
        CachingConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DashboardCountsIT {

    private static Logger logger = LoggerFactory.getLogger(DashboardCountsIT.class);

    @Autowired
    MessageStatisticsService service;

    @Autowired
    YTDStatisticsService ytdService;

    @Test
    public void testYTDStatistics() {
        List<YTDRules> ruleList = new ArrayList<YTDRules>();
        List<YTDAirportStatistics> statsList = new ArrayList<YTDAirportStatistics>();

        ruleList = ytdService.getYTDRules();
        statsList = ytdService.getYTDAirportStats();

        assertNotNull(statsList);
    }

    // @Test
    public void testPnrStatistics() {
        logger.info("##################### begin testPnrStatistics#############################");
        PnrStatistics pntCounts = service.getPnrStatistics();
        if (pntCounts != null) {
            logger.info(" COUNTS : " + pntCounts.toString());
        }
        assertNotNull(pntCounts);
        logger.info("###################### end testPnrStatistics############################");
    }

    // @Test
    public void testApisStatistics() {
        logger.info("##################### begin testApisStatistics#############################");
        ApisStatistics apisCounts = service.getApisStatistics();
        if (apisCounts != null) {
            logger.info(" COUNTS : " + apisCounts.toString());
        }
        assertNotNull(apisCounts);
        logger.info("###################### end testApisStatistics ############################");
    }
}
