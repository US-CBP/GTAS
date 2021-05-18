package gov.gtas.parsers.tamr;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.PassengerIDTag;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.parsers.tamr.model.TamrDerogHit;
import gov.gtas.parsers.tamr.model.TamrHistoryCluster;
import gov.gtas.parsers.tamr.model.TamrHistoryClusterAction;
import gov.gtas.parsers.tamr.model.TamrMessage;
import gov.gtas.parsers.tamr.model.TamrTravelerResponse;
import gov.gtas.repository.FlightPassengerRepository;
import gov.gtas.repository.PassengerIDTagRepository;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;

@Component
@ConditionalOnProperty(prefix = "tamr", name = "enabled")
public class TamrMessageHandlerServiceImpl implements TamrMessageHandlerService {

    private final Logger logger = LoggerFactory.getLogger(TamrMessageHandlerServiceImpl.class);

    private PassengerIDTagRepository passengerIDTagRepository;
    
    private WatchlistItemRepository watchlistItemRepository;
    
    private PendingHitDetailRepository pendingHitDetailRepository;
        
    private FlightPassengerRepository flightPassengerRepository;
    
    @Value("${tamr.derog_hit.title}")
    String derogHitTitle;
    
    @Value("${tamr.derog_hit.description}")
    String derogHitDescription;
    
    public TamrMessageHandlerServiceImpl(
            PassengerIDTagRepository passengerIDTagRepository,
            WatchlistItemRepository watchlistItemRepository,
            PendingHitDetailRepository pendingHitDetailRepository,
            FlightPassengerRepository flightPassengerRepository) {
        this.passengerIDTagRepository = passengerIDTagRepository;
        this.watchlistItemRepository = watchlistItemRepository;
        this.pendingHitDetailRepository = pendingHitDetailRepository;
        this.flightPassengerRepository = flightPassengerRepository;
    }

    /**
     * Handle responses to Tamr QUERY requests. This handles both the "derog
     * matches" and "traveler history" responses.
     */
    @Override
    public void handleQueryResponse(TamrMessage response) {
        if (this.checkRecordErrors(response)) return;

        Map<Long, String> gtasIdToTamrId = new HashMap<Long, String>();
        for (TamrTravelerResponse travelerResponse: response.getTravelerQuery()) {
            if (travelerResponse.getTamrId() != null) {
                this.addTamrIdUpdate(gtasIdToTamrId, travelerResponse.getGtasId(),
                        travelerResponse.getTamrId());
            }
        }
        this.updateTamrIds(gtasIdToTamrId);
        
        this.createPendingHits(response.getTravelerQuery());
    }
    
    /**
     * Creates and saves PendingHitDetails instances to the database for all
     * the given traveler responses from Tamr which contain derog hits.
     */
    private void createPendingHits(List<TamrTravelerResponse> travelerResponses) {
        // List of (GTAS passenger ID, derog hit) pairs.
        List<AbstractMap.SimpleEntry<Long, TamrDerogHit>> derogHits =
                new ArrayList<>();

        Set<Long> passengerIds = new HashSet<>();
        Set<Long> watchlistItemIds = new HashSet<>();

        for (TamrTravelerResponse travelerResponse: travelerResponses) {
            for (TamrDerogHit derogHit: travelerResponse.getDerogIds()) {
                long gtasId, watchlistItemId;
                try {
                    gtasId = Long.parseLong(travelerResponse.getGtasId());
                } catch (NumberFormatException e) {
                    logger.warn("Tamr returned derog hit for passenger with " +
                            "invalid ID \"{}\".", travelerResponse.getGtasId());
                    continue;
                }
                try {
                    watchlistItemId = Long.parseLong(derogHit.getDerogId()); 
                } catch (NumberFormatException e) {
                    logger.warn("Tamr returned derog hit for watchlist " +
                            "entry with invalid ID \"{}\".",
                            derogHit.getDerogId());
                    continue;
                }
                
                passengerIds.add(gtasId);
                watchlistItemIds.add(watchlistItemId);
                derogHits.add(new AbstractMap.SimpleEntry<>(gtasId, derogHit));
            }
        }
        
        if (derogHits.isEmpty()) return;

        // Construct a map from passenger IDs to flight IDs...
        Map<Long, Long> passengerFlightIds = new HashMap<>();
        flightPassengerRepository.findAllByPassengerIds(passengerIds)
                .forEach((flightPassenger) -> passengerFlightIds.put(
                        flightPassenger.getPassengerId(),
                        flightPassenger.getFlightId()));

        // ...and from watchlist item IDs to watchlist items.
        Map<Long, WatchlistItem> watchlistItems = new HashMap<>();
        watchlistItemRepository.findAllById(watchlistItemIds)
                .forEach((watchlistItem) -> watchlistItems.put(
                        watchlistItem.getId(), watchlistItem));


        // Now, make a list of valid pending hits.
        List<PendingHitDetails> pendingHits = new ArrayList<>();
        for(AbstractMap.SimpleEntry<Long, TamrDerogHit> derogHitWithId:
                derogHits) {
            long gtasId = derogHitWithId.getKey();
            TamrDerogHit derogHit = derogHitWithId.getValue();

            PendingHitDetails pendingHit = new PendingHitDetails(); 

            pendingHit.setTitle(derogHitTitle);
            pendingHit.setDescription(derogHitDescription);

            pendingHit.setHitEnum(HitTypeEnum.PARTIAL_WATCHLIST);
            pendingHit.setHitType(pendingHit.getHitEnum().toString());

            long watchlistItemId = Long.parseLong(derogHit.getDerogId());
            WatchlistItem watchlistItem = watchlistItems.get(watchlistItemId);
            if (watchlistItem != null) {
                pendingHit.setHitMakerId(watchlistItem.getId());
            } else {
                logger.warn("Tamr returned derog hit for nonexistent watchlist " +
                        "entry with ID {}.", watchlistItemId);
                continue;
            }
            
            pendingHit.setPercentage(derogHit.getScore()); 

            // Tamr doesn't return any details about the matching algorithm,
            // so leave this empty.
            pendingHit.setRuleConditions("");
            
            // Find associated flight ID for passenger.
            if (!passengerFlightIds.containsKey(gtasId)) {
                logger.warn("Tamr returned derog hit for nonexistent passenger " +
                        "with ID {}.", gtasId);
                continue;
            }
            pendingHit.setFlightId(passengerFlightIds.get(gtasId));

            pendingHit.setPassengerId(gtasId);

            pendingHit.setCreatedDate(new Date());
            
            pendingHits.add(pendingHit);
        }
        
        // Finally, save them all to the database.
        if (!pendingHits.isEmpty()) {
            pendingHitDetailRepository.saveAll(pendingHits);
        }
    }

    /**
     * Handles an acknowledgement response from Tamr. This response is received
     * for requests that don't give any particular response data.
     */
    @Override
    public void handleAcknowledgeResponse(TamrMessage response) {
        if (this.checkRecordErrors(response)) {
            // Record errors are already logged.
        } else if (response.getAcknowledgment() == null) {
            logger.warn("{} message received from Tamr with no " +
                    "\"acknowledgment\" key. Ignoring...");
        } else if (response.getAcknowledgment()) {
            logger.info("{} request to Tamr acknowledged",
                    response.getMessageType());
        } else {
            logger.error("Error with {} request to Tamr: {}",
                    response.getMessageType(), response.getError());
        }
    }

    /**
     * Handles QUERY, TH.CLUSTERS, and TH.DELTAS messages from Tamr. These all
     * require similar functionality: updating the Tamr cluster IDs.
     */
    @Override
    public void handleTamrIdUpdate(TamrMessage message) {
        // The "error" field can be set on these.
        if (message.getError() != null) {
            logger.error("Tamr error in {} message: {}",
                    message.getMessageType(), message.getError());
            return;
        }

        Map<Long, String> gtasIdToTamrId = new HashMap<Long, String>();
        for (TamrHistoryCluster cluster: message.getHistoryClusters()) {
            if (cluster.getAction() == null ||
                    cluster.getAction() == TamrHistoryClusterAction.UPDATE) {
                addTamrIdUpdate(gtasIdToTamrId, cluster.getGtasId(),
                        cluster.getTamrId());
            } else if (cluster.getAction() == TamrHistoryClusterAction.DELETE) {
                addTamrIdUpdate(gtasIdToTamrId, cluster.getGtasId(), null);
            } else {
                logger.warn("Unknown history cluster action \"{}\" received " +
                        "from Tamr for passenger {}. Ignoring...",
                        cluster.getAction(), cluster.getGtasId());
            }
        }
        
        updateTamrIds(gtasIdToTamrId);
    }
    
    /**
     * Add a (GTAS ID, Tamr ID) pair to a map that can then be passed to
     * updateTamrIds.
     */
    private void addTamrIdUpdate(Map<Long, String> gtasIdToTamrId,
            String gtasIdStr, String tamrId) {
        long gtasId;
        try {
            gtasId = Long.parseLong(gtasIdStr);
        } catch (NumberFormatException e) {
            logger.warn("Unable to update tamrId of passenger with invalid " +
                    "ID \"{}\".", gtasIdStr);
            return;
        }
        gtasIdToTamrId.put(gtasId, tamrId);
    }
    
    /**
     * Given a map from GTAS IDs to Tamr IDs (or null, if a Tamr ID should be
     * deleted), this will update the Passenger ID Tags for the given IDs
     * to have the given Tamr IDs.
     */
    private void updateTamrIds(Map<Long, String> gtasIdToTamrId) {
        if (gtasIdToTamrId.isEmpty()) return;
        
        // Get existing PassengerIDTags.
        Map<Long, PassengerIDTag> passengerIDTags = new HashMap<>();
        passengerIDTagRepository.findAllById(gtasIdToTamrId.keySet())
                .forEach((passengerIDTag) -> passengerIDTags.put(
                        passengerIDTag.getPax_id(), passengerIDTag));
        
        // Update PassengerIDTags and save to database in one batch.
        gtasIdToTamrId.forEach((gtasId, tamrId) -> {
            if (passengerIDTags.containsKey(gtasId)) {
                passengerIDTags.get(gtasId).setTamrId(tamrId);
            } else {
                logger.warn("Unable to update tamrId of nonexistent passenger " +
                        "ID tag {}.", gtasId);
            }
        });
        passengerIDTagRepository.saveAll(passengerIDTags.values());
    }
    
    /**
     * Handles ERROR messages from Tamr. These can happen during Tamr
     * processing or for any error unrelated to a request.
     */
    @Override
    public void handleErrorResponse(TamrMessage response) {
        logger.error("{} message received from Tamr: {}",
                response.getMessageType(), response.getError());
    }
    
    /**
     * Checks to see if recordErrors are present on the given response. If so,
     * returns true and logs a warning message.
     */
    private boolean checkRecordErrors(TamrMessage response) {
        boolean hasRecordErrors = response.getRecordErrors() != null &&
                response.getRecordErrors().size() > 0;
        if (hasRecordErrors) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String recordErrorsJson = mapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(response.getRecordErrors());
                logger.warn("Received recordErrors from Tamr for {} " +
                        "request. recordErrors = {}",
                        response.getMessageType(), recordErrorsJson);
            } catch (JsonProcessingException e) {
                logger.warn("Received recordErrors from Tamr for {} " +
                        "request. Unable to display errors.",
                        response.getMessageType());
            }
        }
        return hasRecordErrors;
    }
}
