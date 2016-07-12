/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the TargetingService using Mockito.
 */
public class TargetingServiceTest {
    private TargetingService targetingService;

    @Mock
    private RuleService mockRuleService;

    @Mock
    private ApisMessageRepository mockApisMsgRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        targetingService = new TargetingServiceImpl(mockRuleService);
        ReflectionTestUtils.setField(targetingService, "apisMsgRepository",
                mockApisMsgRepository);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInitialization() {
        assertNotNull("Autowire of targeting service failed", targetingService);
        assertNotNull("Autowire of rule service failed",
                ReflectionTestUtils.getField(targetingService, "ruleService"));
    }

    @Test
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

    @Test
    public void testRetrieveApisMessage() {
        ReflectionTestUtils.setField(targetingService, "apisMsgRepository",
                mockApisMsgRepository);
        List<ApisMessage> messages = new ArrayList<ApisMessage>();
        ApisMessage nApisMessage = new ApisMessage();
        nApisMessage.setStatus(MessageStatus.LOADED);
        messages.add(nApisMessage);
        MessageStatus messageStatus = MessageStatus.LOADED;
        when(mockApisMsgRepository.findByStatus(messageStatus)).thenReturn(
                messages);
        targetingService.retrieveApisMessage(messageStatus);
        verify(mockApisMsgRepository).findByStatus(messageStatus);
    }

    @Test
    public void testNullReturnRetrieveApisMessage() {
        ReflectionTestUtils.setField(targetingService, "apisMsgRepository",
                mockApisMsgRepository);
        List<ApisMessage> messages = new ArrayList<ApisMessage>();
        MessageStatus messageStatus = MessageStatus.LOADED;
        when(mockApisMsgRepository.findByStatus(messageStatus)).thenReturn(
                messages);
        List<ApisMessage> result = targetingService
                .retrieveApisMessage(messageStatus);
        verify(mockApisMsgRepository).findByStatus(messageStatus);
        assertEquals(new ArrayList<>(0), result);
    }
}
