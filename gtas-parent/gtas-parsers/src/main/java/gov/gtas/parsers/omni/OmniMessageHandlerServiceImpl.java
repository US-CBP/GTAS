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
import gov.gtas.services.AppConfigurationService;
import gov.gtas.model.HitDetail;
import gov.gtas.model.ExternalHit;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.parsers.omni.model.OmniAssessPassengersResponse;
import gov.gtas.parsers.omni.model.OmniSupportInfo;
import gov.gtas.parsers.omni.model.OmniErrorResponse;
import gov.gtas.parsers.omni.model.OmniModelPredictions;
import gov.gtas.parsers.omni.model.OmniTravelerResponse;
import gov.gtas.parsers.omni.model.OmniTravelerResponse;
import gov.gtas.parsers.omni.model.OmniDerogHit;

import gov.gtas.repository.FlightPassengerRepository;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.repository.HitCategoryRepository;
import gov.gtas.repository.HitMakerRepository;
import gov.gtas.repository.ExternalHitRepository;
import gov.gtas.model.lookup.HitCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.Set;

@Component
@ConditionalOnProperty(prefix = "omni", name = "enabled")
public class OmniMessageHandlerServiceImpl implements OmniMessageHandlerService {
    private static final String MATCHING_THRESHOLD = "MATCHING_THRESHOLD";

    @Autowired
    private final AppConfigurationService appConfigurationService;

    private final int OMNI_DEROG_INDEX = 1;
    private final int MAX_DEBUG_PRINT_ROWS = 2;

    private final Logger logger = LoggerFactory.getLogger(OmniMessageHandlerServiceImpl.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    private PendingHitDetailRepository pendingHitDetailRepository;

    private FlightPassengerRepository flightPassengerRepository;

    @Autowired
    private HitCategoryRepository hitCategoryRepository;

    @Autowired
    private HitMakerRepository hitMakerRepository;

    @Autowired
    private ExternalHitRepository externalHitRepository;

    @Autowired
    private OmniDerogUpdateService omniDerogUpdateService;

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    @Value("${omni.derog_hit.title}")
    private String derogHitTitle;

    @Value("${omni.derog_hit.description}")
    private String derogHitDescription;

    public OmniMessageHandlerServiceImpl(AppConfigurationService appConfigurationService,
            PendingHitDetailRepository pendingHitDetailRepository,
            FlightPassengerRepository flightPassengerRepository,
            HitCategoryRepository hitCateogoryRepository) {
        this.appConfigurationService = appConfigurationService;
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
            OmniSupportInfo omniSupportInfo = omniAssessPassengersResponse.getSupportInfo();

            logger.info("Omni returned passengers risk assessments. Status: " + status);

            String supportInfoDetails = omniSupportInfo.toString();
            logger.info("support info: {}", supportInfoDetails);

            if (Objects.equals(status, "ERROR")) {
                OmniErrorResponse omniErrorResponse = omniAssessPassengersResponse.getError();
                String errorMessage = omniErrorResponse.toString();
                logger.error("Got an error: {}", errorMessage);
                return;
            }

            debugPrintOmniPassengersRiskAssessmentResponsePayload(omniAssessPassengersResponse);

            List<OmniModelPredictions> omniModelPredictionsList = omniAssessPassengersResponse.getPredictions();

            // Now consume this omniAssessPassengersResponse object
            List<OmniTravelerResponse> omniTravelerResponseList = new ArrayList<>();

            for(OmniModelPredictions entry: omniModelPredictionsList) {
                omniTravelerResponseList.add(convertOmniModelPredictionsToOmniTravelerResponse(entry));
            }

            debugPrintOmniTravelerResponsePayload(omniTravelerResponseList);

            this.createPendingHits(omniTravelerResponseList);

        } catch (Exception ex) {
            logger.error("handlePassengersRiskAssessmentResponse() - Got an exception: ", ex);
        }
    }

    @Override
    public void handleHitDetailsAvailableNotification(String jsonStream) {
        try {
            // logger.info("handleHitDetailsAvailableNotification() got jsonStream: {}", jsonStream);

            Set<HitDetail> firstTimeHits = new HashSet<>();
            Set<Long> flightIds = new HashSet<>();

            ArrayNode hitDetailsNodeArray = (ArrayNode) objectMapper.readTree(jsonStream);

            if (null != hitDetailsNodeArray && hitDetailsNodeArray.isArray()) {
                for (JsonNode jsonNodeEntry : hitDetailsNodeArray) {
                    HitDetail hitDetail = objectMapper.readValue(jsonNodeEntry.toString(), HitDetail.class);
                    flightIds.add(hitDetail.getFlightId());
                    firstTimeHits.add(hitDetail);
                }
            }

            // String jsonFirstTimeHits = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firstTimeHits);
            // logger.info("serialized hit details: {}", jsonFirstTimeHits);

            // String jsonFlightIds = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(flightIds);
            // logger.info("serialized hit flight Ids: {}", jsonFlightIds);

            if (firstTimeHits.size() > 0) {
                omniDerogUpdateService.updateOmniDerogPassengers(flightIds);
            }

        } catch (Exception ex) {
            logger.error("handleHitDetailsAvailableNotification() - Got an exception: ", ex);
        }
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private OmniTravelerResponse convertOmniModelPredictionsToOmniTravelerResponse(OmniModelPredictions omniModelPredictions) {
        OmniTravelerResponse omniTravelerResponse = new OmniTravelerResponse();
        try {
            Double matchingThreshold = Double.parseDouble(appConfigurationService.findByOption(MATCHING_THRESHOLD).getValue());

            String jsonPayload = objectMapper.writer().writeValueAsString(omniModelPredictions);

            // logger.info("========= omniModelPredictions={} =======", jsonPayload);

            double scoreDoubleValue = omniModelPredictions.getLabelAnyProb().get(OMNI_DEROG_INDEX).doubleValue();
            float scoreFloatValue = (float) scoreDoubleValue;
            double cat1DoubleValue = omniModelPredictions.getCat1Prob().get(OMNI_DEROG_INDEX).doubleValue();
            double cat2DoubleValue = omniModelPredictions.getCat2Prob().get(OMNI_DEROG_INDEX).doubleValue();
            double cat3DoubleValue = omniModelPredictions.getCat3Prob().get(OMNI_DEROG_INDEX).doubleValue();
            double cat4DoubleValue = omniModelPredictions.getCat4Prob().get(OMNI_DEROG_INDEX).doubleValue();
            double cat5DoubleValue = omniModelPredictions.getCat5Prob().get(OMNI_DEROG_INDEX).doubleValue();
            double cat6DoubleValue = omniModelPredictions.getCat6Prob().get(OMNI_DEROG_INDEX).doubleValue();

            float cat1Prob = (float) cat1DoubleValue;
            float cat2Prob = (float) cat2DoubleValue;
            float cat3Prob = (float) cat3DoubleValue;
            float cat4Prob = (float) cat4DoubleValue;
            float cat5Prob = (float) cat5DoubleValue;
            float cat6Prob = (float) cat6DoubleValue;

            Iterable<ExternalHit> allExternalHits = externalHitRepository.findAll();

            omniTravelerResponse.setPaxId(Long.toString(omniModelPredictions.getPassengerNumber()));
            omniTravelerResponse.setScore(scoreFloatValue);

            List<OmniDerogHit> omniDerogHitList = new ArrayList<>();

            allExternalHits.forEach((externalHit) -> {
                String categoryName = externalHit.getDescription();
                Long id = externalHit.getId();
                String idStr = Long.toString(id);

                switch (categoryName) {
                    case OmniDerogUpdateServiceImpl.GTAS_HIT_CATEGORY_GENERAL:
                        if (cat1Prob > matchingThreshold) {
                            OmniDerogHit omniDerogHit = new OmniDerogHit();
                            omniDerogHit.setDerogId(idStr);
                            omniDerogHit.setScore(cat1Prob);
                            omniDerogHitList.add(omniDerogHit);
                        }
                        break;
                    case OmniDerogUpdateServiceImpl.GTAS_HIT_CATEGORY_TERRORISM:
                        if (cat2Prob > matchingThreshold) {
                            OmniDerogHit omniDerogHit = new OmniDerogHit();
                            omniDerogHit.setDerogId(idStr);
                            omniDerogHit.setScore(cat2Prob);
                            omniDerogHitList.add(omniDerogHit);
                        }
                        break;
                    case OmniDerogUpdateServiceImpl.GTAS_HIT_CATEGORY_WORLD_HEALTH:
                        if (cat3Prob > matchingThreshold) {
                            OmniDerogHit omniDerogHit = new OmniDerogHit();
                            omniDerogHit.setDerogId(idStr);
                            omniDerogHit.setScore(cat3Prob);
                            omniDerogHitList.add(omniDerogHit);
                        }
                        break;
                    case OmniDerogUpdateServiceImpl.GTAS_HIT_CATEGORY_FEDERAL_LAW_ENFORCEMENT:
                        if (cat4Prob > matchingThreshold) {
                            OmniDerogHit omniDerogHit = new OmniDerogHit();
                            omniDerogHit.setDerogId(idStr);
                            omniDerogHit.setScore(cat4Prob);
                            omniDerogHitList.add(omniDerogHit);
                        }
                        break;
                    case OmniDerogUpdateServiceImpl.GTAS_HIT_CATEGORY_LOCAL_LAW_ENFORCEMENT:
                        if (cat5Prob > matchingThreshold) {
                            OmniDerogHit omniDerogHit = new OmniDerogHit();
                            omniDerogHit.setDerogId(idStr);
                            omniDerogHit.setScore(cat5Prob);
                            omniDerogHitList.add(omniDerogHit);
                        }
                        break;
                    default:
                        if (cat6Prob > matchingThreshold) {
                            OmniDerogHit omniDerogHit = new OmniDerogHit();
                            omniDerogHit.setDerogId(idStr);
                            omniDerogHit.setScore(cat6Prob);
                            omniDerogHitList.add(omniDerogHit);
                        }
                        break;
                }
            });

            omniTravelerResponse.setDerogIds(omniDerogHitList);
        } catch (Exception ex) {
            logger.error("convertOmniModelPredictionsToOmniTravelerResponse() - Got an exception: ", ex);
        }

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

        Set<Long> externalHitItemIds = new HashSet<>();

        for (OmniTravelerResponse travelerResponse: travelerResponses) {
            for (OmniDerogHit derogHit: travelerResponse.getDerogIds()) {
                long paxId;
                long externalHitItemId;
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

                externalHitItemId = Long.parseLong(derogIdStr);

                passengerIds.add(paxId);
                externalHitItemIds.add(externalHitItemId);
                derogHits.add(new AbstractMap.SimpleEntry<>(paxId, derogHit));
            }
        }
        
        if (derogHits.isEmpty()) {
            logger.info("createPendingHits() - Got an empty derogHits list. So, nothing to do...");
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
            long externalHitItemId = Long.parseLong(derogHit.getDerogId());

            PendingHitDetails pendingHit = new PendingHitDetails(); 

            pendingHit.setTitle(derogHitTitle);
            pendingHit.setDescription(derogHitDescription);

            pendingHit.setHitEnum(HitTypeEnum.EXTERNAL_HIT);
            pendingHit.setHitType(pendingHit.getHitEnum().toString());

            pendingHit.setHitMakerId(externalHitItemId);
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

            int i = 0;
            for (OmniModelPredictions omniModelPredictions : omniModelPredictionsList) {
                displayOmniModelPredictionsBucket.add(omniModelPredictions);
                i++;
                if (i >= maxElementShown) {
                    break;
                }
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

    private void debugPrintOmniTravelerResponsePayload(List<OmniTravelerResponse> omniTravelerResponseList) {
        try {

            int maxElementShown = MAX_DEBUG_PRINT_ROWS;
            List<OmniTravelerResponse> displayOmniTravelerResponseBucket = new ArrayList<>();
            int totalSize = omniTravelerResponseList.size();
            if (maxElementShown > totalSize) {
                maxElementShown = totalSize;
            }
            int i = 0;
            for (OmniTravelerResponse omniTravelerResponse: omniTravelerResponseList) {
                displayOmniTravelerResponseBucket.add(omniTravelerResponse);
                i++;
                if (i >= maxElementShown) {
                    break;
                }
            }

            String displayOmniTravelerResponseTitle = "========= Showing the first " + maxElementShown + " OmniTravelerResponse Received from Kaizen =======";

            if (maxElementShown == 1) {
                displayOmniTravelerResponseTitle = "========= Showing the only OmniTravelerResponse Received from Kaizen =======";
            }

            String jsonPayload = objectMapper.writer().writeValueAsString(displayOmniTravelerResponseBucket);

            logger.info(displayOmniTravelerResponseTitle);
            logger.info(jsonPayload);

        } catch (Exception ex) {
            logger.error("debugPrintOmniTravelerResponsePayload() - Got an exception: ", ex);
        }
    }
}
