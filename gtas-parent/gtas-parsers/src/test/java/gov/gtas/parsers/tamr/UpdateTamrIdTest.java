package gov.gtas.parsers.tamr;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import gov.gtas.model.PassengerIDTag;
import gov.gtas.parsers.ParserTestHelper;
import gov.gtas.parsers.tamr.model.TamrHistoryCluster;
import gov.gtas.parsers.tamr.model.TamrMessage;
import gov.gtas.parsers.tamr.model.TamrTravelerResponse;
import gov.gtas.repository.PassengerIDTagRepository;

/**
 * Test the handling of messages from Tamr that are supposed to update the
 * Tamr cluster IDs stored in GTAS. These include QUERY responses and
 * TH.DELTA and TH.CLUSTER messages.
 */
public class UpdateTamrIdTest implements ParserTestHelper {
    private TamrMessageHandlerService handler;
    private PassengerIDTagRepository passengerIDTagRepository;

    private static final long gtasId = 17957625;
    private static final String newTamrId =
            "c50feffb-a991-357f-a06e-0a034284d523";

    @Before
    public void setUp() {
        this.handler = new TamrMessageHandlerServiceImpl();
        this.passengerIDTagRepository = mock(PassengerIDTagRepository.class);
        ReflectionTestUtils.setField(handler,
                "passengerIDTagRepository", passengerIDTagRepository);

        // Set up the PassengerIDTagRepository to return a dummy instance when
        // queried.
        PassengerIDTag passengerIdTag = new PassengerIDTag();
        passengerIdTag.setPax_id(gtasId);
        given(passengerIDTagRepository.findByPaxId(gtasId)).willReturn(
                passengerIdTag);
    }
    
    @Test
    public void testQueryResponse() {
        TamrTravelerResponse travelerResponse = new TamrTravelerResponse();
        travelerResponse.setGtasId(Long.toString(gtasId));
        travelerResponse.setDerogIds(Collections.emptyList());
        travelerResponse.setTamrId(newTamrId);
        travelerResponse.setVersion(1);
        travelerResponse.setScore(1);
        
        TamrMessage message = new TamrMessage();
        message.setTravelerQuery(Collections.singletonList(travelerResponse));
        
        handler.handleQueryResponse(message);

        verify(passengerIDTagRepository).updateTamrId(
                gtasId, newTamrId);
    }
    
    @Test
    public void testDeltaMessageUpdate() {
        TamrHistoryCluster historyCluster = new TamrHistoryCluster();
        historyCluster.setGtasId(Long.toString(gtasId));
        historyCluster.setTamrId(newTamrId);
        historyCluster.setVersion(1);
        historyCluster.setAction("UPDATE");
        
        TamrMessage message = new TamrMessage();
        message.setHistoryClusters(Collections.singletonList(historyCluster));
        
        handler.handleTamrIdUpdate(message);

        verify(passengerIDTagRepository).updateTamrId(
                gtasId, newTamrId);
    }
    
    @Test
    public void testDeltaMessageDelete() {
        TamrHistoryCluster historyCluster = new TamrHistoryCluster();
        historyCluster.setGtasId(Long.toString(gtasId));
        historyCluster.setTamrId(null);
        historyCluster.setVersion(1);
        historyCluster.setAction("DELETE");
        
        TamrMessage message = new TamrMessage();
        message.setHistoryClusters(Collections.singletonList(historyCluster));
        
        handler.handleTamrIdUpdate(message);

        verify(passengerIDTagRepository).updateTamrId(
                gtasId, null);
    }
    
    @Test
    public void testClusterMessage() {
        TamrHistoryCluster historyCluster = new TamrHistoryCluster();
        historyCluster.setGtasId(Long.toString(gtasId));
        historyCluster.setTamrId(newTamrId);
        historyCluster.setVersion(1);
        historyCluster.setAction(null);
        
        TamrMessage message = new TamrMessage();
        message.setHistoryClusters(Collections.singletonList(historyCluster));
        
        handler.handleTamrIdUpdate(message);

        verify(passengerIDTagRepository).updateTamrId(
                gtasId, newTamrId);
    }
}
