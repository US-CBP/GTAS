package gov.gtas.parsers.tamr;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalMatchers.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;


import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.parsers.ParserTestHelper;
import gov.gtas.parsers.tamr.model.TamrDerogHit;
import gov.gtas.parsers.tamr.model.TamrMessage;
import gov.gtas.parsers.tamr.model.TamrTravelerResponse;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;

public class HandleTamrDerogHitsTest implements ParserTestHelper {
    private TamrMessageHandlerService handler;
    private WatchlistItem watchlistItem;
    private WatchlistItemRepository watchlistItemRepository;
    private PendingHitDetailRepository pendingHitDetailRepository;
    private Passenger passenger;
    private PassengerRepository passengerRepository;

    @Before
    public void setUp() {
        this.handler = new TamrMessageHandlerServiceImpl();

        this.watchlistItem = new WatchlistItem();
        watchlistItem.setId(94L);
        
        watchlistItemRepository = mock(WatchlistItemRepository.class);
        ReflectionTestUtils.setField(handler, "watchlistItemRepository",
                watchlistItemRepository);
        // Set up WatchlistItemRepository to return a fake watchlist item.
        given(watchlistItemRepository.findById(watchlistItem.getId()))
            .willReturn(Optional.of(watchlistItem));
        // Otherwise return null.
        given(watchlistItemRepository.findById(not(eq(watchlistItem.getId()))))
            .willReturn(Optional.empty());
   
        pendingHitDetailRepository = mock(PendingHitDetailRepository.class);
        ReflectionTestUtils.setField(handler, "pendingHitDetailRepository",
                pendingHitDetailRepository);


        passengerRepository = mock(PassengerRepository.class);
        ReflectionTestUtils.setField(handler, "passengerRepository",
                passengerRepository);

        // Set up the PassengerRepository to return a dummy instance when
        // queried.
        passenger = new Passenger();
        passenger.setId(1099L);
        Flight flight = new Flight();
        flight.setId(22L);
        passenger.setFlight(flight);
        given(passengerRepository.findById(passenger.getId())).willReturn(
                Optional.of(passenger));
        // Otherwise return null.
        given(passengerRepository.findById(not(eq(passenger.getId()))))
            .willReturn(Optional.empty());
    }
    
    @Test
    public void testHandleDerogHit() {
        float score = 0.76f;
        TamrMessage derogMessage = getDerogMessage(
                passenger.getId(), watchlistItem.getId(), score);
        handler.handleQueryResponse(derogMessage);

        final ArgumentCaptor<PendingHitDetails> pendingHitCaptor =
                ArgumentCaptor.forClass(PendingHitDetails.class);
        verify(pendingHitDetailRepository).save(pendingHitCaptor.capture());
        PendingHitDetails pendingHit = pendingHitCaptor.getValue();
        
        // We should mention Tamr somewhere in the title and description for
        // the derog hit so it's clear where it came from.
        assertTrue(pendingHit.getTitle().contains("Tamr"));
        assertTrue(pendingHit.getDescription().contains("Tamr"));

        assertEquals(pendingHit.getHitEnum().toString(),
                pendingHit.getHitType());
        assertEquals(HitTypeEnum.PARTIAL_WATCHLIST, pendingHit.getHitEnum());
        
        assertEquals(watchlistItem.getId(), pendingHit.getHitMakerId());
        assertEquals(score, pendingHit.getPercentage(), 0.00001f);

        assertEquals(passenger.getId(), pendingHit.getPassengerId());
        assertEquals(passenger.getFlight().getId(), pendingHit.getFlightId());
    }
    
    /**
     * Test when Tamr sends a nonexistent derogId.
     */
    @Test
    public void handleNonexistentDerogId() {
        TamrMessage derogMessage = getDerogMessage(
                passenger.getId(), 35L, 0.22f);
        handler.handleQueryResponse(derogMessage);
        
        verifyZeroInteractions(pendingHitDetailRepository);
    }
    
    /**
     * Test when Tamr sends a nonexistent gtasId.
     */
    @Test
    public void handleNonexistentGtasId() {
        TamrMessage derogMessage = getDerogMessage(
                547L, watchlistItem.getId(), 0.28f);
        handler.handleQueryResponse(derogMessage);
        
        verifyZeroInteractions(pendingHitDetailRepository);
    }
    
    /**
     * Constructs a QUERY response TamrMessage that includes matching the
     * given passenger with the given wachlist item.
     */
    private TamrMessage getDerogMessage(long gtasId, long derogId, float score) {
        TamrDerogHit derogHit = new TamrDerogHit();
        derogHit.setDerogId(Long.toString(derogId));
        derogHit.setScore(score);
        
        TamrTravelerResponse travelerResponse = new TamrTravelerResponse();
        travelerResponse.setGtasId(Long.toString(gtasId));
        travelerResponse.setDerogIds(Collections.singletonList(derogHit));
        travelerResponse.setTamrId(null);
        travelerResponse.setVersion(-1);
        travelerResponse.setScore(0);
        
        TamrMessage message = new TamrMessage();
        message.setTravelerQuery(Collections.singletonList(travelerResponse));
        return message;
    }
}
