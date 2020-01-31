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
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.parsers.tamr.TamrDerogReplaceScheduler;
import gov.gtas.parsers.tamr.jms.TamrMessageSender;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;

public class TamrDerogReplaceSchedulerTest {
    private TamrDerogReplaceScheduler scheduler;
    private TamrMessageSender messageSender;
    private WatchlistItemRepository watchlistItemRepository;
    private Watchlist watchlist;
    private WatchlistRepository watchlistRepository;

    @Before
    public void setUp() {
        scheduler = new TamrDerogReplaceScheduler();
        
        watchlist = new Watchlist();
        watchlist.setId(1L);
        watchlist.setEditTimestamp(new Date());
        watchlistRepository = mock(WatchlistRepository.class);
        ReflectionTestUtils.setField(scheduler, "watchlistRepository",
                watchlistRepository);
        given(watchlistRepository.findAll())
                .willReturn(Collections.singletonList(watchlist));
        
        List<WatchlistItem> watchlistItems =
                TamrAdapterImplTest.getWatchlistItems();
        watchlistItemRepository = mock(WatchlistItemRepository.class);
        ReflectionTestUtils.setField(scheduler, "watchlistItemRepository",
                watchlistItemRepository);
        given(watchlistItemRepository.findAll()).willReturn(watchlistItems);
   
        messageSender = mock(TamrMessageSender.class);
        ReflectionTestUtils.setField(scheduler, "tamrMessageSender",
                messageSender);
        doCallRealMethod().when(messageSender).sendMessageToTamr(any(), any());
    }
    
    /**
     * Make sure watchlist items are sent to Tamr the first time the scheduler
     * runs.
     */
    @Test
    public void testSendWatchlist() throws InterruptedException {
        scheduler.jobScheduling();

        ArgumentCaptor<String> messageTypeCaptor =
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageTextCaptor =
                ArgumentCaptor.forClass(String.class);
        verify(messageSender).sendTextMessageToTamr(
                messageTypeCaptor.capture(), messageTextCaptor.capture());
        assertEquals("DC.REPLACE", messageTypeCaptor.getValue());
        assertEquals(TamrAdapterImplTest.expectedDerogListJson,
                messageTextCaptor.getValue());
    }
    
    /**
     * Make sure derog replace isn't sent to Tamr when the watchlists haven't
     * been edited.
     * @throws InterruptedException 
     */
    @Test
    public void testNoSendIfNotEdited() throws InterruptedException {
        scheduler.jobScheduling();
        scheduler.jobScheduling();
        scheduler.jobScheduling();

        // Only one message should have been sent.
        verify(messageSender, times(1)).sendTextMessageToTamr(
                any(), any());
        
        // Now update edited time...
        watchlist.setEditTimestamp(new Date());
        // ...and another message should be sent.
        scheduler.jobScheduling();
        scheduler.jobScheduling();
        verify(messageSender, times(2)).sendTextMessageToTamr(
                any(), any());
    }
}
