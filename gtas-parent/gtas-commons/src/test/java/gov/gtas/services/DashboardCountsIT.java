/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
        CachingConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DashboardCountsIT {

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
        System.out
                .println("##################### begin testPnrStatistics#############################");
        PnrStatistics pntCounts = service.getPnrStatistics();
        if (pntCounts != null) {
            System.out.println(" COUNTS : " + pntCounts.toString());
        }
        assertNotNull(pntCounts);
        System.out
                .println("###################### end testPnrStatistics############################");
    }

    // @Test
    public void testApisStatistics() {
        System.out
                .println("##################### begin testApisStatistics#############################");
        ApisStatistics apisCounts = service.getApisStatistics();
        if (apisCounts != null) {
            System.out.println(" COUNTS : " + apisCounts.toString());
        }
        assertNotNull(apisCounts);
        System.out
                .println("###################### end testApisStatistics ############################");
    }
}
