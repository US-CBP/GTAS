/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.util.SampleDataGenerator;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchlistBuilderTest {


    private static final Logger logger = LoggerFactory.getLogger(WatchlistBuilderTest.class);
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBuildwatchlistFromSpec() {
        WatchlistBuilder bldr = new WatchlistBuilder(SampleDataGenerator.createSampleWatchlist("WLTest1"));
        bldr.buildPersistenceLists();
        assertEquals("WLTest1", bldr.getName());
        List<WatchlistItem> items = bldr.getCreateUpdateList();
        List<WatchlistItem> deleteItems = bldr.getDeleteList();
        assertNotNull(items);
        assertNotNull(deleteItems);
        assertEquals(2, items.size());
        assertEquals(1, deleteItems.size());
        assertNotNull(deleteItems.get(0).getId());
        for(WatchlistItem item:items){
            assertNotNull(item.getItemData());
            logger.info(item.getItemData());
        }
    }

    @Test
    public void testBuildWatchlistSpecFromWl() {
        final String wlname = "WLTest1";
        WatchlistBuilder bldr = new WatchlistBuilder(SampleDataGenerator.createSampleWatchlist(wlname));
        bldr.buildPersistenceLists();
        assertEquals(wlname, bldr.getName());
        List<WatchlistItem> items = bldr.getCreateUpdateList();
        assertNotNull(items);
        Watchlist wl = new Watchlist(wlname, EntityEnum.PASSENGER);
        bldr = new WatchlistBuilder(wl, items);
        WatchlistSpec spec = bldr.buildWatchlistSpec();
        assertNotNull(spec);
        assertEquals(2, spec.getWatchlistItems().size());
    }
}
