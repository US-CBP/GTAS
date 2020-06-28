/*
 * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.parsers.tamr.TamrDerogReplaceScheduler;
import gov.gtas.parsers.tamr.jms.TamrMessageSender;
import gov.gtas.parsers.tamr.model.TamrMessageType;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;

public class TamrDerogReplaceSchedulerTest {
    private TamrDerogReplaceScheduler scheduler;
    private TamrMessageSender messageSender;
    private WatchlistItemRepository watchlistItemRepository;
    private Watchlist passengerWatchlist;
    private WatchlistRepository watchlistRepository;

    @Before
    public void setUp() {
        passengerWatchlist = new Watchlist("Passenger", EntityEnum.PASSENGER);
        passengerWatchlist.setId(1L);
        passengerWatchlist.setEditTimestamp(new Date());
        watchlistRepository = mock(WatchlistRepository.class);
        given(watchlistRepository.findAll())
                .willReturn(Collections.singletonList(passengerWatchlist));
        given(watchlistRepository.getWatchlistByName("Passenger"))
                .willReturn(passengerWatchlist);
        
        List<WatchlistItem> watchlistItems =
                TamrAdapterImplTest.getWatchlistItems();
        watchlistItemRepository = mock(WatchlistItemRepository.class);
        given(watchlistItemRepository.findAll()).willReturn(watchlistItems);
        given(watchlistItemRepository.getItemsByWatchlistName("Passenger"))
            .willReturn(watchlistItems);
        doAnswer(new Answer<List<WatchlistItem>>() {
            public List<WatchlistItem> answer(InvocationOnMock invocation) {
                Pageable pageable = invocation.getArgument(1);
                int fromIndex = Math.min(
                        (int) pageable.getOffset(), watchlistItems.size());
                int toIndex = Math.min(
                        (int) pageable.getOffset() + pageable.getPageSize(),
                        watchlistItems.size());
                return watchlistItems.subList(fromIndex, toIndex);
            }
        }).when(watchlistItemRepository).getItemsByWatchlistName(
                eq("Passenger"), any(Pageable.class));
   
        messageSender = mock(TamrMessageSender.class);
        doCallRealMethod().when(messageSender).sendMessageToTamr(any(), any());

        scheduler = new TamrDerogReplaceScheduler(
                watchlistRepository,
                watchlistItemRepository,
                messageSender,
                new TamrAdapterImpl(null));
        
        ReflectionTestUtils.setField(scheduler, "batchSize", 3);
    }
    
    /**
     * Make sure watchlist items are sent to Tamr the first time the scheduler
     * runs.
     */
    @Test
    public void testSendWatchlist() throws InterruptedException {
        scheduler.jobScheduling();

        ArgumentCaptor<TamrMessageType> messageTypeCaptor =
                ArgumentCaptor.forClass(TamrMessageType.class);
        ArgumentCaptor<String> messageTextCaptor =
                ArgumentCaptor.forClass(String.class);
        verify(messageSender).sendTextMessageToTamr(
                messageTypeCaptor.capture(), messageTextCaptor.capture());
        assertEquals(TamrMessageType.DC_REPLACE, messageTypeCaptor.getValue());
        assertEquals(TamrAdapterImplTest.expectedDerogListJson,
                messageTextCaptor.getValue());
    }
    
    /**
     * Make sure derog replace isn't sent to Tamr when the watchlists haven't
     * been edited.
     * @throws InterruptedException 
     */
    @Test
    @Ignore
    public void testNoSendIfNotEdited() throws InterruptedException {
        scheduler.jobScheduling();
        scheduler.jobScheduling();
        scheduler.jobScheduling();

        // Only one message should have been sent.
        verify(messageSender, times(1)).sendTextMessageToTamr(
                any(), any());
        
        // Now update edited time...
        passengerWatchlist.setEditTimestamp(new Date());
        // ...and another message should be sent.
        scheduler.jobScheduling();
        scheduler.jobScheduling();
        verify(messageSender, times(2)).sendTextMessageToTamr(
                any(), any());
    }
}
