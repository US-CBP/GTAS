/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services.matcher;

import static gov.gtas.repository.AppConfigurationRepository.QUICKMATCH_DOB_YEAR_OFFSET;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.*;
import gov.gtas.services.PassengerService;
import gov.gtas.services.RuleHitPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PassengerWatchlistRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.matcher.quickmatch.DerogHit;
import gov.gtas.services.matcher.quickmatch.DerogResponse;
import gov.gtas.services.matcher.quickmatch.MatchingContext;
import gov.gtas.services.matcher.quickmatch.MatchingResult;
import gov.gtas.services.matcher.quickmatch.QuickMatcherImpl;

@Component
@Scope("prototype")
public class MatchingServiceImpl implements MatchingService {
	private final WatchlistItemRepository watchlistItemRepository;
	private final PassengerRepository passengerRepository;
	private final WatchlistRepository watchlistRepository;
	private final PassengerWatchlistRepository passengerWatchlistRepository;
	private final AppConfigurationRepository appConfigRepository;
	private final PassengerService passengerService;
	final private ApplicationContext applicationContext;

	@Value("${partial.hits.case.create}")
	private Boolean hitWillCreateCase;

	private ObjectMapper mapper = new ObjectMapper();
	private static Logger logger = LoggerFactory.getLogger(MatchingService.class);

	public MatchingServiceImpl(WatchlistItemRepository watchlistItemRepository, PassengerRepository passengerRepository,
			WatchlistRepository watchlistRepository, PassengerWatchlistRepository passengerWatchlistRepository,
			AppConfigurationRepository appConfigRepository, PassengerService passengerService,
			ApplicationContext applicationContext) {
		this.watchlistItemRepository = watchlistItemRepository;
		this.passengerRepository = passengerRepository;
		this.watchlistRepository = watchlistRepository;
		this.passengerWatchlistRepository = passengerWatchlistRepository;
		this.appConfigRepository = appConfigRepository;
		this.passengerService = passengerService;
		this.applicationContext = applicationContext;
	}

	// Overloaded method that will save on erroneous passenger calls during
	// automated run. Automated run already contains passenger objects.
	public void performFuzzyMatching(Long id) {
		logger.debug("Starting fuzzy matching");
		Passenger passenger = passengerRepository.getFullPassengerById(id);
		RuleHitPersistenceService ruleHitPersistenceService = applicationContext
				.getBean(RuleHitPersistenceService.class);
		Flight flight = passenger.getFlight();
		MatcherParameters matcherParameters = getMatcherParameters();
		Set<HitDetail> hitDetailSet = performFuzzyMatching(flight, passenger, matcherParameters);
		ruleHitPersistenceService.persistToDatabase(hitDetailSet);
		logger.debug("ending fuzzy matching");

	}

	private MatcherParameters getMatcherParameters() {
		logger.debug("getting matcher parameters");
		MatcherParameters matcherParameters = new MatcherParameters();
		matcherParameters
				.set_watchlists(watchlistRepository.getWatchlistByNames(Collections.singletonList("Passenger")));
		Map<Long, List<WatchlistItem>> watchlistListMap = new HashMap<>();
		logger.debug("getting items");

		for (Watchlist watchlist : matcherParameters.get_watchlists()) {
			List<WatchlistItem> watchlistsItemList = watchlistItemRepository
					.getItemsByWatchlistName(watchlist.getWatchlistName());
			watchlistListMap.put(watchlist.getId(), watchlistsItemList);
		}
		matcherParameters.setWatchlistListMap(watchlistListMap);
		matcherParameters.setThreshold(Float.parseFloat(
				(appConfigRepository.findByOption(AppConfigurationRepository.MATCHING_THRESHOLD).getValue())));
		matcherParameters.setDobYearOffset(getDobYearOffset());
		matcherParameters.get_watchlists().forEach(w -> {
			List<HashMap<String, String>> derogLists = createWatchlistItems(
					matcherParameters.getWatchlistListMap().get(w.getId()));
			matcherParameters.addDerogList(derogLists);
		});
		matcherParameters.setQm(new QuickMatcherImpl(matcherParameters.getDerogList()));
		logger.debug("got matcher parameters");
		return matcherParameters;
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

	public Set<HitDetail> performFuzzyMatching(Flight flight, Passenger passenger,
			MatcherParameters matcherParameters) {

		logger.debug("Starting fuzzy matching");
		Set<HitDetail> hitDetailSet = new HashSet<>();
		List<Watchlist> watchlistsList = matcherParameters.get_watchlists();
		for (Watchlist watchlist : watchlistsList) {
			if (passengerNeedsWatchlistCheck(passenger, watchlist)) {
				MatchingResult result = matcherParameters.getQm().match(passenger, matcherParameters.getThreshold(),
						matcherParameters.getDobYearOffset());
				Set<HitDetail> passengerHitDetails = processMatcherResults(passenger, result);
				hitDetailSet.addAll(passengerHitDetails);
			}
		}
		logger.debug("ended fuzzy matching");
		return hitDetailSet;
	}

	private Set<HitDetail> processMatcherResults(Passenger passenger, MatchingResult result) {
		Set<HitDetail> hitDetailSet = new HashSet<>();
		if (result.getTotalHits() >= 0) {
			Map<String, DerogResponse> _responses = result.getResponses();
			for (String key : _responses.keySet()) {
				List<DerogHit> derogs = _responses.get(key).getDerogIds();
				for (DerogHit hit : derogs) {
					if (hit.getPercent() != 1f) {
						HitDetail hitDetail = new HitDetail(HitTypeEnum.PARTIAL_WATCHLIST);
						hitDetail.setPassengerId(passenger.getId());
						hitDetail.setPassenger(passenger);
						hitDetail.setHitMakerId(Long.parseLong(hit.getDerogId()));
						hitDetail.setRuleId(Long.parseLong(hit.getDerogId()));
						hitDetail.setRuleConditions(hit.getRuleDescription());
						hitDetail.setCreatedDate(new Date());
						hitDetail.setTitle("Partial Name Match");
						hitDetail.setPercentage(hit.getPercent());
						hitDetail.setDescription(
								"Jaro-Winkler or Double Metaphone match on WL Item #" + hit.getDerogId());
						hitDetailSet.add(hitDetail);
					}
				}
			}
		}
		return hitDetailSet;
	}

	private int getDobYearOffset() {
		int dobYearOffset = MatchingContext.DOB_YEAR_OFFSET;
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
		return passenger.getPassengerWLTimestamp() == null
				|| passenger.getPassengerWLTimestamp().getWatchlistCheckTimestamp() == null || passenger
						.getPassengerWLTimestamp().getWatchlistCheckTimestamp().before(watchlist.getEditTimestamp());
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
		Set<Passenger> passengers = passengerService.getPassengersForFuzzyMatching(messageStatuses);
		int totalMatchCount = 0;
		long startTime = System.nanoTime();
		// get flights that are arriving between timeOffset and "now".
		long endTime = System.nanoTime();
		logger.debug("Execution to get initial matching data is = {}ms", (endTime - startTime) / 1000000);
		// Begin matching for all passengers on all flights retrieved within time frame.
		if (passengers != null && !passengers.isEmpty()) { // Don't try and match if no flights
			RuleHitPersistenceService ruleHitPersistenceService = applicationContext
					.getBean(RuleHitPersistenceService.class);
			startTime = System.nanoTime();
			Set<PassengerWLTimestamp> savingPassengerSet = new HashSet<>();
			MatcherParameters matcherParameters = getMatcherParameters();
			Set<HitDetail> partialWatchlistHits = new HashSet<>();
			for (Passenger passenger : passengers) {
				try {
					Set<HitDetail> passengerHits = performFuzzyMatching(passenger.getFlight(), passenger,
							matcherParameters);
					partialWatchlistHits.addAll(passengerHits);
					PassengerWLTimestamp passengerWLTimestamp;
					if (passenger.getPassengerWLTimestamp() == null) {
						passengerWLTimestamp = new PassengerWLTimestamp(passenger.getId(), new Date());
					} else {
						passengerWLTimestamp = passenger.getPassengerWLTimestamp();
						passengerWLTimestamp.setWatchlistCheckTimestamp(new Date());
					}
					if (passengerHits.size() > 0) {
						totalMatchCount++;
					}
					savingPassengerSet.add(passengerWLTimestamp);
				} catch (Exception e) {
					logger.error("failed to run watchlist check on passenger. " + "Will attempt a run on next pass.",
							e);
				}
			}
			passengerWatchlistRepository.saveAll(savingPassengerSet);
			ruleHitPersistenceService.persistToDatabase(partialWatchlistHits);
		}
		endTime = System.nanoTime();
		int paxTotal = passengers == null ? 0 : passengers.size();
		logger.debug("Passenger hit count and total run: {} {}", totalMatchCount, paxTotal);
		logger.info("Execution time for performFuzzyMatching() for loop  = {} ms, {} passengers",
				(endTime - startTime) / 1000000, paxTotal);
		return totalMatchCount;
	}
}
