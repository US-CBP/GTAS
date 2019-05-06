/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.gtas.bo.BasicRuleServiceResult;
import gov.gtas.bo.RuleExecutionStatistics;
import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.MessageStatus;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.rule.RuleService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the TargetingService using Mockito.
 */
@Ignore
public class TargetingServiceTest {
    private TargetingService targetingService;

    @Mock
    private RuleService mockRuleService;

    @Mock
    private ApisMessageRepository mockApisMsgRepository;
/*
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        targetingService = new TargetingServiceImpl(mockRuleService);
        ReflectionTestUtils.setField(targetingService, "apisMsgRepository",
                mockApisMsgRepository);
    }*/

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Ignore
    public void testInitialization() {
        assertNotNull("Autowire of targeting service failed", targetingService);
        assertNotNull("Autowire of rule service failed",
                ReflectionTestUtils.getField(targetingService, "ruleService"));
    }

    @Test
    @Ignore // move to an IT test class to get needed autowired services.
    public void testAnalyzeApisMessage() {
        ReflectionTestUtils.setField(targetingService, "ruleService",
                mockRuleService);
        ApisMessage message = new ApisMessage();
        RuleServiceResult result = new BasicRuleServiceResult(
                new LinkedList<RuleHitDetail>(), new RuleExecutionStatistics());
        when(mockRuleService.invokeRuleEngine(any(RuleServiceRequest.class)))
                .thenReturn(result);
        targetingService.analyzeApisMessage(message);
        verify(mockRuleService).invokeRuleEngine(any(RuleServiceRequest.class));
    }
}
