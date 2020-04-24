package gov.gtas.parsers.tamr;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.ArgumentMatchers.argThat;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.springframework.test.util.ReflectionTestUtils;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightPassenger;
import gov.gtas.model.Passenger;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.parsers.tamr.model.TamrDerogHit;
import gov.gtas.parsers.tamr.model.TamrMessage;
import gov.gtas.parsers.tamr.model.TamrTravelerResponse;
import gov.gtas.repository.FlightPassengerRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;

public class HandleTamrDerogHitsTest {
    private TamrMessageHandlerService handler;
    private WatchlistItem watchlistItem;
    private WatchlistItemRepository watchlistItemRepository;
    private PendingHitDetailRepository pendingHitDetailRepository;
    private FlightPassengerRepository flightPassengerRepository;
    private Passenger passenger;
    private Flight flight;
    
    private String derogHitTitle = "Derog Title";
    private String derogHitDescription = "Derog description.";
    
    @Before
    public void setUp() {
        this.watchlistItem = new WatchlistItem();
        watchlistItem.setId(94L);
        
        // Dummy passenger and flight instance.
        passenger = new Passenger();
        passenger.setId(1099L);
        flight = new Flight();
        flight.setId(22L);
        
        watchlistItemRepository = mock(WatchlistItemRepository.class);
        // Set up WatchlistItemRepository to return a fake watchlist item.
        given(watchlistItemRepository.findAllById(
                argThat(new ContainsMatcher<Long>(watchlistItem.getId()))))
            .willReturn(Collections.singletonList(watchlistItem));
        // Otherwise return empty list.
        given(watchlistItemRepository.findAllById(
                not(argThat(new ContainsMatcher<Long>(watchlistItem.getId())))))
            .willReturn(Collections.EMPTY_LIST);
   
        pendingHitDetailRepository = mock(PendingHitDetailRepository.class);

        flightPassengerRepository = mock(FlightPassengerRepository.class);

        
        
        // Set up the PassengerRepository to return a dummy instance when
        // queried.
        FlightPassenger flightPassenger = new FlightPassenger();
        flightPassenger.setFlightId(flight.getId());
        flightPassenger.setPassengerId(passenger.getId());
        given(flightPassengerRepository.findAllByPassengerIds(
                argThat(new ContainsMatcher<Long>(passenger.getId()))))
            .willReturn(Collections.singletonList(flightPassenger));
        // Otherwise return empty list.
        given(flightPassengerRepository.findAllByPassengerIds(
                not(argThat(new ContainsMatcher<Long>(passenger.getId())))))
            .willReturn(Collections.EMPTY_LIST);

        this.handler = new TamrMessageHandlerServiceImpl(
                null,
                watchlistItemRepository,
                pendingHitDetailRepository,
                flightPassengerRepository);
        ReflectionTestUtils.setField(handler, "derogHitTitle",
                derogHitTitle);
        ReflectionTestUtils.setField(handler, "derogHitDescription",
                derogHitDescription);

    }
    
    @Test
    public void testHandleDerogHit() {
        float score = 0.76f;
        TamrMessage derogMessage = getDerogMessage(
                passenger.getId(), watchlistItem.getId(), score);
        handler.handleQueryResponse(derogMessage);

        final ArgumentCaptor<Iterable<PendingHitDetails>> pendingHitCaptor =
                ArgumentCaptor.forClass(Iterable.class);
        verify(pendingHitDetailRepository).saveAll(pendingHitCaptor.capture());
        PendingHitDetails pendingHit = pendingHitCaptor.getValue()
                .iterator().next();
        
        assertTrue(pendingHit.getTitle().equals(derogHitTitle));
        assertTrue(pendingHit.getDescription().equals(derogHitDescription));

        assertEquals(pendingHit.getHitEnum().toString(),
                pendingHit.getHitType());
        assertEquals(HitTypeEnum.PARTIAL_WATCHLIST, pendingHit.getHitEnum());
        
        assertEquals(watchlistItem.getId(), pendingHit.getHitMakerId());
        assertEquals(score, pendingHit.getPercentage(), 0.00001f);

        assertEquals(passenger.getId(), pendingHit.getPassengerId());
        assertEquals(flight.getId(), pendingHit.getFlightId());
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
     * Test when Tamr sends an invalid (non-numeric) derogId.
     */
    @Test
    public void handleInvalidDerogId() {
        TamrMessage derogMessage = getDerogMessage(
                passenger.getId(), watchlistItem.getId(), 1);
        derogMessage.getTravelerQuery().get(0).getDerogIds().get(0)
            .setDerogId("abc");
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
     * Test when Tamr sends an invalid (non-numeric) gtasId.
     */
    @Test
    public void handleInvalidGtasId() {
        TamrMessage derogMessage = getDerogMessage(
                passenger.getId(), watchlistItem.getId(), 1);
        derogMessage.getTravelerQuery().get(0).setGtasId("abc");
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

    /**
     * Mockito argument matcher that matches any iterable argument which
     * contains the given item.
     */
    private class ContainsMatcher<T> implements ArgumentMatcher<Iterable<T>> {
        private T element;
        public ContainsMatcher(T element) {
            this.element = element;
        }
        @Override
        public boolean matches(Iterable<T> argument) {
            if (argument == null) return false;
            for (T otherElement: argument) {
                if (otherElement.equals(element)) return true;
            }
            return false;
        }
    }
}
