package gov.gtas.services.matcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import gov.gtas.model.*;
import gov.gtas.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.matcher.quickmatch.DerogHit;
import gov.gtas.services.matcher.quickmatch.DerogResponse;
import gov.gtas.services.matcher.quickmatch.MatchingResult;
import gov.gtas.services.matcher.quickmatch.QuickMatcher;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

@Service
public class MatchingServiceImpl implements MatchingService {
	@Autowired
	private PaxWatchlistLinkRepository paxWatchlistLinkRepository;
	@Autowired
	private WatchlistItemRepository watchlistItemRepository;
	@Autowired
	private PassengerRepository passengerRepository;
	@Autowired
	private WatchlistRepository watchlistRepository;
	@Autowired
	private FlightRepository flightRepository;
	@Autowired
	private CaseDispositionService caseDispositionService;

	@Autowired
	private PassengerWatchlistRepository passengerWatchlistRepository;

	@Autowired
	private AppConfigurationRepository appConfigRepository;

	@Autowired
	NameMatchCaseMgmtUtils nameMatchCaseMgmtUtils;

	@Autowired
	private QuickMatcher qm;
	
	private ObjectMapper mapper = new ObjectMapper();
	private static Logger logger = LoggerFactory.getLogger(MatchingService.class);

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
			for(int i=0; i< items.length; i++) {
				if(items[i].getField().equals("firstName")) {
					firstName=items[i].getValue();
				}
				
				if(items[i].getField().equals("lastName")) {
					lastName=items[i].getValue();
				}

				if(items[i].getField().equals("dob")) {
					dob = items[i].getValue();
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		Passenger passenger = passengerRepository.getFullPassengerById(id);
		List<Long> passengers = Collections.singletonList(passenger.getId());
		Flight flight = passenger.getFlight();
		MatcherParameters matcherParameters = getMatcherParameters(passengers);
		performFuzzyMatching(flight, passenger, matcherParameters);
	}

	private MatcherParameters getMatcherParameters(List<Long> passengers) {
		MatcherParameters matcherParameters = new MatcherParameters();
		matcherParameters.setCaseMap(createCaseMap(passengers, this.caseDispositionService));
		matcherParameters.setRuleCatMap(createRuleCatMap(caseDispositionService));
		matcherParameters.set_watchlists(watchlistRepository.getWatchlistByNames(Arrays.asList("Passenger", "Document")));
		Map<Long, List<WatchlistItem>> watchlistListMap = new HashMap<>();
		for (Watchlist watchlist : matcherParameters.get_watchlists()) {
			List<WatchlistItem> watchlistsItemList = watchlistItemRepository
					.getItemsByWatchlistName(watchlist.getWatchlistName());
			watchlistListMap.put(watchlist.getId(), watchlistsItemList);
		}
		matcherParameters.setWatchlistListMap(watchlistListMap);
		matcherParameters.setThreshold(Float
				.parseFloat((appConfigRepository.findByOption(appConfigRepository.MATCHING_THRESHOLD).getValue())));
		return matcherParameters;
	}

	public void performFuzzyMatching(Flight flight, Passenger passenger, MatcherParameters matcherParameters) {
		List<Watchlist> watchlistsList = matcherParameters.get_watchlists();
		for (Watchlist watchlist : watchlistsList) {
			if (passengerNeedsWatchlistCheck(passenger, watchlist)) {
				Set<Long> wlItemIds = new HashSet<>();
				for (PaxWatchlistLink paxWatchlistLink : passenger.getPaxWatchlistLinks()) {
					wlItemIds.add(paxWatchlistLink.getWatchlistItem().getId());
				}
				List<HashMap<String, String>> derogList = new ArrayList<>();
				Map<Long, List<WatchlistItem>> watchlistMap = matcherParameters.getWatchlistListMap();
				for (WatchlistItem item : watchlistMap.get(watchlist.getId())) {
					// If watchlist-pax connection doesn't exist (Prevents Duplicate Inserts)
					if (!wlItemIds.contains(item.getId())) {
						try {
							WatchlistItemSpec itemSpec = mapper.readValue(item.getItemData(), WatchlistItemSpec.class);
							HashMap<String, String> derogItem = new HashMap<>();
							derogItem.put("gtasId", passenger.getId().toString());
							derogItem.put("derogId", item.getId().toString());
							derogItem.put(DerogHit.WATCH_LIST_NAME, item.getWatchlist().getWatchlistName());
							if (itemSpec != null && itemSpec.getTerms() != null) {
								for (int i = 0; i < itemSpec.getTerms().length; i++) {
									derogItem.put(itemSpec.getTerms()[i].getField(), itemSpec.getTerms()[i].getValue());
								}
							}
							derogList.add(derogItem);
						} catch (IOException ioe) {
							logger.error("Matching Service" + ioe.getMessage());
							throw ErrorHandlerFactory.getErrorHandler().createException(
									CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE, item.getId(),
									"getWatchListMatchByPaxId");
						}
					}
				}
				MatchingResult result = qm.match(passenger, derogList, matcherParameters.getThreshold());
				// These will make calls to the database to save a watchlist link and make a case.
				// This will cause performance issues IF every passenger hits on a watchlist.
				if (result.getTotalHits() >= 0) {
					Map<String, DerogResponse> _responses = result.getResponses();
					for (String key : _responses.keySet()) {
						List<DerogHit> derogs = _responses.get(key).getDerogIds();
						for (DerogHit hit : derogs) {
							paxWatchlistLinkRepository.savePaxWatchlistLink(new Date(), hit.getPercent(), 0,
									passenger.getId(), Long.parseLong(hit.getDerogId()));
						    Case existingCase = matcherParameters.getCaseMap().get(passenger.getId());
							// make a call to open case here
							nameMatchCaseMgmtUtils
									.processPassengerFlight(
									hit.getWatchlistName(),
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
	}

	private boolean passengerNeedsWatchlistCheck(Passenger passenger, Watchlist watchlist) {
		return  passenger.getPassengerWLTimestamp() == null ||
				passenger.getPassengerWLTimestamp()
						 .getWatchlistCheckTimestamp() == null ||
				passenger.getPassengerWLTimestamp()
				.getWatchlistCheckTimestamp()
				.before(watchlist.getEditTimestamp());
	}

	private static Map<Long, RuleCat> createRuleCatMap(CaseDispositionService dispositionService)
	    {
	       Map<Long, RuleCat> ruleCatMap = new HashMap<>();
	       Iterable<RuleCat> ruleCatList = dispositionService.findAllRuleCat();
	       for (RuleCat ruleCat : ruleCatList)
	       {
	         ruleCatMap.put(ruleCat.getId(), ruleCat);
	       }
	        
	       return ruleCatMap;
	    }
	 
	 private static Map<Long, Case> createCaseMap(List<Long> passengerIdList, CaseDispositionService dispositionService)
	    {
	        List<Case> caseResultList = null;
	        Map<Long, Case> caseMap = new HashMap<>();

	        if (!passengerIdList.isEmpty())
	        {
	            caseResultList = dispositionService.getCaseByPaxId(passengerIdList);

	            for (Case caze :  caseResultList)
	            {
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
	public int findMatchesBasedOnTimeThreshold() {
		logger.debug("entering findMatchesBasedOnTimeThreshold()");
		int totalMatchCount = 0;
		long startTime = System.nanoTime();
		// get flights that are arriving between timeOffset and "now".
		Map<Flight,Set<Passenger>> passengers = getPassengersOnFlightsWithinTimeRange();
		long endTime = System.nanoTime();
		logger.debug("Execution time for getFlightsWithinTimeRange() = " + (endTime - startTime) / 1000000 + "ms");
		// Begin matching for all passengers on all flights retrieved within time frame.
		if (passengers != null && passengers.size() > 0) { // Don't try and match if no flights
			startTime = System.nanoTime();
			List<Long> passengerIds = new ArrayList<>();

			for (Set<Passenger> passList : passengers.values()) {
				for (Passenger p : passList) {
					passengerIds.add(p.getId());
				}
			}
			Set<PassengerWLTimestamp> savingPassengerSet = new HashSet<>();
			MatcherParameters matcherParameters = getMatcherParameters(passengerIds);
			for (Flight f : passengers.keySet()) {
				for (Passenger passenger : passengers.get(f)) {
					try {
						performFuzzyMatching(f, passenger, matcherParameters);
						PassengerWLTimestamp passengerWLTimestamp = new PassengerWLTimestamp(passenger.getId(), new Date());
						savingPassengerSet.add(passengerWLTimestamp);
					} catch (Exception e) {
						logger.error("failed to run watchlist check on passenger. " +
								"Will attempt a run on next pass.",  e);
					}
				}
			}
			passengerWatchlistRepository.save(savingPassengerSet);
			endTime = System.nanoTime();
			logger.debug("Passenger count for matching service: " + passengerIds.size());
			logger.debug("Execution time for performFuzzyMatching() for loop = " + (endTime - startTime) / 1000000
					+ "ms");
		}
		return totalMatchCount;
	}

	private Map<Flight, Set<Passenger>> getPassengersOnFlightsWithinTimeRange() {
		logger.debug("entering getFlightsWithinTimeRange()");
		long startTime, endTime;
		double timeOffset = Double
				.parseDouble(appConfigRepository.findByOption(appConfigRepository.FLIGHT_RANGE).getValue());
		String[] arr = String.valueOf(timeOffset).split("\\.");
		int timeOffsetHours = Integer.parseInt(arr[0]);
		int timeOffsetMinutes = Integer
				.parseInt(String.valueOf((60 * Double.parseDouble(arr[1]) / 10)).split("\\.")[0]); // retrieves the
																									// percentage of the
																									// minutes solution
		Date startDate = new Date();
		Date endDate = new Date();
		// Set time +hours and +minutes out from current time in order to grab upcoming
		// flights arriving or departing within the time frame.
		endDate.setHours(startDate.getHours() + timeOffsetHours);
		endDate.setMinutes(startDate.getMinutes() + timeOffsetMinutes);
		// Calls native query that uses a between to get all flights with flight.eta
		// between startDate and endDate
		ArrayList<Flight> flights = (ArrayList<Flight>) flightRepository
				.getInboundAndOutboundFlightsWithinTimeFrame(startDate, endDate);
		Map<Flight, Set<Passenger>> passengers = new HashMap<>();
		if (flights != null && flights.size() > 0) {
			startTime = System.nanoTime();
			for (Flight f : flights) {
				passengers.put(f, f.getPassengers());
			}
			endTime = System.nanoTime();
			logger.debug(
					"Execution time for getPassengersOnFlightsWithingTimeRange() get passenger by flight ID for loop = "
							+ (endTime - startTime) / 1000000 + "ms");
		}
		logger.debug("Number of flights found within " + timeOffsetHours + " hours and " + timeOffsetMinutes
				+ " minutes of arrival or departure. Flight Count: " + flights.size());
		return passengers;
	}
}
