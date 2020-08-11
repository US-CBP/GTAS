/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.parsers.omni.model.*;
import gov.gtas.repository.FlightPassengerRepository;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.repository.HitCategoryRepository;
import gov.gtas.model.lookup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.regex.*;

@Component
public class OmniMessageHandlerServiceImpl implements OmniMessageHandlerService {

    private final int MAX_DEBUG_PRINT_ROWS = 5;

    private final Logger logger = LoggerFactory.getLogger(OmniMessageHandlerServiceImpl.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    private PendingHitDetailRepository pendingHitDetailRepository;

    private FlightPassengerRepository flightPassengerRepository;

    @Autowired
    private HitCategoryRepository hitCategoryRepository;

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    @Value("${omni.derog_hit.title}")
    private String derogHitTitle;

    @Value("${omni.derog_hit.description}")
    private String derogHitDescription;

    public OmniMessageHandlerServiceImpl(
            PendingHitDetailRepository pendingHitDetailRepository,
            FlightPassengerRepository flightPassengerRepository,
            HitCategoryRepository hitCateogoryRepository) {
        this.pendingHitDetailRepository = pendingHitDetailRepository;
        this.flightPassengerRepository = flightPassengerRepository;
        this.hitCategoryRepository = hitCategoryRepository;
    }

    /**
     * Handle responses to Omni ASSESS_RISK_RESPONSE requests. This handles both the "derog
     * matches" and "traveler history" responses.
     */
    @Override
    public void handlePassengersRiskAssessmentResponse(String jsonStream) {
        try {
            OmniAssessPassengersResponse omniAssessPassengersResponse = objectMapper.readValue(jsonStream, OmniAssessPassengersResponse.class);
            String status = omniAssessPassengersResponse.getStatus();
            // logger.info("Omni returned passengers risk assessments JSON Stream: " + jsonStream);
            logger.info("Omni returned passengers risk assessments. Status: " + status);

            debugPrintOmniPassengersRiskAssessmentResponsePayload(omniAssessPassengersResponse);

            if (Objects.equals(status, "ERROR")) {
                return;
            }

            List<OmniModelPredictions> omniModelPredictionsList = omniAssessPassengersResponse.getPredictions();

            // Now consume this omniAssessPassengersResponse object
            List<OmniTravelerResponse> omniTravelerResponseList = new ArrayList<>();

            for(OmniModelPredictions entry: omniModelPredictionsList) {
                omniTravelerResponseList.add(convertOmniModelPredictionsToOmniTravelerResponse(entry));
            }

            // String jsonOmniTraverResponseList = objectMapper.writer().writeValueAsString(omniTravelerResponseList);

            // logger.info(" ========= About to save Kaizen Predictions as OmniTravelerResponseList={}", jsonOmniTraverResponseList);

            this.createPendingHits(omniTravelerResponseList);

        } catch (Exception ex) {
            logger.error("handlePassengersRiskAssessmentResponse() - Got an exception: ", ex);
        }
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private OmniTravelerResponse convertOmniModelPredictionsToOmniTravelerResponse(OmniModelPredictions omniModelPredictions) {
        double scoreDoubleValue = omniModelPredictions.getLabelAnyProb().get(0).doubleValue();
        float scoreFloatValue = (float) scoreDoubleValue;
        double cat1DoubleValue = omniModelPredictions.getCat1Prob().get(0).doubleValue();
        double cat2DoubleValue = omniModelPredictions.getCat2Prob().get(0).doubleValue();
        double cat3DoubleValue = omniModelPredictions.getCat3Prob().get(0).doubleValue();
        double cat4DoubleValue = omniModelPredictions.getCat4Prob().get(0).doubleValue();
        double cat5DoubleValue = omniModelPredictions.getCat5Prob().get(0).doubleValue();
        double cat6DoubleValue = omniModelPredictions.getCat6Prob().get(0).doubleValue();

        float cat1Prob = (float) cat1DoubleValue;
        float cat2Prob = (float) cat2DoubleValue;
        float cat3Prob = (float) cat3DoubleValue;
        float cat4Prob = (float) cat4DoubleValue;
        float cat5Prob = (float) cat5DoubleValue;
        float cat6Prob = (float) cat6DoubleValue;

        Iterable<HitCategory> allHitCategories = hitCategoryRepository.findAll();

        OmniTravelerResponse omniTravelerResponse = new OmniTravelerResponse();
        omniTravelerResponse.setPaxId(Long.toString(omniModelPredictions.getPassengerNumber()));
        omniTravelerResponse.setScore(scoreFloatValue);

        List<OmniDerogHit> omniDerogHitList = new ArrayList<>();

        allHitCategories.forEach( (hitCategory) -> {
            String categoryName = hitCategory.getName();
            Long id = hitCategory.getId();
            String idStr = Long.toString(id);
            switch(categoryName) {
                case OmniDerogUpdateScheduler.GTAS_HIT_CATEGORY_GENERAL:
                    if (cat1Prob > 0) {
                        OmniDerogHit omniDerogHit = new OmniDerogHit();
                        omniDerogHit.setDerogId(idStr);
                        omniDerogHit.setScore(cat1Prob);
                        omniDerogHitList.add(omniDerogHit);
                    }
                    break;
                case OmniDerogUpdateScheduler.GTAS_HIT_CATEGORY_TERRORISM:
                    if (cat2Prob > 0) {
                        OmniDerogHit omniDerogHit = new OmniDerogHit();
                        omniDerogHit.setDerogId(idStr);
                        omniDerogHit.setScore(cat2Prob);
                        omniDerogHitList.add(omniDerogHit);
                    }
                    break;
                case OmniDerogUpdateScheduler.GTAS_HIT_CATEGORY_WORLD_HEALTH:
                    if (cat3Prob > 0) {
                        OmniDerogHit omniDerogHit = new OmniDerogHit();
                        omniDerogHit.setDerogId(idStr);
                        omniDerogHit.setScore(cat3Prob);
                        omniDerogHitList.add(omniDerogHit);
                    }
                    break;
                case OmniDerogUpdateScheduler.GTAS_HIT_CATEGORY_FEDERAL_LAW_ENFORCEMENT:
                    if (cat4Prob > 0) {
                        OmniDerogHit omniDerogHit = new OmniDerogHit();
                        omniDerogHit.setDerogId(idStr);
                        omniDerogHit.setScore(cat4Prob);
                        omniDerogHitList.add(omniDerogHit);
                    }
                    break;
                case OmniDerogUpdateScheduler.GTAS_HIT_CATEGORY_LOCAL_LAW_ENFORCEMENT:
                    if (cat5Prob > 0) {
                        OmniDerogHit omniDerogHit = new OmniDerogHit();
                        omniDerogHit.setDerogId(idStr);
                        omniDerogHit.setScore(cat5Prob);
                        omniDerogHitList.add(omniDerogHit);
                    }
                    break;
                default:
                    if (cat6Prob > 0) {
                        OmniDerogHit omniDerogHit = new OmniDerogHit();
                        omniDerogHit.setDerogId(idStr);
                        omniDerogHit.setScore(cat6Prob);
                        omniDerogHitList.add(omniDerogHit);
                    }
                    break;
            }
        });

        omniTravelerResponse.setDerogIds(omniDerogHitList);

        return omniTravelerResponse;
    }

    /**
     * Creates and saves PendingHitDetails instances to the database for all
     * the given traveler responses from Omni which contain derog hits.
     */

    private void createPendingHits(List<OmniTravelerResponse> travelerResponses) {
        // List of (GTAS passenger ID, derog hit) pairs.
        List<AbstractMap.SimpleEntry<Long, OmniDerogHit>> derogHits =
                new ArrayList<>();

        Set<Long> passengerIds = new HashSet<>();

        Set<Long> watchlistItemIds = new HashSet<>();

        for (OmniTravelerResponse travelerResponse: travelerResponses) {
            for (OmniDerogHit derogHit: travelerResponse.getDerogIds()) {
                long paxId;
                long watchlistItemId;
                String derogIdStr = derogHit.getDerogId();

                if (!isNumeric(travelerResponse.getPaxId())) {
                    logger.warn("Omni returned derog hit for passenger with " +
                            "invalid ID \"{}\".", travelerResponse.getPaxId());
                    continue;
                }

                paxId = Long.parseLong(travelerResponse.getPaxId());

                if (!isNumeric(derogIdStr)) {
                    logger.warn("Omni returned derog hit for external hit " +
                                    "entry with invalid ID \"{}\".",
                            derogIdStr);
                    continue;
                }

                watchlistItemId = Long.parseLong(derogIdStr);

                passengerIds.add(paxId);
                watchlistItemIds.add(watchlistItemId);
                derogHits.add(new AbstractMap.SimpleEntry<>(paxId, derogHit));
            }
        }
        
        if (derogHits.isEmpty()) {
            logger.info("createPendingHits() - Got an empty derogHits list. So, nothting to do...");
            return;
        }

        // Construct a map between passenger IDs and flight IDs ...
        Map<Long, Long> passengerFlightIds = new HashMap<>();
        flightPassengerRepository.findAllByPassengerIds(passengerIds)
                .forEach((flightPassenger) -> passengerFlightIds.put(
                        flightPassenger.getPassengerId(),
                        flightPassenger.getFlightId()));

        // Now, make a list of valid pending hits.
        List<PendingHitDetails> pendingHits = new ArrayList<>();
        for(AbstractMap.SimpleEntry<Long, OmniDerogHit> derogHitWithId:
                derogHits) {
            long paxId = derogHitWithId.getKey();
            OmniDerogHit derogHit = derogHitWithId.getValue();
            long watchlistItemId = Long.parseLong(derogHit.getDerogId());

            PendingHitDetails pendingHit = new PendingHitDetails(); 

            pendingHit.setTitle(derogHitTitle);
            pendingHit.setDescription(derogHitDescription);

            pendingHit.setHitEnum(HitTypeEnum.EXTERNAL_HIT);
            pendingHit.setHitType(pendingHit.getHitEnum().toString());

            pendingHit.setHitMakerId(watchlistItemId);
            pendingHit.setPercentage(derogHit.getScore());

            // Omni doesn't return any details about the matching algorithm,
            // so we set this as empty.
            pendingHit.setRuleConditions("");
            
            // Find the associated flight ID for passenger.
            if (!passengerFlightIds.containsKey(paxId)) {
                logger.warn("Omni returned derog hit for nonexistent passenger " +
                        "with ID {}.", paxId);
                continue;
            }
            pendingHit.setFlightId(passengerFlightIds.get(paxId));

            pendingHit.setPassengerId(paxId);

            pendingHit.setCreatedDate(new Date());
            
            pendingHits.add(pendingHit);
        }
        
        // Finally, save all records to the database.
        if (!pendingHits.isEmpty()) {
            pendingHitDetailRepository.saveAll(pendingHits);
        }
    }

    private void debugPrintOmniPassengersRiskAssessmentResponsePayload(OmniAssessPassengersResponse omniAssessPassengersResponse) {
        try {
            OmniSupportInfo omniSupportInfo = omniAssessPassengersResponse.getSupportInfo();
            String status = omniAssessPassengersResponse.getStatus();

            String supportInfoDetails = omniSupportInfo.toString();
            logger.info("support info: {}", supportInfoDetails);

            if (Objects.equals(status, "ERROR")) {
                OmniErrorResponse omniErrorResponse = omniAssessPassengersResponse.getError();
                String errorMessage = omniErrorResponse.toString();
                logger.error("Got an error: {}", errorMessage);
                return;
            }

            int maxElementShown = MAX_DEBUG_PRINT_ROWS;
            List<OmniModelPredictions> omniModelPredictionsList = omniAssessPassengersResponse.getPredictions();
            int totalSize = omniModelPredictionsList.size();
            Long startTime = omniAssessPassengersResponse.getStartPredictionTimeMillisecs();
            Long endTime = omniAssessPassengersResponse.getEndPredictionTimeMillisecs();

            if (null != startTime && null != endTime) {
                Long delta = endTime - startTime;
                logger.info("Kaizen Model Predictions Start Time: " + startTime + " millisecs");
                logger.info("Kaizen Model Predictions End Time: " + endTime + " millisecs");
                logger.info("It took " + delta + " millisecs to run inference on the batach of " + totalSize + " OmniRawProfiles");
            }

            if (totalSize == 0) {
                logger.error("Got an empty model predictions list");
                return;
            }

            if (totalSize < maxElementShown) {
                maxElementShown = totalSize;
            }

            List<OmniModelPredictions> displayOmniModelPredictionsBucket = new ArrayList<>();

            for (OmniModelPredictions omniModelPredictions : omniModelPredictionsList) {
                displayOmniModelPredictionsBucket.add(omniModelPredictions);
            }

            String displayOmniModelPredictionsTitle = "========= Showing the first " + maxElementShown + " OmniModelPredictions Received from Kaizen =======";

            if (maxElementShown == 1) {
                displayOmniModelPredictionsTitle = "========= Showing the only OmniModelPredictions Received from Kaizen =======";
            }

            String jsonPayload = objectMapper.writer().writeValueAsString(displayOmniModelPredictionsBucket);

            logger.info(displayOmniModelPredictionsTitle);
            logger.info(jsonPayload);

        } catch (Exception ex) {
            logger.error("debugPrintOmniPassengersRiskAssessmentResponsePayload() - Got an exception: ", ex);
        }
    }
}
