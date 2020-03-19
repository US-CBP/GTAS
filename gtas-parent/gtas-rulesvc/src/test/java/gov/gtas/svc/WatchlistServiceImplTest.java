/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.svc;

import gov.gtas.services.watchlist.WatchlistPersistenceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WatchlistServiceImplTest {

    @Mock
    WatchlistPersistenceService watchlistPersistenceService;

    @InjectMocks
    WatchlistServiceImpl watchlistService;

    List<Long> emptyList = new ArrayList<>();

    @Before
    public void before() {
     }

    @Test
    public void testListOf1() {
        List<Long> listOfIds = new ArrayList<>();
        listOfIds.add(1L);
        watchlistService.deleteWatchlistItems(listOfIds);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyList() {
        watchlistService.deleteWatchlistItems(emptyList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullList() {
        watchlistService.deleteWatchlistItems(null);
    }
}