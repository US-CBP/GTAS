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
import gov.gtas.repository.*;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.matcher.quickmatch.DerogHit;
import gov.gtas.services.matcher.quickmatch.DerogResponse;
import gov.gtas.services.matcher.quickmatch.MatchingResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class MatchingServiceImplTest {

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

    private MatchingServiceImpl matchingService;

    @Before
    public void before() {
        initMocks(this);

        matchingService = new MatchingServiceImpl(paxWatchlistLinkRepository, watchlistItemRepository, passengerRepository,
                watchlistRepository, caseDispositionService, flightFuzzyHitsRepository, passengerWatchlistRepository, appConfigRepository, nameMatchCaseMgmtUtils);

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
