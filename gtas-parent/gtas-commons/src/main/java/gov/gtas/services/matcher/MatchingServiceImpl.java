package gov.gtas.services.matcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.lucene.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.Case;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.PaxWatchlistLink;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.CaseDispositionRepository;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PaxWatchlistLinkRepository;
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
	@Resource
	private CaseDispositionRepository caseDispositionRepository;
	@Autowired
	private AppConfigurationRepository appConfigRepository;

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
	public void saveWatchListMatchByPaxId(Long id) {
		Passenger passenger = passengerRepository.getPassengerById(id);
		List<Long> passengers = Lists.asList(passenger.getId(), new Long[0]);
		Flight flight = flightRepository.getFlightByPassengerId(passenger.getId()).stream().findFirst()
				.orElse(null);
		saveWatchListMatchByPaxId(createCaseMap(
				passengers,this.caseDispositionService), flight, passenger);
	}

	public void saveWatchListMatchByPaxId(Map<Long, Case> existingCases, Flight flight, Passenger passenger) {
		final float threshold = Float
				.parseFloat((appConfigRepository.findByOption(appConfigRepository.MATCHING_THRESHOLD).getValue()));
		List<Watchlist> _watchlists = watchlistRepository.getWatchlistByNames(Arrays.asList("Passenger", "Document"));
		for (Watchlist watchlist : _watchlists) {
			if (passenger.getWatchlistCheckTimestamp() == null
					|| passenger.getWatchlistCheckTimestamp().before(watchlist.getEditTimestamp())) {
				List<WatchlistItem> watchlists = watchlistItemRepository
						.getItemsByWatchlistName(watchlist.getWatchlistName());
				Set<Long> watchlistIds = new HashSet<Long>(
						paxWatchlistLinkRepository.findWatchlistItemByPassengerId(passenger.getId()));
				List<HashMap<String, String>> derogList = new ArrayList<HashMap<String, String>>();
				for (WatchlistItem item : watchlists) {

					// If watchlist-pax connection doesn't exist (Prevents Duplicate Inserts)
					if (!watchlistIds.contains(item.getId())) {
						try {
							WatchlistItemSpec itemSpec = mapper.readValue(item.getItemData(), WatchlistItemSpec.class);
							HashMap<String, String> derogItem = new HashMap<String, String>();

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
				
				long matchingStart = System.nanoTime();
				logger.info("fuzzy name matching started " + matchingStart);
				MatchingResult result = qm.match(passenger, derogList, threshold);
				logger.info("fuzzy name matching finished -- " + Long.toString((System.nanoTime() - matchingStart)/1000000)+"ms");
				
				
				logger.info(result.toString());
				 Map<Long, RuleCat> ruleCatMap = createRuleCatMap(caseDispositionService);

				if (result.getTotalHits() >= 0) {
					Map<String, DerogResponse> _responses = result.getResponses();
					for (String key : _responses.keySet()) {
						List<DerogHit> derogs = _responses.get(key).getDerogIds();
						for (DerogHit hit : derogs) {

							paxWatchlistLinkRepository.savePaxWatchlistLink(new Date(), hit.getPercent(), 0,
									passenger.getId(), Long.parseLong(hit.getDerogId()));

						    Case existingCase = existingCases.get(passenger.getId()); 
						    
							// make a call to open case here
							new NameMatchCaseMgmtUtils().nameMatchCaseProcessing(passenger,
									Long.parseLong(hit.getDerogId()), hit.getWatchlistName(),
									
										flight	,existingCase,ruleCatMap,
									caseDispositionService);
						}
					}
				}
				
				passengerRepository.setPassengerWatchlistTimestamp(passenger.getId(), new Date());
			}
		}
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
		logger.info("entering findMatchesBasedOnTimeThreshold()");
		int totalMatchCount = 0;
		long startTime = System.nanoTime();
		// get flights that are arriving between timeOffset and "now".
		Map<Flight,List<Passenger>> passengers = getPassengersOnFlightsWithinTimeRange();
		long endTime = System.nanoTime();
		logger.info("Execution time for getFlightsWithinTimeRange() = " + (endTime - startTime) / 1000000 + "ms");
		
		// Begin matching for all passengers on all flights retrieved within time frame.
		if (passengers != null && passengers.size() > 0) { // Don't try and match if no flights
			;
			startTime = System.nanoTime();
			for (Flight f : passengers.keySet()) {
				for(Passenger passenger: passengers.get(f)) {
					saveWatchListMatchByPaxId(createCaseMap(
							passengers.
							values().
							stream().
							flatMap(List::stream).
							collect(Collectors.toList()).
							stream().map(p -> p.getId())
							.collect(Collectors.toList()),this.caseDispositionService), f, passenger);
				}
			}
			endTime = System.nanoTime();
			logger.info("Passenger count for matching service: " + passengers.size());
			logger.info("Execution time for saveWatchListMatchByPaxId() for loop = " + (endTime - startTime) / 1000000
					+ "ms");
		}
		// TODO totalMatchCount is not being added at the moment, but is something that
		// could be used in the future
		return totalMatchCount;
	}

	private Map<Flight, List<Passenger>> getPassengersOnFlightsWithinTimeRange() {
		logger.info("entering getFlightsWithinTimeRange()");
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
		Map<Flight, List<Passenger>> passengers = new HashMap<Flight, List<Passenger>>();
		if (flights != null && flights.size() > 0) {
			startTime = System.nanoTime();
			for (Flight f : flights) {
				passengers.put(f, passengerRepository.getPassengersByFlightId(f.getId()));
			}
			endTime = System.nanoTime();
			logger.info(
					"Execution time for getPassengersOnFlightsWithingTimeRange() get passenger by flight ID for loop = "
							+ (endTime - startTime) / 1000000 + "ms");
		}
		logger.info("Number of flights found within " + timeOffsetHours + " hours and " + timeOffsetMinutes
				+ " minutes of arrival or departure. Flight Count: " + flights.size());
		return passengers;
	}
}
