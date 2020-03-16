/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.parsers.tamr.jms.TamrMessageSender;
import gov.gtas.parsers.tamr.model.TamrDerogListEntry;
import gov.gtas.parsers.tamr.model.TamrDerogListUpdate;
import gov.gtas.parsers.tamr.model.TamrMessageType;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.FlightService;

/**
 * Scheduler class for sending the derog list (watchlist) to Tamr when changed.
 * Uses Spring's Scheduled annotation for scheduling tasks. The class reads
 * configuration values from an external file.
 * 
 * @author Cassidy Laidlaw
 */
@Component
@ConditionalOnProperty(prefix = "tamr", name = "enabled")
public class TamrDerogReplaceScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(TamrDerogReplaceScheduler.class);
    
    WatchlistRepository watchlistRepository;
    
    WatchlistItemRepository watchlistItemRepository;
    
    TamrAdapter tamrAdapter;
    
    TamrMessageSender tamrMessageSender;

    private Date lastRun = null;

    public TamrDerogReplaceScheduler(
            WatchlistRepository watchlistRepository,
            WatchlistItemRepository watchlistItemRepository,
            TamrMessageSender tamrMessageSender,
            TamrAdapter tamrAdapter) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistItemRepository = watchlistItemRepository;
        this.tamrMessageSender = tamrMessageSender;
        this.tamrAdapter = tamrAdapter;
    }

    /**
     * Replace the watchlist in Tamr if the GTAS one has changed.
     **/
    @Scheduled(fixedDelayString = "${tamr.derogReplace.fixedDelay.in.milliseconds}",
            initialDelayString = "${tamr.derogReplace.initialDelay.in.milliseconds}")
    public void jobScheduling() throws InterruptedException {
        logger.info("Checking for watchlist edits to send to Tamr...");
        
        // Get the latest time that a watchlist was edited.
        Optional<Date> latestWatchlistEdit = StreamSupport.stream(
                watchlistRepository.findAll().spliterator(), false)
            .map((watchlist) -> watchlist.getEditTimestamp())
            .collect(Collectors.maxBy(Comparator.naturalOrder()));
        
        // Return if there are no watchlists to send.
        if (!latestWatchlistEdit.isPresent()) return;
        
        // If there has been an edit to a watchlist since the last time this
        // job was run...
        if (lastRun == null || lastRun.before(latestWatchlistEdit.get())) {
            logger.info("Sending latest watchlist to Tamr.");
          
            List<WatchlistItem> watchlistItems = new ArrayList<>();
            watchlistItemRepository.findAll().forEach(watchlistItems::add);
            List<TamrDerogListEntry> derogListEntries =
                    tamrAdapter.convertWatchlist(watchlistItems);
            TamrDerogListUpdate derogReplace =
                    new TamrDerogListUpdate(derogListEntries);
            tamrMessageSender.sendMessageToTamr(
                    TamrMessageType.DC_REPLACE, derogReplace);
            
            lastRun = new Date();
        } else {
            logger.info("No recent watchlist edits found.");
        }
    }

}
