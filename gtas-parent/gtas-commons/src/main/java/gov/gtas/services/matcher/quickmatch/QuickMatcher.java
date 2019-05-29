/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matcher.quickmatch;

import java.util.HashMap;
import java.util.List;

import gov.gtas.model.Passenger;

public interface QuickMatcher {

	MatchingResult match(Passenger passenger, final List<HashMap<String, String>> watchListItems, float threshold,
			int dobYearOffset);

	MatchingResult match(Passenger passenger, final List<HashMap<String, String>> watchListItems);
}
