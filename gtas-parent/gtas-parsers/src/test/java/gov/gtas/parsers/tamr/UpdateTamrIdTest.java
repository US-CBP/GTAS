package gov.gtas.parsers.tamr;

import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;

import gov.gtas.model.PassengerIDTag;
import gov.gtas.parsers.ParserTestHelper;
import gov.gtas.parsers.tamr.model.TamrHistoryCluster;
import gov.gtas.parsers.tamr.model.TamrHistoryClusterAction;
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
        this.passengerIDTagRepository = mock(PassengerIDTagRepository.class);

        // Set up the PassengerIDTagRepository to return a dummy instance when
        // queried.
        PassengerIDTag passengerIdTag = new PassengerIDTag();
        passengerIdTag.setPax_id(gtasId);
        
        ArgumentMatcher<Iterable<Long>> containsGtasId =
                new ArgumentMatcher<Iterable<Long>>() {
            @Override
            public boolean matches(Iterable<Long> ids) {
                if (ids == null) return false;
                for (Long id: ids) {
                    if (id == gtasId) return true;
                }
                return false;
            }
        };
        
        given(passengerIDTagRepository.findAllById(argThat(containsGtasId)))
                .willReturn(Collections.singleton(passengerIdTag));
        given(passengerIDTagRepository.findAllById(not(argThat(containsGtasId))))
                .willReturn(Collections.EMPTY_LIST);

        this.handler = new TamrMessageHandlerServiceImpl(
                passengerIDTagRepository,
                null, null, null);
    }
    
    private void verifyTamrIdUpated(long gtasId, String newTamrId) {
        final ArgumentCaptor<Iterable<PassengerIDTag>> passengerIDTagsCaptor =
                ArgumentCaptor.forClass(Iterable.class);
        verify(passengerIDTagRepository).saveAll(
                passengerIDTagsCaptor.capture());
        boolean tamrIdUpdated = false;
        for(PassengerIDTag passengerIDTag: passengerIDTagsCaptor.getValue()) {
            if (passengerIDTag.getPax_id() == gtasId) {
                assertEquals(passengerIDTag.getTamrId(), newTamrId);
                tamrIdUpdated = true;
            }
        }
        assertTrue(tamrIdUpdated);
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

        verifyTamrIdUpated(gtasId, newTamrId);
    }
    
    @Test
    public void testDeltaMessageUpdate() {
        TamrHistoryCluster historyCluster = new TamrHistoryCluster();
        historyCluster.setGtasId(Long.toString(gtasId));
        historyCluster.setTamrId(newTamrId);
        historyCluster.setVersion(1);
        historyCluster.setAction(TamrHistoryClusterAction.UPDATE);
        
        TamrMessage message = new TamrMessage();
        message.setHistoryClusters(Collections.singletonList(historyCluster));
        
        handler.handleTamrIdUpdate(message);

        verifyTamrIdUpated(gtasId, newTamrId);
    }
    
    @Test
    public void testDeltaMessageDelete() {
        TamrHistoryCluster historyCluster = new TamrHistoryCluster();
        historyCluster.setGtasId(Long.toString(gtasId));
        historyCluster.setTamrId(null);
        historyCluster.setVersion(1);
        historyCluster.setAction(TamrHistoryClusterAction.DELETE);
        
        TamrMessage message = new TamrMessage();
        message.setHistoryClusters(Collections.singletonList(historyCluster));
        
        handler.handleTamrIdUpdate(message);

        verifyTamrIdUpated(gtasId, null);
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

        verifyTamrIdUpated(gtasId, newTamrId);
    }
    
    /**
     * Make sure invalid or nonexistent gtasIds are handled correctly.
     */
    @Test
    public void testInvalidGtasId() {
        List<TamrTravelerResponse> travelerResponses = new ArrayList<>();
        
        TamrTravelerResponse travelerResponse = new TamrTravelerResponse();
        travelerResponse.setGtasId("abc");
        travelerResponse.setDerogIds(Collections.emptyList());
        travelerResponse.setTamrId(newTamrId);
        travelerResponse.setVersion(1);
        travelerResponse.setScore(1);
        travelerResponses.add(travelerResponse);
        
        travelerResponse = new TamrTravelerResponse();
        travelerResponse.setGtasId("3");
        travelerResponse.setDerogIds(Collections.emptyList());
        travelerResponse.setTamrId(newTamrId);
        travelerResponse.setVersion(1);
        travelerResponse.setScore(1);
        travelerResponses.add(travelerResponse);
        
        TamrMessage message = new TamrMessage();
        message.setTravelerQuery(travelerResponses);

        handler.handleQueryResponse(message);

        final ArgumentCaptor<Iterable<PassengerIDTag>> passengerIDTagsCaptor =
                ArgumentCaptor.forClass(Iterable.class);
        verify(passengerIDTagRepository).saveAll(
                passengerIDTagsCaptor.capture());
        List<PassengerIDTag> updatedPassengerIDTags =
                new ArrayList<PassengerIDTag>();
        passengerIDTagsCaptor.getValue().forEach(updatedPassengerIDTags::add);
        assertTrue(updatedPassengerIDTags.isEmpty());
    }
}
