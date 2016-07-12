/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.common.WebAppConfig;
import gov.gtas.controller.config.TestMvcRestServiceWebConfig;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppConfig.class})
@WebAppConfiguration
//@TransactionConfiguration(defaultRollback = true)
public class QueryBuilderControllerIT {

    public void testInitQueryBuilder() {
        
    }
    
    public void testRunFlightQuery() {
    
    }
    
    public void testRunPassengerQuery() {

    }
    
    @Rollback
    @Transactional
    public void saveQuery() {

    }

    
    public void editQuery() {

    }
    
    public void listQueryByUser() {

    }
    
    public void deleteQuery() {

    }

}
