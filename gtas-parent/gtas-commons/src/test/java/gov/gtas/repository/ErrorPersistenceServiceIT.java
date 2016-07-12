/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.error.CommonServiceException;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorUtils;
import gov.gtas.services.ErrorPersistenceService;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
        CachingConfig.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ErrorPersistenceServiceIT {

    @Autowired
    private ErrorPersistenceService testTarget;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Transactional
    public void createErrorTest() {
        ErrorDetailInfo err = ErrorUtils
                .createErrorDetails(new NullPointerException("Test Error"));
        err = testTarget.create(err);
        assertNotNull(err);
        assertNotNull(err.getErrorId());
        assertEquals("Test Error", err.getErrorDescription());
    }

    @Test
    @Transactional
    public void findErrorTest() {
        ErrorDetailInfo err = ErrorUtils
                .createErrorDetails(new NullPointerException("Test Error"));
        err = testTarget.create(err);
        ErrorDetailInfo err2 = testTarget.findById(err.getErrorId());
        assertNotNull(err2);
        assertNotNull(err2.getErrorId());
        assertEquals("Test Error", err2.getErrorDescription());
    }

    @Test
    @Transactional
    public void findByCodeTest() {
        testTarget.create(ErrorUtils
                .createErrorDetails(new NullPointerException("Test Error1")));
        testTarget.create(ErrorUtils
                .createErrorDetails(new CommonServiceException("TEST_CODE",
                        "Test Error2")));
        testTarget.create(ErrorUtils
                .createErrorDetails(new CommonServiceException("TEST_CODE",
                        "Test Error3")));
        testTarget.create(ErrorUtils
                .createErrorDetails(new CommonServiceException("TEST_CODE",
                        "Test Error4")));
        List<ErrorDetailInfo> lst = testTarget.findByCode("TEST_CODE");
        assertNotNull(lst);
        assertEquals(3, lst.size());
        String desc1 = lst.get(0).getErrorDescription();
        String desc2 = lst.get(1).getErrorDescription();
        String desc3 = lst.get(2).getErrorDescription();
        assertTrue(desc1.matches("Test Error[2,3,4]"));
        assertTrue(desc2.matches("Test Error[2,3,4]"));
        assertTrue(desc3.matches("Test Error[2,3,4]"));
    }

    @Test
    @Transactional
    public void findByDateRangeTest() throws Exception {
        Date start = new Date();
        Thread.sleep(1000L);
        testTarget.create(ErrorUtils
                .createErrorDetails(new NullPointerException("Test Error1")));
        testTarget.create(ErrorUtils
                .createErrorDetails(new CommonServiceException("TEST_CODE",
                        "Test Error2")));
        Thread.sleep(1000L);
        Date fin = new Date();
        Thread.sleep(1000L);
        testTarget.create(ErrorUtils
                .createErrorDetails(new CommonServiceException("TEST_CODE",
                        "Test Error3")));
        testTarget.create(ErrorUtils
                .createErrorDetails(new CommonServiceException("TEST_CODE",
                        "Test Error4")));
        List<ErrorDetailInfo> lst = testTarget.findByDateRange(start, fin);
        assertNotNull(lst);
        assertEquals(2, lst.size());
        String desc1 = lst.get(0).getErrorDescription();
        String desc2 = lst.get(1).getErrorDescription();
        assertTrue(desc1.matches("Test Error[1,2]"));
        assertTrue(desc2.matches("Test Error[1,2]"));

        lst = testTarget.findByDateFrom(start);
        assertNotNull(lst);
        assertEquals(4, lst.size());
        desc1 = lst.get(0).getErrorDescription();
        assertTrue(desc1.matches("Test Error[1,2,3,4]"));
    }
}
