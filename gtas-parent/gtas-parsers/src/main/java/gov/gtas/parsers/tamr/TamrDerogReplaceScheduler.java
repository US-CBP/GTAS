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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.gtas.model.watchlist.Watchlist;
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
 
    private final String PASSENGER_WATCHLIST_NAME = "Passenger";
    
    @Value("${tamr.derog-replace.batchSize}")
    private Integer batchSize;
    
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
    @Scheduled(fixedDelayString = "${tamr.derog-replace.fixed-delay.milliseconds}",
            initialDelayString = "${tamr.derog-replace.initial-delay.milliseconds}")
    public void jobScheduling() throws InterruptedException {
        logger.info("Checking for watchlist edits to send to Tamr...");
        
        Watchlist passengerWatchlist =
                watchlistRepository.getWatchlistByName(PASSENGER_WATCHLIST_NAME);
        if (passengerWatchlist == null) return;
        
        // Check the latest time the watchlist was edited.
        Date latestWatchlistEdit = passengerWatchlist.getEditTimestamp();
        
        // If there has been an edit to a watchlist since the last time this
        // job was run...
        if (lastRun == null || lastRun.before(latestWatchlistEdit)) {
            logger.info("Sending latest watchlist to Tamr.");

            List<TamrDerogListEntry> derogListEntries =
                    new ArrayList<TamrDerogListEntry>();
            
            // Loop through the watchlist items in batches to avoid too much
            // database load.
            List<WatchlistItem> watchlistItemsBatch;
            int batchIndex = 0;
            do {
                Pageable batchPage = PageRequest.of(
                        batchIndex, batchSize);
                watchlistItemsBatch = watchlistItemRepository
                        .getItemsByWatchlistName(
                                PASSENGER_WATCHLIST_NAME, batchPage);
                logger.info("watchlistItemsBatch = {}", watchlistItemsBatch);
                derogListEntries.addAll(tamrAdapter.convertWatchlist(
                        watchlistItemsBatch));

                batchIndex++;
            } while (watchlistItemsBatch.size() == batchSize);
            
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
