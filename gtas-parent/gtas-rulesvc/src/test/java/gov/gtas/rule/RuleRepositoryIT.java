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
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuleServiceConfig.class)
@Rollback(true)
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
        integrationTestData.getFlight().setFlightNumber("testFlightNum");
        integrationTestData.getFlightPaxApis().setBagWeight(0);

        RuleExecutionContext ruleExecutionContext = TargetingServiceUtils.createApisRequest(integrationTestData.getApisMessage());
        RuleServiceRequest ruleServiceRequest = ruleExecutionContext.getRuleServiceRequest();
        String filePathForDrl = RuleServiceConstants.DEFAULT_RULESET_NAME;
        RuleServiceResult result = testTarget.invokeAdhocRules(filePathForDrl, ruleServiceRequest);
        RuleExecutionStatistics executionStatistics = result.getExecutionStatistics();

        assertNotNull(result);
        assertNotNull(result.getResultList());
        assertEquals("Result list is empty", 1, result.getResultList().size());
        //Odd placement - Passenger lives in a RuleHitDetail list due to how the drl file processes.
        assertEquals("Expected Passenger", integrationTestData.getPassenger(), result.getResultList().get(0));
        assertNotNull(executionStatistics);
        assertEquals("Expected 2 rules to be fired", 2,
                executionStatistics.getTotalRulesFired());
        assertEquals("Expected 2 rule names in list", 2, executionStatistics
                .getRuleFiringSequence().size());
        // Expecting 1 apis flight pax + 1 flight + 1 passenger to be affected / used.
        assertEquals("Expected 3 object to be affected", 3,
                executionStatistics.getTotalObjectsModified());
        assertEquals("Expected 3 object to be inserted", 3, executionStatistics
                .getInsertedObjectClassNameList().size());
    }
}
