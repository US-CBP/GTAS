/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services.matcher;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.PaxWatchlistLink;
import gov.gtas.model.lookup.WatchlistCategory;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.repository.*;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.matcher.quickmatch.DerogHit;
import gov.gtas.services.matcher.quickmatch.DerogResponse;
import gov.gtas.services.matcher.quickmatch.MatchingResult;
import gov.gtas.services.matching.PaxWatchlistLinkVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


import java.util.*;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class MatchingServiceImplTest {

    private static final String TESTWATCHLIST_NAME = "testwatchlist_name";
    @Mock
    PaxWatchlistLinkRepository paxWatchlistLinkRepository;
    @Mock
    WatchlistItemRepository watchlistItemRepository;
    @Mock
    PassengerRepository passengerRepository;
    @Mock
    WatchlistRepository watchlistRepository;
    @Mock
    CaseDispositionService caseDispositionService;
    @Mock
    FlightFuzzyHitsRepository flightFuzzyHitsRepository;
    @Mock
    PassengerWatchlistRepository passengerWatchlistRepository;
    @Mock
    AppConfigurationRepository appConfigRepository;
    @Mock
    NameMatchCaseMgmtUtils nameMatchCaseMgmtUtils;
    @Mock
    PassengerService passengerService;

    private MatchingServiceImpl matchingService;

    private PaxWatchlistLink paxWatchlistLink;
    @Before
    public void before() {
        initMocks(this);

        matchingService = new MatchingServiceImpl(paxWatchlistLinkRepository, watchlistItemRepository, passengerRepository,
                watchlistRepository, caseDispositionService, flightFuzzyHitsRepository, passengerWatchlistRepository, appConfigRepository, passengerService, nameMatchCaseMgmtUtils);

        WatchlistItem watchlistItem = new WatchlistItem();
        watchlistItem.setItemData("{\"id\":null,\"action\":null,\"terms\":[{\"field\":\"firstName\",\"type\":\"string\",\"value\":\"FOO\"},{\"field\":\"lastName\",\"type\":\"string\",\"value\":\"BAR\"},{\"field\":\"dob\",\"type\":\"date\",\"value\":\"1964-11-07\"}]}");
        watchlistItem.setId(-9L);
        WatchlistCategory watchlistCategory = new WatchlistCategory();
        watchlistCategory.setName(TESTWATCHLIST_NAME);
        watchlistItem.setWatchlistCategory(watchlistCategory);

        paxWatchlistLink = new PaxWatchlistLink();
        paxWatchlistLink.setId(-999L);
        paxWatchlistLink.setLastRunTimestamp(new Date());
        paxWatchlistLink.setPercentMatch(9);
        paxWatchlistLink.setPassenger(new Passenger());
        paxWatchlistLink.setWatchlistItem(watchlistItem);
        paxWatchlistLink.setVerifiedStatus(0);

    }


    @Test
    public void caseToVo() {
        List<PaxWatchlistLink> paxWatchlistLinks = new ArrayList<>();
        paxWatchlistLinks.add(paxWatchlistLink);
        Mockito.when(paxWatchlistLinkRepository.findByPassengerId(-999L)).thenReturn(paxWatchlistLinks);
        List<PaxWatchlistLinkVo> paxWatchlistLinkVos = matchingService.findByPassengerId(-999L);
        Assert.assertEquals(1, paxWatchlistLinkVos.size());
        Assert.assertEquals(paxWatchlistLinkVos.get(0).getWatchlistCategory(), TESTWATCHLIST_NAME);
    }

    @Test
    public void testCaseCreation() {

        Flight flight = new Flight();
        Passenger passenger = new Passenger();
        MatcherParameters matcherParameters = new MatcherParameters();
        matcherParameters.setCaseMap(new HashMap<>());
        Map<String, DerogResponse> responses = new HashMap<>();
        DerogHit derogHit = new DerogHit("1", "2", 4,"3");
        List<DerogHit> derogHitList = new ArrayList<>();
        derogHitList.add(derogHit);
        DerogResponse derogResponse = new DerogResponse("1",derogHitList);
        responses.put("1", derogResponse );
        MatchingResult matchingResult = new MatchingResult(1, responses);
        matchingService.setHitWillCreateCase(true);
        ProcessedMatcherResults processedResults = matchingService.processMatcherResults(flight, passenger, matcherParameters, matchingResult );
        Assert.assertTrue(processedResults.getCaseCreated());
    }

}
