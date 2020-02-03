/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import gov.gtas.config.TestCommonServicesConfig;

/**
 * Make sure that no Tamr-related code runs while tamr.enabled is false.
 * @author Cassidy Laidlaw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class })
@TestPropertySource(locations = { "classpath:default.application.properties" }, properties = {"tamr.enabled=false"})
@Rollback(true)
public class TamrDisabledIT extends AbstractTransactionalJUnit4SpringContextTests {
    private Logger logger = LoggerFactory.getLogger(TamrDisabledIT.class);
    
    @Autowired
    private TamrIntegrationTestUtils tamrUtils;

    @Test
    @Transactional
    public void testTamrDisabled() throws Exception {
        tamrUtils.disableJmsListeners();
        
        tamrUtils.createWatchlistItems();
        tamrUtils.loadFlight(
           "tamr-integration-data/flight1.txt",
            new String[] { "SCL", "JFK", "LA", "0532",
                    "1579392000000", "1579394000000" }
        );
        
        // No messages should be sent to Tamr.
        assertNull(tamrUtils.getMessageSentToTamr());
    }
}
