/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services.matcher;

import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.repository.*;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.repository.watchlist.WatchlistRepository;
import gov.gtas.services.PassengerService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class MatchingServiceImplTest {

	private static final String TESTWATCHLIST_NAME = "testwatchlist_name";

	@Mock
	WatchlistItemRepository watchlistItemRepository;
	@Mock
	PassengerRepository passengerRepository;
	@Mock
	WatchlistRepository watchlistRepository;
	@Mock
	FlightFuzzyHitsRepository flightFuzzyHitsRepository;
	@Mock
	PassengerWatchlistRepository passengerWatchlistRepository;
	@Mock
	AppConfigurationRepository appConfigRepository;
	@Mock
	PassengerService passengerService;

	private MatchingServiceImpl matchingService;


	@Before
	public void before() {
		initMocks(this);
		WatchlistItem watchlistItem = new WatchlistItem();
		watchlistItem.setItemData(
				"{\"id\":null,\"action\":null,\"terms\":[{\"field\":\"firstName\",\"type\":\"string\",\"value\":\"FOO\"},{\"field\":\"lastName\",\"type\":\"string\",\"value\":\"BAR\"},{\"field\":\"dob\",\"type\":\"date\",\"value\":\"1964-11-07\"}]}");
		watchlistItem.setId(-9L);
	}
}
