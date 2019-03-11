/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static gov.gtas.rule.builder.RuleBuilderTestUtils.ADDRESS_PHONE_EMAIL_DOCUMENT_RULE_INDX;
import static gov.gtas.rule.builder.RuleBuilderTestUtils.AGENCY_CC_FF_FLIGHT_DOC_RULE_INDX;
import static gov.gtas.rule.builder.RuleBuilderTestUtils.PNR_PASSENGER_RULE_INDX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;

import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.repository.PnrRepository;
import gov.gtas.rule.builder.DrlRuleFileBuilder;
import gov.gtas.rule.builder.RuleBuilderTestUtils;
import gov.gtas.svc.util.TargetingServiceUtils;
import gov.gtas.testdatagen.PnrDataGenerator;

/**
 * Unit tests for the TargetingService using spring support and Mockito.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuleServiceConfig.class)
@Rollback(true)
public class TargetingServicePnrIT {
    private static final Logger logger = LoggerFactory.getLogger(TargetingServicePnrIT.class);
    public static final String UDR_RULE_AUTHOR="test";

    @Autowired
    TargetingService targetingService;

    @Resource
    private PnrRepository pnrRepository;
    
    @Test
    @Transactional
    public void testDataGeneration() {
        Pnr pnr1 = PnrDataGenerator.createTestPnr(1L);
        assertNotNull(pnr1);
        assertNotNull(pnr1.getId());
        int size1 = pnr1.getPassengers().size();
        assertTrue(size1 == 4 || size1 == 2 );
        Passenger pax = pnr1.getPassengers().iterator().next();
        assertNotNull("Pax ID is null", pax.getId());
    }

    @Test
    @Transactional
    public void testPnrRuleExecution1() throws ParseException {
        /*
         * one rule with multiple conditions involving PNR record locator
         * and passenger type and last name.
         */
        Pnr msg = PnrDataGenerator.createTestPnr(1L);
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, PNR_PASSENGER_RULE_INDX);
        String drlRules = drlBuilder.addRule(udrRule).build();
        RuleServiceRequest request = TargetingServiceUtils
                .createPnrRequestContext(msg).getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected 2 hit", 2, result.getResultList().size());
        RuleHitDetail res1 = (RuleHitDetail) (result.getResultList().get(0));
        assertNotNull("passenger ID in result is null", res1.getPassengerId());
        assertTrue("Expected passenger with id in (2,3)", (res1.getPassengerId() == 2L || res1.getPassengerId() == 3L));
        RuleHitDetail res2 = (RuleHitDetail) (result.getResultList().get(1));
        assertNotNull("passenger ID in result is null", res2.getPassengerId());
        assertTrue("Expected passenger with id in (2,3)", (res2.getPassengerId() == 2L || res2.getPassengerId() == 3L));
        assertTrue(res1.getPassengerId() != res2.getPassengerId());
    }

    @Test
    @Transactional
    public void testPnrRuleExecution2() throws ParseException {
        Pnr msg = PnrDataGenerator.createTestPnr(1L);
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, ADDRESS_PHONE_EMAIL_DOCUMENT_RULE_INDX);
        String drlRules = drlBuilder.addRule(udrRule).build();
        RuleServiceRequest request = TargetingServiceUtils
                .createPnrRequestContext(msg).getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected 2 hits", 2, result.getResultList().size());
        Iterator<RuleHitDetail> itr = result.getResultList().iterator();
        RuleHitDetail res1 = (RuleHitDetail) (itr.next());
        assertNotNull("passenger ID in result is null", res1.getPassengerId());
        assertTrue(
                "Hit Passenger id mismatch",
                new Long(1L).equals(res1.getPassengerId())
                        || new Long(2L).equals(res1.getPassengerId()));
        RuleHitDetail res2 = (RuleHitDetail) (itr.next());
        assertNotNull("passenger ID in result is null", res2.getPassengerId());
        assertTrue(
                "Hit Passenger id mismatch",
                new Long(1L).equals(res2.getPassengerId())
                        || new Long(2L).equals(res2.getPassengerId()));
        assertTrue("Expected passenger ids to be different", res1.getPassengerId() != res2.getPassengerId());
    }

    @Test
    @Transactional
    public void testPnrRuleExecution3() throws ParseException {
        // select all passengers in a flight
        Pnr msg = PnrDataGenerator.createTestPnr2(1L);
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, AGENCY_CC_FF_FLIGHT_DOC_RULE_INDX);
        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);
        RuleServiceRequest request = TargetingServiceUtils
                .createPnrRequestContext(msg).getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected 1 hit", 1, result.getResultList().size());
        RuleHitDetail res = (RuleHitDetail) (result.getResultList().get(0));
        assertNotNull("passenger ID in result is null", res.getPassengerId());
        assertEquals("Hit Passenger id mismatch",
                6L, res.getPassengerId());
    }

    @Test
    @Transactional
    public void testPnrRuleExecution4() throws ParseException {//seat rule with IN operator
        // select all passengers in a flight
        Pnr msg = PnrDataGenerator.createTestPnr2(1L);
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, RuleBuilderTestUtils.PNR_SEAT_RULE_INDX);
        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);
        RuleServiceRequest request = TargetingServiceUtils
                .createPnrRequestContext(msg).getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected 1 hit", 1, result.getResultList().size());
        RuleHitDetail res = (RuleHitDetail) (result.getResultList().get(0));
        assertNotNull("passenger ID in result is null", res.getPassengerId());
        assertEquals("Hit Passenger id mismatch",
                5L, res.getPassengerId());
    }

}
