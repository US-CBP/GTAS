/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services.matcher;

import static gov.gtas.repository.AppConfigurationRepository.QUICKMATCH_DOB_YEAR_OFFSET;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.Case;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightHitsFuzzy;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerWLTimestamp;
import gov.gtas.model.PaxWatchlistLink;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.FlightFuzzyHitsRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PassengerWatchlistRepository;
import gov.gtas.repository.PaxWatchlistLinkRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.matcher.quickmatch.DerogHit;
import gov.gtas.services.matcher.quickmatch.DerogResponse;
import gov.gtas.services.matcher.quickmatch.MatchingContext;
import gov.gtas.services.matcher.quickmatch.MatchingResult;
import gov.gtas.services.matcher.quickmatch.QuickMatcherImpl;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

@Component
@Scope("prototype")
public class MatchingServiceImpl implements MatchingService {
    private final PaxWatchlistLinkRepository paxWatchlistLinkRepository;
    private final WatchlistItemRepository watchlistItemRepository;
    private final PassengerRepository passengerRepository;
    private final WatchlistRepository watchlistRepository;
    private final CaseDispositionService caseDispositionService;
    private final FlightFuzzyHitsRepository flightFuzzyHitsRepository;

    private final PassengerWatchlistRepository passengerWatchlistRepository;

    private final AppConfigurationRepository appConfigRepository;
    private final ApplicationContext ctx;

    private final
    NameMatchCaseMgmtUtils nameMatchCaseMgmtUtils;

    private ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(MatchingService.class);

    public MatchingServiceImpl(
            PaxWatchlistLinkRepository paxWatchlistLinkRepository,
            WatchlistItemRepository watchlistItemRepository,
            PassengerRepository passengerRepository,
            WatchlistRepository watchlistRepository,
            CaseDispositionService caseDispositionService,
            FlightFuzzyHitsRepository flightFuzzyHitsRepository, PassengerWatchlistRepository passengerWatchlistRepository,
            AppConfigurationRepository appConfigRepository,
            ApplicationContext ctx,
            NameMatchCaseMgmtUtils nameMatchCaseMgmtUtils) {
        this.paxWatchlistLinkRepository = paxWatchlistLinkRepository;
        this.watchlistItemRepository = watchlistItemRepository;
        this.passengerRepository = passengerRepository;
        this.watchlistRepository = watchlistRepository;
        this.caseDispositionService = caseDispositionService;
        this.flightFuzzyHitsRepository = flightFuzzyHitsRepository;
        this.passengerWatchlistRepository = passengerWatchlistRepository;
        this.appConfigRepository = appConfigRepository;
        this.ctx = ctx;
        this.nameMatchCaseMgmtUtils = nameMatchCaseMgmtUtils;
    }

    private PaxWatchlistLinkVo convertToVo(PaxWatchlistLink pwLink) {
        return convertToVo(pwLink.getPercentMatch(), pwLink.getLastRunTimestamp(), pwLink.getVerifiedStatus(),
                pwLink.getPassenger(), pwLink.getWatchlistItem());
    }

    private PaxWatchlistLinkVo convertToVo(float percentMatch, Date lastRunTimestamp, int verifiedStatus,
                                           Passenger passenger, WatchlistItem item) {
        String firstName = "";
        String lastName = "";
        String dob = "";
        try {
            WatchlistItemSpec itemSpec = mapper.readValue(item.getItemData(), WatchlistItemSpec.class);

            WatchlistTerm[] items = itemSpec.getTerms();
            for (int i = 0; i < items.length; i++) {
                if (items[i].getField().equals("firstName")) {
                    firstName = items[i].getValue();
                }

                if (items[i].getField().equals("lastName")) {
                    lastName = items[i].getValue();
                }

                if (items[i].getField().equals("dob")) {
                    dob = items[i].getValue();
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }


        return new PaxWatchlistLinkVo(percentMatch, lastRunTimestamp, verifiedStatus, passenger.getId(), item.getId(),
                firstName, lastName, dob);
    }

    public List<PaxWatchlistLinkVo> findByPassengerId(Long id) {
        List<PaxWatchlistLinkVo> pwlList = new ArrayList<PaxWatchlistLinkVo>();
        for (PaxWatchlistLink pwLink : paxWatchlistLinkRepository.findByPassengerId(id)) {
            pwlList.add(convertToVo(pwLink));
        }
        return pwlList;
    }

    // Overloaded method that will save on erroneous passenger calls during
    // automated run. Automated run already contains passenger objects.
    public void performFuzzyMatching(Long id) {
        logger.debug("Starting fuzzy matching");
        Passenger passenger = passengerRepository.getFullPassengerById(id);
        Set<Passenger> passengers = Collections.singleton(passenger);
        Flight flight = passenger.getFlight();
        MatcherParameters matcherParameters = getMatcherParameters(passengers);
        
        performFuzzyMatching(flight, passenger, matcherParameters);
        logger.debug("ending fuzzy matching");


    }

    private MatcherParameters getMatcherParameters(Set<Passenger> passengers) {
        logger.debug("getting matcher parameters");
        MatcherParameters matcherParameters = new MatcherParameters();
        matcherParameters.setCaseMap(createCaseMap(passengers.stream().map(Passenger::getId).collect(Collectors.toList()), this.caseDispositionService));
        matcherParameters.setRuleCatMap(createRuleCatMap(caseDispositionService));
        matcherParameters.set_watchlists(watchlistRepository.getWatchlistByNames(Collections.singletonList("Passenger")));
        Map<Long, List<WatchlistItem>> watchlistListMap = new HashMap<>();
        logger.debug("getting items");

        for (Watchlist watchlist : matcherParameters.get_watchlists()) {
            List<WatchlistItem> watchlistsItemList = watchlistItemRepository
                    .getItemsByWatchlistName(watchlist.getWatchlistName());
            watchlistListMap.put(watchlist.getId(), watchlistsItemList);
        }
        matcherParameters.setWatchlistListMap(watchlistListMap);
        matcherParameters.setThreshold(Float
                .parseFloat((appConfigRepository.findByOption(AppConfigurationRepository.MATCHING_THRESHOLD).getValue())));
        matcherParameters.setDobYearOffset(getDobYearOffset(MatchingContext.DOB_YEAR_OFFSET));  
		matcherParameters.setPaxWatchlistLinks(this.getPaxWatchlistLinks(passengers)); 
        		
		matcherParameters.get_watchlists().forEach(w -> {
			List<HashMap<String, String>> derogLists  = createWatchlistItems(matcherParameters.getWatchlistListMap().get(w.getId()));
			matcherParameters.addDerogList(derogLists);
		});

		//
		matcherParameters.setQm(new QuickMatcherImpl(matcherParameters.getDerogList())); 
		 
        logger.debug("got matcher parameters");
        return matcherParameters;
    }

	/**
	 * 
	 * @return
	 */
	public Map<Long, Set<Long>> getPaxWatchlistLinks(Set<Passenger> passengers) {
		
		Map<Long, Set<Long>> watchlistLinks =  new HashMap<>();
		for (Passenger passenger : passengers) {
			
			Set<Long> links = new HashSet<>();	
			for (PaxWatchlistLink item : passenger.getPaxWatchlistLinks()) {
				links.add(item.getId());
			}
			watchlistLinks.put(passenger.getId(), links);
		}
		return watchlistLinks;
	}
    
	private List<HashMap<String, String>> createWatchlistItems(List<WatchlistItem> watchlistItems) {

		List<HashMap<String, String>> derogList = new ArrayList<>();

		for (WatchlistItem item : watchlistItems) {
			try {
				WatchlistItemSpec itemSpec = mapper.readValue(item.getItemData(), WatchlistItemSpec.class);
				HashMap<String, String> derogItem = new HashMap<>();
				derogItem.put("derogId", item.getId().toString());
				derogItem.put(DerogHit.WATCH_LIST_NAME, item.getWatchlist().getWatchlistName());
				if (itemSpec != null && itemSpec.getTerms() != null) {
					for (int i = 0; i < itemSpec.getTerms().length; i++) {
						derogItem.put(itemSpec.getTerms()[i].getField(), itemSpec.getTerms()[i].getValue());
					}
				}
				derogList.add(derogItem);
			} catch (IOException ioe) {
				logger.error("Matching Service {}", ioe.getMessage());
				throw ErrorHandlerFactory.getErrorHandler().createException(
						CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE, item.getId(), "getWatchListMatchByPaxId");
			}
		}

		return derogList;
	}

    public int performFuzzyMatching(Flight flight, Passenger passenger, MatcherParameters matcherParameters) {
        
        logger.debug("Starting fuzzy matching");
       
        List<Watchlist> watchlistsList = matcherParameters.get_watchlists();
        int hitCounter = 0;
        for (Watchlist watchlist : watchlistsList) {
            if (passengerNeedsWatchlistCheck(passenger, watchlist)) {

				Set<Long> wlItemIds = matcherParameters.getPaxWatchlistLinks(passenger.getId());
				hitCounter += wlItemIds.size();
				//
				MatchingResult result = matcherParameters.getQm().match(passenger, matcherParameters.getThreshold(),
						matcherParameters.getDobYearOffset(), wlItemIds);

                // These will make calls to the database to save a watchlist link and make a case.
                // This will cause performance issues IF every passenger hits on a watchlist.
                if (result.getTotalHits() >= 0) {
                    Map<String, DerogResponse> _responses = result.getResponses();
                    for (String key : _responses.keySet()) {
                        List<DerogHit> derogs = _responses.get(key).getDerogIds();
                        for (DerogHit hit : derogs) {
                            hitCounter++;
                            paxWatchlistLinkRepository.savePaxWatchlistLink(new Date(), hit.getPercent(), 0,
                                    passenger.getId(), Long.parseLong(hit.getDerogId()));
                            Case existingCase = matcherParameters.getCaseMap().get(passenger.getId());
                            // make a call to open case here
                            nameMatchCaseMgmtUtils
                                    .processPassengerFlight(
                                            hit.getRuleDescription(),
                                            passenger,
                                            Long.parseLong(hit.getDerogId()),
                                            flight,
                                            existingCase,
                                            matcherParameters.getRuleCatMap());
                        }
                    }
                }
            }
        }
        logger.debug("ended fuzzy matching");

        return hitCounter;
    }

	private int getDobYearOffset(int dobYearOffset) {
		try {

			dobYearOffset = Integer.parseInt(appConfigRepository.findByOption(QUICKMATCH_DOB_YEAR_OFFSET).getValue());

		} catch (Exception e) {

			logger.warn(
					"QUICKMATCH_DOB_YEAR_OFFSET is not configured properly - DEFAULT offset value of {} will be used instead. "
							+ "Set QUICKMATCH_DOB_YEAR_OFFSET in application config",
					dobYearOffset);

		}
		return dobYearOffset;
	}

    private boolean passengerNeedsWatchlistCheck(Passenger passenger, Watchlist watchlist) {
        return passenger.getPassengerWLTimestamp() == null ||
                passenger.getPassengerWLTimestamp()
                        .getWatchlistCheckTimestamp() == null ||
                passenger.getPassengerWLTimestamp()
                        .getWatchlistCheckTimestamp()
                        .before(watchlist.getEditTimestamp());
    }

    private static Map<Long, RuleCat> createRuleCatMap(CaseDispositionService dispositionService) {
        Map<Long, RuleCat> ruleCatMap = new HashMap<>();
        Iterable<RuleCat> ruleCatList = dispositionService.findAllRuleCat();
        for (RuleCat ruleCat : ruleCatList) {
            ruleCatMap.put(ruleCat.getId(), ruleCat);
        }

        return ruleCatMap;
    }

    private static Map<Long, Case> createCaseMap(List<Long> passengerIdList, CaseDispositionService dispositionService) {
        List<Case> caseResultList = null;
        Map<Long, Case> caseMap = new HashMap<>();

        if (!passengerIdList.isEmpty()) {
            caseResultList = dispositionService.getCaseByPaxId(passengerIdList);

            for (Case caze : caseResultList) {
                caseMap.put(caze.getPaxId(), caze);

            }
        }

        return caseMap;
    }

    /**
     * receives a time threshold in hours to process potential matches for flights
     * within a given timeframe returns a count of all matches found during matching
     * process beyond the match threshold
     *
     * @return totalMatchCount
     */
    @Transactional
    public int findMatchesBasedOnTimeThreshold(List<MessageStatus> messageStatuses) {
        logger.debug("entering findMatchesBasedOnTimeThreshold()");

        if (messageStatuses.isEmpty()) {
            return 0;
        }
        Set<Long> messageIds = messageStatuses.stream().map(MessageStatus::getMessageId).collect(toSet());
        Set<Passenger> passengers = passengerRepository.getPassengerMatchingInformation(messageIds);

        int totalMatchCount = 0;
        long startTime = System.nanoTime();
        // get flights that are arriving between timeOffset and "now".
        long endTime = System.nanoTime();
        logger.debug("Execution to get initial matching data is = {}ms", (endTime - startTime) / 1000000);
        // Begin matching for all passengers on all flights retrieved within time frame.
        if (passengers != null && !passengers.isEmpty()) { // Don't try and match if no flights
            startTime = System.nanoTime();
            Set<Flight> flights = new HashSet<>();
            Set<PassengerWLTimestamp> savingPassengerSet = new HashSet<>();
            MatcherParameters matcherParameters = getMatcherParameters(passengers);
            
            for (Passenger passenger : passengers) {
                try {
                    flights.add(passenger.getFlight());
                    int fuzzyHitCounts = performFuzzyMatching(passenger.getFlight(), passenger, matcherParameters);
                    PassengerWLTimestamp passengerWLTimestamp;
                    if (passenger.getPassengerWLTimestamp() == null) {
                        passengerWLTimestamp = new PassengerWLTimestamp(passenger.getId(), new Date());
                        passengerWLTimestamp.setHitCount(fuzzyHitCounts);
                    } else {
                        passengerWLTimestamp = passenger.getPassengerWLTimestamp();
                        passengerWLTimestamp.setWatchlistCheckTimestamp(new Date());
                        if (passengerWLTimestamp.getHitCount() != null) {
                            passengerWLTimestamp.setHitCount(passengerWLTimestamp.getHitCount() + fuzzyHitCounts);
                        } else {
                            passengerWLTimestamp.setHitCount(fuzzyHitCounts);
                        }
                    }
                    if (fuzzyHitCounts > 0) {
                        totalMatchCount++;
                    }
                    savingPassengerSet.add(passengerWLTimestamp);
                } catch (Exception e) {
                    logger.error("failed to run watchlist check on passenger. " +
                            "Will attempt a run on next pass.", e);
                }
            }
            passengerWatchlistRepository.saveAll(savingPassengerSet);
            for (Flight flight : flights) {
                FlightHitsFuzzy flightHitsFuzzy = new FlightHitsFuzzy();
                Integer hits = flightFuzzyHitsRepository.fuzzyCount(flight.getId());
                flightHitsFuzzy.setHitCount(hits);
                flightHitsFuzzy.setFlightId(flight.getId());
                flightFuzzyHitsRepository.save(flightHitsFuzzy);
            }
        }
        endTime = System.nanoTime();
        int paxTotal = passengers == null ? 0 : passengers.size();
        logger.debug("Passenger hit count and total run: {} {}",totalMatchCount, paxTotal);
        logger.info("Execution time for performFuzzyMatching() for loop  = {} ms, {} passengers", (endTime - startTime) / 1000000, paxTotal);
        return totalMatchCount;
    }

}
