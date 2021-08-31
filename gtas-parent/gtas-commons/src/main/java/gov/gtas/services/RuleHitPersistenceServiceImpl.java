/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.enumtype.HitViewStatusEnum;
import gov.gtas.enumtype.LookoutStatusEnum;
import gov.gtas.model.*;
import gov.gtas.repository.*;
import gov.gtas.services.jms.OmniLocalGtasSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class RuleHitPersistenceServiceImpl implements RuleHitPersistenceService {

	private static final String OMNI_LOCAL_HIT_DETAILS_AVAILABLE_NOTIFICATION = "LOCAL_NOTIFICATION_HIT_DETAILS_AVAILABLE";

	private static ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger logger = LoggerFactory.getLogger(RuleHitPersistenceServiceImpl.class);

	private final PassengerService passengerService;

	private final HitDetailRepository hitDetailRepository;

	private final HitsSummaryRepository hitsSummaryRepository;

	private final FlightHitsWatchlistRepository flightHitsWatchlistRepository;

	private final FlightHitsRuleRepository flightHitsRuleRepository;

	private final FlightFuzzyHitsRepository flightFuzzyHitsRepository;

	private final FlightGraphHitsRepository flightGraphHitsRepository;

	private final FlightHitsExternalRepository flightHitsExternalRepository;

	private final FlightHitsManualRepository flightHitsManualRepository;

	private final FlightPriorityCountRepository flightPriorityCountRepository;

	private final HitMakerRepository hitMakerRepository;
	
	private final MessageStatusRepository messageStatusRepository;

	@Value("${omni.enabled}")
	private Boolean omniEnabled;

	@Autowired(required=false)
	private OmniLocalGtasSender omniLocalGtasSender;

	public RuleHitPersistenceServiceImpl(PassengerService passengerService, HitDetailRepository hitDetailRepository,
										 HitsSummaryRepository hitsSummaryRepository, FlightHitsWatchlistRepository flightHitsWatchlistRepository,
										 FlightHitsRuleRepository flightHitsRuleRepository, FlightFuzzyHitsRepository flightFuzzyHitsRepository,
										 FlightGraphHitsRepository flightGraphHitsRepository, HitMakerRepository hitMakerRepository, FlightHitsExternalRepository flightHitsExternalRepository, FlightHitsManualRepository flightHitsManualRepository, 
										 FlightPriorityCountRepository flightPriorityCountRepository, MessageStatusRepository messageStatusRepository) {
		this.passengerService = passengerService;
		this.hitDetailRepository = hitDetailRepository;
		this.hitsSummaryRepository = hitsSummaryRepository;
		this.flightHitsWatchlistRepository = flightHitsWatchlistRepository;
		this.flightHitsRuleRepository = flightHitsRuleRepository;
		this.flightFuzzyHitsRepository = flightFuzzyHitsRepository;
		this.flightGraphHitsRepository = flightGraphHitsRepository;
		this.hitMakerRepository = hitMakerRepository;
		this.flightHitsExternalRepository = flightHitsExternalRepository;
		this.flightHitsManualRepository = flightHitsManualRepository;
		this.flightPriorityCountRepository = flightPriorityCountRepository;
		this.messageStatusRepository = messageStatusRepository;
	}

	@Transactional
	public Iterable<HitDetail> persistToDatabase(@NonNull Set<HitDetail> hitDetailSet) {
		Iterable<HitDetail> hitDetailIterable = null;
		try {
			Objects.requireNonNull(hitDetailSet);
			if (!hitDetailSet.isEmpty()) {
				Map<Long, Set<HitDetail>> hitDetailMappedToPassengerId = new HashMap<>();
				for (HitDetail hitDetail : hitDetailSet) {
					Long passengerId = hitDetail.getPassengerId();
					if (hitDetailMappedToPassengerId.containsKey(passengerId)) {
						hitDetailMappedToPassengerId.get(passengerId).add(hitDetail);
					} else {
						Set<HitDetail> hdMapSet = new HashSet<>();
						hdMapSet.add(hitDetail);
						hitDetailMappedToPassengerId.put(passengerId, hdMapSet);
					}
				}
				Set<Long> passengerIds = hitDetailSet.stream().filter(hd -> hd.getPassengerId() != null)
						.map(HitDetail::getPassengerId).collect(Collectors.toSet());
				Set<Passenger> passengersWithHitDetails = passengerService.getPassengersWithHitDetails(passengerIds);
				//hitMaker severity calculation work
				Map<Long, HitMaker> hitMakerMappedToHitMakerId = new HashMap<>();
				Set<Long> hitMakerIdsForCounting = new HashSet<>();
				for (HitDetail hd : hitDetailSet){
					hitMakerIdsForCounting.add(hd.getHitMakerId());
				}
				Iterable<HitMaker> relevantHitMakers = hitMakerRepository.findAllById(hitMakerIdsForCounting);
				for(HitMaker hm : relevantHitMakers){
					hitMakerMappedToHitMakerId.put(hm.getId(), hm);
				}
				//hitMaker severity calculation work
				int newDetails = 0;
				int existingDetails = 0;
				Set<HitDetail> hitDetailsToPersist = new HashSet<>();
				Set<HitMaker> hitMakersSet = new HashSet<>();
				Set<Long> flightIds = passengersWithHitDetails.stream().map(Passenger::getFlight).map(Flight::getId)
						.collect(Collectors.toSet());
				Set<HitsSummary> updatedHitsSummaries = new HashSet<>();
				int ruleHits = 0;
				int wlHits = 0;
				int graphHits = 0;
				int pwlHits = 0;
				int manualHits = 0;
				int externalHits = 0;
				for (Passenger passenger : passengersWithHitDetails) {
					Set<HitDetail> passengerHitDetails = passenger.getHitDetails();
					Set<HitDetail> ruleEngineHitDetails = hitDetailMappedToPassengerId.get(passenger.getId());
					for (HitDetail ruleEngineDetail : ruleEngineHitDetails) {
						if (passengerHitDetails.contains(ruleEngineDetail)) {
							existingDetails++;
						} else {
							// The flight can always be found to the flight passenger (flight passenger is
							// always resolved to a single flight
							// In order to make a join easy for the web application we are
							// Adding a relationship to the flight directly from the hit detail.
							ruleEngineDetail.setFlightId(passenger.getFlight().getId());
							HitsSummary hitsSummary = passenger.getHits();
							if (hitsSummary == null) {
								hitsSummary = new HitsSummary();
								hitsSummary.setPassenger(passenger);
								hitsSummary.setPaxId(passenger.getId());
								passenger.setHits(hitsSummary);
							}
							hitsSummary.setUpdated(true);
							hitsSummary.setFlightId(passenger.getFlight().getId());
							hitsSummary.setFlight(passenger.getFlight());
							switch (ruleEngineDetail.getHitEnum()) {
							case WATCHLIST:
							case WATCHLIST_DOCUMENT:
							case WATCHLIST_PASSENGER:
								hitsSummary.setWatchListHitCount(hitsSummary.getWatchListHitCount() + 1);
								wlHits++;
								break;
							case USER_DEFINED_RULE:
								hitsSummary.setRuleHitCount(hitsSummary.getRuleHitCount() + 1);
								ruleHits++;
								break;
							case GRAPH_HIT:
								hitsSummary.setGraphHitCount(hitsSummary.getGraphHitCount() + 1);
								graphHits++;
								break;
							case PARTIAL_WATCHLIST:
								hitsSummary.setPartialHitCount(hitsSummary.getPartialHitCount() + 1);
								pwlHits++;
								break;
							case MANUAL_HIT:
								hitsSummary.setManualHitCount(hitsSummary.getManualHitCount() + 1);
								manualHits++;
								break;
							case EXTERNAL_HIT:
								hitsSummary.setExternalHitCount(hitsSummary.getExternalHitCount() + 1);
								externalHits++;
								break;
							default:
								logger.warn("UNIMPLEMENTED FIELD - COUNT NOT SAVED - " + ruleEngineDetail.getHitEnum());
							}
							HitMaker hm = hitMakerMappedToHitMakerId.get(ruleEngineDetail.getHitMakerId());
							switch (hm.getHitCategory().getSeverity()) {
								case NORMAL:
									hitsSummary.setLowPriorityCount(hitsSummary.getLowPriorityCount() + 1);
									break;
								case HIGH:
									hitsSummary.setMedPriorityCount(hitsSummary.getMedPriorityCount() + 1);
									break;
								case TOP:
									hitsSummary.setHighPriorityCount(hitsSummary.getHighPriorityCount() + 1);
									break;
								default:
									logger.warn("UNIMPLEMENTED PRIORITY - COUNT NOT SAVED - " + ruleEngineDetail.getHitMaker().getHitCategory().getSeverity());
							}

							newDetails++;
							hitMakersSet.add(hm); //consolidated hit maker's set, above is a super set technically.
							ruleEngineDetail.setPassenger(passenger);
							ruleEngineDetail.setPassengerId(passenger.getId());
							hitDetailsToPersist.add(ruleEngineDetail);
							updatedHitsSummaries.add(hitsSummary);
						}
					}
				}
				if (!hitDetailsToPersist.isEmpty()) {
					Map<Long, Set<UserGroup>> hitMakerMappedByPrimaryKey = new HashMap<>();
					Map<Long, Boolean> hitMakerIdMappedToLookoutStatus = new HashMap<>();
					for (HitMaker hitMaker : hitMakersSet) {
						hitMakerMappedByPrimaryKey.put(hitMaker.getId(), hitMaker.getHitCategory().getUserGroups());
						hitMakerIdMappedToLookoutStatus.put(hitMaker.getId(), hitMaker.getHitCategory().isPromoteToLookout());
					}

					for (HitDetail hd : hitDetailsToPersist) {
						LookoutStatusEnum poeStatus = LookoutStatusEnum.NOTPROMOTED;
						if(hitMakerIdMappedToLookoutStatus.get(hd.getHitMakerId())) { //If ANY category is worthy of promoting, ALL hit view statuses are set active
							poeStatus = LookoutStatusEnum.ACTIVE;
						}
						for (UserGroup ug : hitMakerMappedByPrimaryKey.get(hd.getHitMakerId())) {
							MutableFlightDetails mfd = hd.getPassenger().getFlight().getMutableFlightDetails();
							Flight f = hd.getPassenger().getFlight();
							HitViewStatus hitViewStatus = new HitViewStatus(hd, ug, HitViewStatusEnum.NEW,
									hd.getPassenger(), poeStatus, mfd.getEta(), mfd.getEtd(), f.getDirection());
							hd.getHitViewStatus().add(hitViewStatus);
						}
					}
					hitDetailIterable = hitDetailRepository.saveAll(hitDetailsToPersist);

					if (omniEnabled) {
						sendHitDetailsToOmniHandler(hitDetailsToPersist);
					}
				}
				if (!updatedHitsSummaries.isEmpty()) {
					hitsSummaryRepository.saveAll(updatedHitsSummaries);
					updateFlightHitCounts(flightIds);
				}
				logger.debug("Processed... rule hits: " + ruleHits + " wlHits: " + wlHits + " graphHits" + graphHits
						+ " partial hits: " + pwlHits + " manual hits: " + manualHits + " external hits: " + externalHits);
				logger.info("Persisted " + newDetails + " new passenger hit details, ignored " + existingDetails
						+ " existing passenger hit details.");
			}
		} catch (Exception e) {
			logger.warn("UNABLE TO CREATE NEW HIT DETAILS. FAILURE! ", e);
		}

		return hitDetailIterable;
	}

	
	@Override	
	public List<MessageStatus> getRelevantMessages(Set<Long> messageIds) {
		if (messageIds == null || messageIds.isEmpty()) {
			return new ArrayList<>();
		} else {
			return messageStatusRepository.getMessageFromIds(new ArrayList<>(messageIds));
		}	
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.gtas.svc.TargetingService#updateFlightHitCounts(java.util.Set)
	 */
	@Override
	public void updateFlightHitCounts(Set<Long> flights) {
		logger.debug("Entering updateFlightHitCounts().");
		if (CollectionUtils.isEmpty(flights)) {
			logger.debug("no flight");
			return;
		}
		logger.debug("update rule hit count on flights.");
		Set<FlightHitsRule> flightHitsRules = new HashSet<>();
		Set<FlightHitsWatchlist> flightHitsWatchlists = new HashSet<>();
		Set<FlightHitsGraph> flightHitsGraphs = new HashSet<>();
		Set<FlightHitsFuzzy> flightHitsFuzzies = new HashSet<>();
		Set<FlightHitsExternal> flightHitsExternals = new HashSet<>();
		Set<FlightHitsManual> flightHitsManuals = new HashSet<>();
		Set<FlightPriorityCount> flightPriorityCounts = new HashSet<>();
		for (Long flightId : flights) {
			Integer ruleHits = Optional.ofNullable(hitsSummaryRepository.totalRuleHitCount(flightId)).orElse(0);
			FlightHitsRule ruleFlightHits = new FlightHitsRule(flightId, ruleHits);
			flightHitsRules.add(ruleFlightHits);

			Integer watchlistHit = Optional.ofNullable(hitsSummaryRepository.totalWatchlistHitCount(flightId)).orElse(0);
			FlightHitsWatchlist watchlistHitCount = new FlightHitsWatchlist(flightId, watchlistHit);
			flightHitsWatchlists.add(watchlistHitCount);

			Integer graphWatchlistHit = Optional.ofNullable(hitsSummaryRepository.totalGraphHitCount(flightId)).orElse(0);
			FlightHitsGraph flightHitsGraph = new FlightHitsGraph(flightId, graphWatchlistHit);
			flightHitsGraphs.add(flightHitsGraph);

			Integer partialHitCount = Optional.ofNullable(hitsSummaryRepository.totalPartialHitCount(flightId)).orElse(0);
			FlightHitsFuzzy flightHitsFuzzy = new FlightHitsFuzzy(flightId, partialHitCount);
			flightHitsFuzzies.add(flightHitsFuzzy);

			Integer externalHitCount = Optional.ofNullable(hitsSummaryRepository.totalExternalHitCount(flightId)).orElse(0);
			FlightHitsExternal flightHitsExternal = new FlightHitsExternal(flightId, externalHitCount);
			flightHitsExternals.add(flightHitsExternal);

			Integer manualHitCount = Optional.ofNullable(hitsSummaryRepository.totalManualHitCount(flightId)).orElse(0);
			FlightHitsManual flightHitsManual = new FlightHitsManual(flightId, manualHitCount);
			flightHitsManuals.add(flightHitsManual);

			Set<HitsSummary> hss = hitsSummaryRepository.findByFlightId(flightId);
			int highPrioCount = 0;
			int medPrioCount = 0;
			int lowPrioCount = 0;

			for(HitsSummary hs : hss){
				highPrioCount += hs.getHighPriorityCount();
				medPrioCount += hs.getMedPriorityCount();
				lowPrioCount += hs.getLowPriorityCount();
			}
			FlightPriorityCount flightPriorityCount = new FlightPriorityCount(flightId, highPrioCount, medPrioCount, lowPrioCount);
			flightPriorityCounts.add(flightPriorityCount);

		}
		flightHitsRuleRepository.saveAll(flightHitsRules);
		flightHitsWatchlistRepository.saveAll(flightHitsWatchlists);
		flightGraphHitsRepository.saveAll(flightHitsGraphs);
		flightFuzzyHitsRepository.saveAll(flightHitsFuzzies);
		flightHitsExternalRepository.saveAll(flightHitsExternals);
		flightHitsManualRepository.saveAll(flightHitsManuals);
		flightPriorityCountRepository.saveAll(flightPriorityCounts);
	}

	private void sendHitDetailsToOmniHandler(Set<HitDetail> hitDetailsToPersist) {
		try {
			String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(hitDetailsToPersist);
			omniLocalGtasSender.send(OMNI_LOCAL_HIT_DETAILS_AVAILABLE_NOTIFICATION, jsonResponse);
		} catch(Exception ex) {
			logger.error("sendHitDetailsToOmniHandler() - Got an execption", ex);
		}
	}
}
