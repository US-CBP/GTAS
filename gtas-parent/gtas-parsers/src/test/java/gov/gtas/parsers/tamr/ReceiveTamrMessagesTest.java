package gov.gtas.parsers.tamr;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import gov.gtas.parsers.ParserTestHelper;
import gov.gtas.parsers.tamr.jms.TamrMessageReceiver;
import gov.gtas.parsers.tamr.model.TamrMessage;

public class ReceiveTamrMessagesTest implements ParserTestHelper { 
    private static final String HISTORY_QUERY_RESPONSE = "/tamr-messages/history_query_response.json";  
    private static final String DEROG_QUERY_RESPONSE = "/tamr-messages/derog_query_response.json";  
    private static final String HISTORY_UPDATE_RESPONSE = "/tamr-messages/history_update_response.json";  
    private static final String DEROG_REPLACE_RESPONSE = "/tamr-messages/derog_replace_response.json"; 
    private static final String CLUSTERS_MESSAGE = "/tamr-messages/clusters_message.json";
    private static final String DELTAS_MESSAGE = "/tamr-messages/deltas_message.json";
    private static final String RECORD_ERRORS_RESPONSE = "/tamr-messages/record_errors_response.json";
    private static final String ERROR_MESSAGE = "/tamr-messages/error_message.json";  
    
    private TamrMessageReceiver receiver;
    private TamrMessageHandlerService handler;

    @Before
    public void setUp() {
        this.receiver = new TamrMessageReceiver();
        this.handler = mock(TamrMessageHandlerService.class);
        ReflectionTestUtils.setField(receiver, "tamrMessageHandler", handler);
    }
    
    @Test
    public void testHistoryQueryResponse() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("QUERY");
        given(message.getText()).willReturn(getMessageText(HISTORY_QUERY_RESPONSE));
        
        receiver.receive(message);

        verify(handler).handleQueryResponse(any(TamrMessage.class));
        verify(handler, never()).handleAcknowledgeResponse(any());
        verify(handler, never()).handleTamrIdUpdate(any());
        verify(handler, never()).handleErrorResponse(any());
    }
    
    @Test
    public void testDerogQueryResponse() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("QUERY");
        given(message.getText()).willReturn(getMessageText(DEROG_QUERY_RESPONSE));
        
        receiver.receive(message);

        verify(handler).handleQueryResponse(any(TamrMessage.class));
        verify(handler, never()).handleAcknowledgeResponse(any());
        verify(handler, never()).handleTamrIdUpdate(any());
        verify(handler, never()).handleErrorResponse(any());
    }
    
    @Test
    public void testHistoryUpdateResponse() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("TH.UPDATE");
        given(message.getText()).willReturn(getMessageText(HISTORY_UPDATE_RESPONSE));
        
        receiver.receive(message);

        verify(handler).handleAcknowledgeResponse(any(TamrMessage.class));
        verify(handler, never()).handleQueryResponse(any());
        verify(handler, never()).handleTamrIdUpdate(any());
        verify(handler, never()).handleErrorResponse(any());
    }
    
    @Test
    public void testDerogReplaceResponse() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("DC.REPLACE");
        given(message.getText()).willReturn(getMessageText(DEROG_REPLACE_RESPONSE));
        
        receiver.receive(message);

        verify(handler).handleAcknowledgeResponse(any(TamrMessage.class));
        verify(handler, never()).handleQueryResponse(any());
        verify(handler, never()).handleTamrIdUpdate(any());
        verify(handler, never()).handleErrorResponse(any());
    }

    @Test
    public void testClustersMessage() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("TH.CLUSTERS");
        given(message.getText()).willReturn(getMessageText(CLUSTERS_MESSAGE));
        
        receiver.receive(message);

        verify(handler).handleTamrIdUpdate(any(TamrMessage.class));
        verify(handler, never()).handleQueryResponse(any());
        verify(handler, never()).handleAcknowledgeResponse(any());
        verify(handler, never()).handleErrorResponse(any());
    }
    
    @Test
    public void testDeltasMessage() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("TH.DELTAS");
        given(message.getText()).willReturn(getMessageText(DELTAS_MESSAGE));
        
        receiver.receive(message);

        verify(handler).handleTamrIdUpdate(any(TamrMessage.class));
        verify(handler, never()).handleQueryResponse(any());
        verify(handler, never()).handleAcknowledgeResponse(any());
        verify(handler, never()).handleErrorResponse(any());
    }
    
    @Test
    public void testRecordErrors() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("QUERY");
        given(message.getText()).willReturn(getMessageText(RECORD_ERRORS_RESPONSE));
        
        receiver.receive(message);

        verify(handler).handleQueryResponse(any(TamrMessage.class));
        verify(handler, never()).handleAcknowledgeResponse(any());
        verify(handler, never()).handleTamrIdUpdate(any());
        verify(handler, never()).handleErrorResponse(any());
    }
    
    @Test
    public void testErrorMessage() throws JMSException, IOException, URISyntaxException {
        TextMessage message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("ERROR");
        given(message.getText()).willReturn(getMessageText(ERROR_MESSAGE));
        
        receiver.receive(message);

        verify(handler).handleErrorResponse(any(TamrMessage.class));
        verify(handler, never()).handleQueryResponse(any());
        verify(handler, never()).handleAcknowledgeResponse(any());
        verify(handler, never()).handleTamrIdUpdate(any());
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
        given(message.getJMSType()).willReturn("QUERY");
        given(message.getText()).willReturn("{");
        receiver.receive(message);

        message = mock(TextMessage.class);
        given(message.getJMSType()).willReturn("QUERY");
        given(message.getText()).willReturn("{\"foo\": \"bar\"}");
        receiver.receive(message);

        // Malformed messages should not make it through to the handler.
        verifyZeroInteractions(handler);
    }
    
    /**
     * Make sure the JMS message type is stored properly in the parsed
     * TamrMessage.
     */
    @Test
    public void testMessageTypeStored() throws JMSException {
        TextMessage jmsMessage = mock(TextMessage.class);
        given(jmsMessage.getJMSType()).willReturn("DC.REPLACE");
        given(jmsMessage.getText()).willReturn("{}");
        
        receiver.receive(jmsMessage);

        final ArgumentCaptor<TamrMessage> messageCaptor =
                ArgumentCaptor.forClass(TamrMessage.class);
        verify(handler).handleAcknowledgeResponse(messageCaptor.capture());
        assertEquals("DC.REPLACE", messageCaptor.getValue().getMessageType());
    }
}
