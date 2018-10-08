/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule;

import static gov.gtas.IntegrationTestBuilder.MessageTypeGenerated.APIS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import gov.gtas.IntegrationTestBuilder;
import gov.gtas.IntegrationTestData;
import gov.gtas.bo.RuleExecutionStatistics;
import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.CommonServiceException;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.TargetingServiceUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuleServiceConfig.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class RuleRepositoryIT {
    @Autowired
    private RuleService testTarget;

    @Autowired
    private IntegrationTestBuilder integrationTestBuilder;

    private IntegrationTestData integrationTestData;

    @Before
    @Transactional
    public void setUp() {
    }

    @After
    @Transactional
    public void tearDown() {
        integrationTestBuilder.reset();
    }


    @Test(expected = CommonServiceException.class)
    @Transactional
    public void testNullRequest() {
        testTarget.invokeAdhocRules("gtas.drl", null);
    }

    @Test
    @Transactional
    public void testBasicApisRequest() {
        integrationTestData = integrationTestBuilder.messageType(APIS).build();
        integrationTestData.getPassenger().setEmbarkation("Timbuktu");
        integrationTestData.getFlight().setFlightNumber("FAKE");
        integrationTestData.getFlightPaxApis().setBagWeight(0);
        RuleExecutionContext ruleExecutionContext = TargetingServiceUtils.createApisRequest(integrationTestData.getApisMessage());
        RuleServiceRequest ruleServiceRequest = ruleExecutionContext.getRuleServiceRequest();
        String filePathForDrl = RuleServiceConstants.DEFAULT_RULESET_NAME;
        RuleServiceResult result = testTarget.invokeAdhocRules(filePathForDrl, ruleServiceRequest);
        RuleExecutionStatistics executionStatistics = result.getExecutionStatistics();
        assertNotNull(result);
        assertNotNull(result.getResultList());
        assertEquals("Result list is empty", 2, result.getResultList().size());
        //Will hit on 2 gts.dl rules. Same passenger will be returned so pick the first one.
        RuleHitDetail ruleHitDetail = result.getResultList().get(0);
        assertEquals("Expected Passenger", integrationTestData.getPassenger(), ruleHitDetail.getPassenger());
        assertNotNull(executionStatistics);
        assertEquals("Expected 2 rules to be fired", 2,
                executionStatistics.getTotalRulesFired());
        assertEquals("Expected 2 rule names in list", 2, executionStatistics
                .getRuleFiringSequence().size());

        assertEquals("Expected 3 object to be affected", 3,
                executionStatistics.getTotalObjectsModified());
        assertEquals("Expected 3 object to be inserted", 3, executionStatistics
                .getInsertedObjectClassNameList().size());
    }
}
