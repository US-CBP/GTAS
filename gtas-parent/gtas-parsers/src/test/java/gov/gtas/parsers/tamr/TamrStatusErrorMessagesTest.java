package gov.gtas.parsers.tamr;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.Invocation;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import gov.gtas.parsers.ParserTestHelper;
import gov.gtas.parsers.tamr.model.TamrMessage;
import gov.gtas.parsers.tamr.model.TamrMessageType;
import gov.gtas.parsers.tamr.model.TamrRecordError;

/**
 * Test that various JMS status and error messages returned from the Tamr API
 * are properly logged.
 */
public class TamrStatusErrorMessagesTest implements ParserTestHelper {
    private TamrMessageHandlerService handler;
    private Logger logger;

    @Before
    public void setUp() {
        this.handler = new TamrMessageHandlerServiceImpl();
        this.logger = mock(Logger.class);
        ReflectionTestUtils.setField(handler, "logger", logger);
    }
    
    @Test
    public void testAcknowledgmentSuccess() {
        String[] messageTypes = {"TH.UPDATE", "DC.REPLACE"};
        for (String messageType: messageTypes) {
            this.handler.handleAcknowledgeResponse(
                    this.buildAcknowledgmentResponse(messageType));
            
            this.assertTextLogged("INFO", "acknowledg");
            this.assertTextLogged("INFO", messageType);
        }
    }
    
    @Test
    public void testAcknowledgmentError() {
        TamrMessage acknowledgmentMessage = new TamrMessage();
        acknowledgmentMessage.setMessageType(TamrMessageType.TH_UPDATE);
        acknowledgmentMessage.setAcknowledgment(false);
        acknowledgmentMessage.setError("Here is an error.");
        this.handler.handleAcknowledgeResponse(acknowledgmentMessage);

        // Make sure message type and error is logged as ERROR.
        this.assertTextLogged("ERROR", "TH.UPDATE");
        this.assertTextLogged("ERROR",
                acknowledgmentMessage.getError());
    }
    
    @Test
    public void testRecordErrors() {
        String[] messageTypeStrs = {"QUERY", "DC.REPLACE", "TH.UPDATE"};
        for (String messageTypeStr: messageTypeStrs) {
            TamrMessageType messageType = TamrMessageType
                    .fromString(messageTypeStr);
            TamrMessage errorMessage = new TamrMessage();
            errorMessage.setMessageType(messageType);
            List<TamrRecordError> recordErrors = new ArrayList<TamrRecordError>();
            TamrRecordError recordError = new TamrRecordError();
            recordError.setErrors(Arrays.asList(
                    new String [] {"Error A", "Error B"}));
            recordErrors.add(recordError);
            errorMessage.setRecordErrors(recordErrors);
            
            if (messageType == TamrMessageType.QUERY) {
                handler.handleQueryResponse(errorMessage);
            } else {
                handler.handleAcknowledgeResponse(errorMessage);
            }
            
            // Make sure message type and all errors are logged as WARN.
            this.assertTextLogged("WARN", messageTypeStr);
            for (String error: recordError.getErrors()) {
                this.assertTextLogged("WARN", error);
            }
        }
    }
    
    /**
     * Test messages of type ERROR.
     */
    @Test
    public void testErrorMessages() {
        TamrMessage errorMessage = new TamrMessage();
        errorMessage.setMessageType(TamrMessageType.ERROR);
        errorMessage.setAcknowledgment(false);
        errorMessage.setError("Error error");
        
        handler.handleErrorResponse(errorMessage);
        
        // Make sure error message is logged.
        this.assertTextLogged("ERROR", errorMessage.getError());
    }
    
    private TamrMessage buildAcknowledgmentResponse(String messageTypeStr) {
        TamrMessage message = new TamrMessage();
        message.setAcknowledgment(true);
        message.setMessageType(TamrMessageType.fromString(messageTypeStr));
        return message;
    }
    
    /**
     * Asserts the given message part was logged as part of a message with the
     * given log level. For instance,
     *     assertTextLogged("WARN", "problem")
     * will ensure that a warning was logged with the word "problem" included.
     */
    private void assertTextLogged(String logLevel, String messagePart) {
        Collection<Invocation> invocations =
                mockingDetails(logger).getInvocations();
        boolean messagePartPresent = false;
        for (Invocation invocation: invocations) {
            if (logLevel.toLowerCase().equals(
                    invocation.getMethod().getName())) {
                for (Object argument: invocation.getArguments()) {
                    if (argument != null) {
                        if (argument.toString().contains(messagePart)) {
                            messagePartPresent = true;
                        }
                    }
                }
            }
        }
        assertTrue(String.format("%s present in %s message", messagePart,
                logLevel), messagePartPresent);
    }
}
