/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static gov.gtas.IntegrationTestBuilder.MessageTypeGenerated.PNR;
import static gov.gtas.rule.builder.RuleBuilderTestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import gov.gtas.IntegrationTestBuilder;
import gov.gtas.IntegrationTestData;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.model.*;
import gov.gtas.model.udr.json.QueryConditionEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.querybuilder.mappings.*;
import gov.gtas.rule.builder.QueryTermFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.config.RuleServiceConfig;
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
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class TargetingServicePnrIT {
    private static final Logger logger = LoggerFactory.getLogger(TargetingServicePnrIT.class);
    public static final String UDR_RULE_AUTHOR="test";

    @Autowired
    TargetingService targetingService;

    @Resource
    private PnrRepository pnrRepository;

    @Autowired
    private IntegrationTestBuilder integrationTestBuilder;


    private QueryTermFactory queryTermFactory;

    @Before
    public void setUp() {
        queryTermFactory = new QueryTermFactory();
    }

    @After
    public void tearDown() {
        integrationTestBuilder.reset();
    }
    @Test
    @Transactional
    public void testPnrRuleExecution1() throws ParseException {
        /*
         * one rule with multiple conditions involving PNR record locator
         * and passenger type and last name.
         */
        Pnr msg = PnrDataGenerator.createTestPnr(1L);
        IntegrationTestData integrationTestData = integrationTestBuilder
                .pnrMessage(msg)
                .messageType(PNR)
                .flight(msg.getFlights()
                        .iterator().next())
                .build();

        Pnr testDataPnrMessage = integrationTestData.getPnrMessage();
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, PNR_PASSENGER_RULE_INDX);
        String drlRules = drlBuilder.addRule(udrRule).build();
        RuleServiceRequest request = TargetingServiceUtils
                .createPnrRequestContext(testDataPnrMessage).getRuleServiceRequest();
        logger.info(drlRules);
        Set<Long> passengerID = new HashSet<>();
        for (Passenger p : testDataPnrMessage.getPassengers()) {
            passengerID.add(p.getId());
        }
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected 2 hit", 2, result.getResultList().size());
        RuleHitDetail res1 = (RuleHitDetail) (result.getResultList().get(0));
        assertNotNull("passenger ID in result is null", res1.getPassengerId());
        assertTrue("Expected passenger with id in (2,3)", (passengerID.contains(res1.getPassengerId()) || passengerID.contains(res1.getPassengerId())));
        RuleHitDetail res2 = (RuleHitDetail) (result.getResultList().get(1));
        assertTrue(res1.getPassengerId() != res2.getPassengerId());
    }

    @Test
    @Transactional
    public void testPnrRuleExecution2() throws ParseException {
        Pnr msg = PnrDataGenerator.createTestPnr(1L);
        IntegrationTestData integrationTestData = integrationTestBuilder
                .pnrMessage(msg)
                .messageType(PNR)
                .flight(msg.getFlights()
                        .iterator().next())
                .build();
        Pnr testDataPnrMessage = integrationTestData.getPnrMessage();

        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, ADDRESS_PHONE_EMAIL_DOCUMENT_RULE_INDX);
        String drlRules = drlBuilder.addRule(udrRule).build();
        RuleServiceRequest request = TargetingServiceUtils
                .createPnrRequestContext(testDataPnrMessage).getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected 2 hits", 2, result.getResultList().size());
        Iterator<RuleHitDetail> itr = result.getResultList().iterator();
        RuleHitDetail res1 = (RuleHitDetail) (itr.next());
        assertNotNull("passenger ID in result is null", res1.getPassengerId());
        Set<Long> passengerID = new HashSet<>();
        for (Passenger p : testDataPnrMessage.getPassengers()) {
            passengerID.add(p.getId());
        }
        assertTrue(
                "Hit Passenger id mismatch",
                passengerID.contains(res1.getPassengerId())
                        || passengerID.contains(res1.getPassengerId()));
        RuleHitDetail res2 = (RuleHitDetail) (itr.next());
        assertNotNull("passenger ID in result is null", res2.getPassengerId());
        assertTrue("Expected passenger ids to be different", res1.getPassengerId() != res2.getPassengerId());
    }

    @Test
    @Transactional
    public void testPnrRuleExecution3() throws ParseException {
        // select all passengers in a flight
        Pnr msg = PnrDataGenerator.createTestPnr2(1L);
        IntegrationTestData integrationTestData = integrationTestBuilder
                .pnrMessage(msg)
                .messageType(PNR)
                .flight(msg.getFlights()
                        .iterator().next())
                .build();
        msg = integrationTestData.getPnrMessage();

        List<QueryTerm> builderQueries = new LinkedList<>();
        builderQueries.add(queryTermFactory.create(TravelAgencyMapping.NAME, CriteriaOperatorEnum.ENDS_WITH, "Tours"));
        builderQueries.add(queryTermFactory.create(CreditCardMapping.CREDIT_CARD_NUMBER, CriteriaOperatorEnum.BEGINS_WITH, "123"));
        builderQueries.add(queryTermFactory.create(FrequentFlyerMapping.CARRIER, CriteriaOperatorEnum.EQUAL, "AA"));
        builderQueries.add(queryTermFactory.create(FlightMapping.AIRPORT_DESTINATION, CriteriaOperatorEnum.EQUAL, "jfk"));
        builderQueries.add(queryTermFactory.create(DocumentMapping.ISSUANCE_DATE, CriteriaOperatorEnum.LESS, "2014-01-30"));
        QueryConditionEnum queryConditionEnum = QueryConditionEnum.AND;
        UdrSpecification udrSpecification = getUdrSpecification(builderQueries, queryConditionEnum);
        UdrRule udrRule = getUdrRule(udrSpecification);

        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);

        RuleServiceRequest request = TargetingServiceUtils
                .createPnrRequestContext(msg).getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected 4 hit", 4, result.getResultList().size());
        RuleHitDetail res = (RuleHitDetail) (result.getResultList().get(0));
        assertNotNull("passenger ID in result is null", res.getPassengerId());
    }

    @Test
    @Transactional
    public void testPnrRuleExecution4() throws ParseException {//seat rule with IN operator
        // select all passengers in a flight
        Pnr msg = PnrDataGenerator.createTestPnr2(1L);
        IntegrationTestData integrationTestData = integrationTestBuilder
                .pnrMessage(msg)
                .messageType(PNR)
                .flight(msg.getFlights()
                        .iterator().next())
                .build();
        msg = integrationTestData.getPnrMessage();
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        UdrRule udrRule = RuleBuilderTestUtils
                .createSimpleUdrRule(UDR_RULE_AUTHOR,
                RuleBuilderTestUtils.PNR_SEAT_RULE_INDX);
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
    }

}
