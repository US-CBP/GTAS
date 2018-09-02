/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;


import gov.gtas.config.CommonServicesConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class })
public class RuleCatServiceImplTestIT {

    private static final Logger logger = LoggerFactory
            .getLogger(RuleCatServiceImplTestIT.class);
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Autowired
    private RuleCatService ruleCatService;


    @Test
    public void testRuleCatRetrieval() throws Exception{

    //assertTrue(ruleCatService.findRuleCatByID(new Long(2)).getCatId()!=null);
    }

}
