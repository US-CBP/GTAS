/*
 *  All Application code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import gov.gtas.parsers.ParserTestHelper;
import gov.gtas.parsers.omni.jms.OmniMessageReceiver;
import gov.gtas.parsers.omni.model.OmniMessageType;
import gov.gtas.parsers.omni.model.OmniAssessPassengersResponse;

public class ReceiveOmniMessagesTest implements ParserTestHelper {
    private static final String ASSESS_RISK_RESPONSE_PAYLOAD = "/omni-messages/assess_passengers_response.json";

    private OmniMessageReceiver receiver;
    private OmniMessageHandlerService handler;

    @Before
    public void setUp() {
        this.handler = mock(OmniMessageHandlerService.class);
        this.receiver = new OmniMessageReceiver(handler);
    }
    
    @Test
    public void testAssessRiskResponse() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("ASSESS_RISK_RESPONSE");
        given(message.getText()).willReturn(getMessageText(ASSESS_RISK_RESPONSE_PAYLOAD));
        
        receiver.receive(message);

        verify(handler).handlePassengersRiskAssessmentResponse(anyString());
    }

    @Test
    public void testDerogUpdateResponse() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("ASSESS_RISK_RESPONSE");
        given(message.getText()).willReturn(getMessageText(ASSESS_RISK_RESPONSE_PAYLOAD));

        receiver.receive(message);

        verify(handler).handlePassengersRiskAssessmentResponse(anyString());
    }

    @Test
    public void testUnknownMessageType() throws JMSException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("UNKNOWN");
        given(message.getText()).willReturn("{}");
        
        receiver.receive(message);
        
        // Message should not make it through to the handler.
        verifyZeroInteractions(handler);
    }

    @Test
    public void testMalformedMessage() throws JMSException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("ASSESS_RISK_REQUEST");
        given(message.getText()).willReturn("{");
        receiver.receive(message);

        message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("ASSESS_RISK_REQUEST");
        given(message.getText()).willReturn("{\"foo\": \"bar\"}");
        receiver.receive(message);

        // Malformed messages should not make it through to the handler.
        verifyZeroInteractions(handler);
    }
}
