/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static gov.gtas.rule.builder.RuleBuilderTestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import gov.gtas.IntegrationTestBuilder;
import gov.gtas.IntegrationTestData;
import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Document;
import gov.gtas.model.Passenger;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.QueryConditionEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.querybuilder.mappings.DocumentMapping;
import gov.gtas.querybuilder.mappings.FlightMapping;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.rule.builder.DrlRuleFileBuilder;
import gov.gtas.rule.builder.QueryTermFactory;
import gov.gtas.rule.builder.RuleBuilderTestUtils;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.TargetingServiceUtils;
import gov.gtas.testdatagen.ApisDataGenerator;
import gov.gtas.util.DateCalendarUtils;

import java.text.ParseException;
import java.util.*;

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
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Integration tests for the TargetingService using spring support. (Also contains
 * tests for date-effective and date-expires properties of rules.)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RuleServiceConfig.class, CommonServicesConfig.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class TargetingServiceIT {
    private static final Logger logger = LoggerFactory.getLogger(TargetingServiceIT.class);
    public static final String UDR_RULE_AUTHOR = "test";

    @Autowired
    TargetingService targetingService;

    @Autowired
    IntegrationTestBuilder integrationTestBuilder;

    private QueryTermFactory queryTermFactory;
    private IntegrationTestData integrationTestData;

    @Before
    public void setUp() {
        queryTermFactory = new QueryTermFactory();

    }

    @After
    @Transactional
    public void tearDown() {
        integrationTestBuilder.reset();
    }

    @Test
    @Transactional
    public void testDataGeneration() {
        integrationTestData = integrationTestBuilder
                .build();
        ApisMessage msg = integrationTestData.getApisMessage();
        assertNotNull(msg);
        assertNotNull(msg.getId());
    }

    @Test
    @Transactional
    public void testApisRuleExecution1() {
        integrationTestData = integrationTestBuilder
                .build();
        /*\
         * Rule strings must be upper case - see RuleConditionBuilderHelper line 59
         * */
        String fakeFlightNumber = "FAKE";
        integrationTestData.getFlight().setFlightNumber(fakeFlightNumber);
        List<QueryTerm> ruleQueries = new LinkedList<>();
        QueryTerm queryTerm = queryTermFactory.create(FlightMapping.FLIGHT_NUMBER, CriteriaOperatorEnum.EQUAL, fakeFlightNumber);
        ruleQueries.add(queryTerm);

        ApisMessage msg = integrationTestData.getApisMessage();
        UdrRule udrRule = createBaseUdrRule("123");
        udrRule.addEngineRule(generateRule(udrRule, ruleQueries));
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);
        RuleExecutionContext ruleExecutionContext = TargetingServiceUtils.createApisRequest(msg);
        RuleServiceRequest request = ruleExecutionContext.getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request, drlRules);

        assertNotNull(result);
        assertEquals("Expected 1 hit", 1, result.getResultList().size());
        RuleHitDetail res = result.getResultList().get(0);
                Long id = integrationTestData.getPassenger().getId();
        assertEquals("Passenger Ids Expected do not match", id.longValue(),
                res.getPassengerId());
    }

    @Test
    @Transactional
    public void testApisRuleExecution2()  {
        integrationTestData = integrationTestBuilder
                .build();
        QueryTerm matchFirstName = queryTermFactory.
                create(PassengerMapping.FIRST_NAME, CriteriaOperatorEnum.EQUAL, "NOT_THERE");
        QueryTerm matchIssueCountry = queryTermFactory.
                create(DocumentMapping.ISSUANCE_COUNTRY, CriteriaOperatorEnum.EQUAL, "YE");

        QueryConditionEnum queryConditionEnum = QueryConditionEnum.OR;
        List<QueryTerm> builderQueries = new LinkedList<>();
        builderQueries.add(matchFirstName);
        builderQueries.add(matchIssueCountry);
        UdrSpecification udrSpecification = getUdrSpecification(builderQueries, queryConditionEnum);
        UdrRule udrRule = getUdrRule(udrSpecification);

        Document document = new Document();
        document.setDocumentType("P");
        document.setDocumentNumber("54");
        document.setIssuanceCountry("YE");
        integrationTestData.getFlight().setFlightNumber("1234");
        document.setPassenger(integrationTestData.getPassenger());
        Set<Document> documents = new HashSet<>();
        documents.add(document);
        integrationTestData.getPassenger().setDocuments(documents);

        ApisMessage msg = integrationTestData.getApisMessage();
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);
        RuleExecutionContext ruleExecutionContext = TargetingServiceUtils.createApisRequest(msg);
        RuleServiceRequest request = ruleExecutionContext.getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected 1 hits", 1, result.getResultList().size());
        RuleHitDetail res = result.getResultList().get(0);
        long passengerId = res.getPassengerId();
        assertEquals("Hit Passenger id mismatch",
                (long) integrationTestData.getPassenger().getId(), passengerId);
    }


    @Test
    @Transactional
    public void noHitOnFutureDateTest() throws ParseException {
        integrationTestData = integrationTestBuilder
                .build();
        ApisMessage msg = ApisDataGenerator.createSimpleTestApisMesssage();
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();

        // set the start date in the future
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(
                UDR_RULE_AUTHOR, DOC_FLIGHT_CRITERIA_RULE_INDX,
                DateCalendarUtils.addOneDayToDate(new Date()), null);

        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);
        RuleServiceRequest request = TargetingServiceUtils.createApisRequest(
                msg).getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected no hit", 0, result.getResultList().size());
    }

    @Test
    @Transactional
    public void testApisRuleExecution1ValidUptoToday() {
        integrationTestData = integrationTestBuilder
                .build();
        /*
         * same test as "testApisRuleExecution1" but with rule start date in the
         * past and end date today. Hence there should be 1 hit as in
         * "testApisRuleExecution1".
         */

        // set the start date to yesterday and end date to today
        Date endDate = new Date();
        Date startDate = DateCalendarUtils.subtractOneDayFromDate(endDate);

        /*\
         * Rules must be upper case - see RuleConditionBuilderHelper line 59
         * */
        String fakeFlightNumber = "FAKE";
        integrationTestData.getFlight().setFlightNumber(fakeFlightNumber);
        List<QueryTerm> ruleQueries = new LinkedList<>();
        QueryTerm queryTerm = queryTermFactory.create(FlightMapping.FLIGHT_NUMBER, CriteriaOperatorEnum.EQUAL, fakeFlightNumber);
        ruleQueries.add(queryTerm);
        ApisMessage msg = integrationTestData.getApisMessage();


        UdrRule udrRule = createBaseUdrRule("123", startDate, endDate);


        udrRule.addEngineRule(generateRule(udrRule, ruleQueries));
        Long id = integrationTestData.getPassenger().getId();
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);
        RuleExecutionContext ruleExecutionContext = TargetingServiceUtils.createApisRequest(msg);
        RuleServiceRequest request = ruleExecutionContext.getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request, drlRules);
        assertNotNull(result);
        assertEquals("Expected 1 hit", 1, result.getResultList().size());
        RuleHitDetail res = result.getResultList().get(0);
        assertEquals("Passenger Ids Expected do not match", id.longValue(),
                res.getPassengerId());
    }

    @Test
    @Transactional
    public void testApisRuleExecution1PastEndDate() {
        integrationTestData = integrationTestBuilder
                .build();
        /*
         * same test as "testApisRuleExecution1" but with rule start date and
         * end date set to yesterday. Hence there should be no hits.
         */

        // set the start/end date to yesterday
        Date date = DateCalendarUtils.subtractOneDayFromDate(new Date());

        /*\
         * Rules must be upper case - see RuleConditionBuilderHelper line 59
         * */
        String fakeFlightNumber = "FAKE";
        integrationTestData.getFlight().setFlightNumber(fakeFlightNumber);
        List<QueryTerm> ruleQueries = new LinkedList<>();
        QueryTerm queryTerm = queryTermFactory.create(FlightMapping.FLIGHT_NUMBER, CriteriaOperatorEnum.EQUAL, fakeFlightNumber);
        ruleQueries.add(queryTerm);
        ApisMessage msg = integrationTestData.getApisMessage();
        UdrRule udrRule = createBaseUdrRule("123", date, date);
        udrRule.addEngineRule(generateRule(udrRule, ruleQueries));

        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);
        RuleExecutionContext ruleExecutionContext = TargetingServiceUtils.createApisRequest(msg);
        RuleServiceRequest request = ruleExecutionContext.getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request, drlRules);
        assertNotNull(result);
        assertEquals("Expected 0 hit", 0, result.getResultList().size());
    }


    @Test
    @Transactional
    public void testApisRuleExecution3() {
        // select all passengers in a flight
        Set<Passenger> passengers = createSetOfThreePassengersSameFirstName();
        integrationTestData = integrationTestBuilder
                .passengersOnApis(passengers)
                .build();

        List<QueryTerm> ruleQueries = new LinkedList<>();
        QueryTerm queryTerm = queryTermFactory.create(PassengerMapping.FIRST_NAME, CriteriaOperatorEnum.EQUAL, "SAME");
        ruleQueries.add(queryTerm);
        ApisMessage msg = integrationTestData.getApisMessage();

        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        UdrRule udrRule = createBaseUdrRule("123");
        udrRule.addEngineRule(generateRule(udrRule, ruleQueries));

        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);
        RuleServiceRequest request = TargetingServiceUtils.createApisRequest(
                msg).getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        assertEquals("Expected 3 hits", 3, result.getResultList().size());
        RuleHitDetail res = result.getResultList().get(0);
        assertNotNull("passenger ID in result is null", res.getPassengerId());
    }

    private Set<Passenger> createSetOfThreePassengersSameFirstName() {
        Passenger passenger = new Passenger();
        passenger.setPassengerType("P");
        passenger.setFirstName("SAME");
        passenger.setLastName("OneLname");

        Passenger passenger2 = new Passenger();
        passenger2.setPassengerType("P");
        passenger2.setFirstName("SAME");
        passenger2.setLastName("TWOLNAME");

        Passenger passenger3 = new Passenger();
        passenger3.setPassengerType("P");
        passenger3.setFirstName("SAME");
        passenger3.setLastName("THREELNAME");

        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);
        passengers.add(passenger2);
        passengers.add(passenger3);
        return passengers;
    }

    @Test
    @Transactional
    public void testApisRuleExecution4() throws ParseException {// apis seat
        // select all passengers in a flight
        Set<Passenger> passengers = createSetOfThreePassengersSameFirstName();
        integrationTestData = integrationTestBuilder
                .passengersOnApis(passengers)
                .build();

        List<QueryTerm> ruleQueries = new LinkedList<>();
        QueryTerm queryTerm = queryTermFactory.create(PassengerMapping.LAST_NAME, CriteriaOperatorEnum.NOT_IN, new String[] {"TWOLNAME", "THREELNAME"});
        ruleQueries.add(queryTerm);
        UdrRule udrRule = createBaseUdrRule("123");
        udrRule.addEngineRule(generateRule(udrRule, ruleQueries));
        DrlRuleFileBuilder drlBuilder = new DrlRuleFileBuilder();
        String drlRules = drlBuilder.addRule(udrRule).build();
        logger.info(drlRules);

        ApisMessage msg = integrationTestData.getApisMessage();
        RuleServiceRequest request = TargetingServiceUtils.createApisRequest(
                msg).getRuleServiceRequest();
        RuleServiceResult result = targetingService.applyRules(request,
                drlRules);
        assertNotNull(result);
        //John doe + Same OneLname
        assertEquals("Expected 2 hit", 2, result.getResultList().size());
        RuleHitDetail res = (RuleHitDetail) (result.getResultList().get(0));
        assertNotNull("passenger ID in result is null", res.getPassengerId());
    }

}
