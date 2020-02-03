/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matcher.quickmatch;

import gov.gtas.model.Passenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class QuickMatcherImpl implements QuickMatcher {

	private final Logger logger = LoggerFactory.getLogger(QuickMatcherImpl.class);

	/*
	 * Do not make this a bean without taking multithreading into account.
	 */
	private final MatchingContext cxt;
	private static final Set<Long> EMPTY_DEROGLIST = new HashSet<>();

	// For normal operations, the MatchingContext gets accuracyMode from config files
	public QuickMatcherImpl(final List<HashMap<String, String>> watchListItems) {
		this.cxt = new MatchingContext();

		// sanity check
		if (watchListItems == null)
			this.cxt.initialize(new ArrayList<>());
		else
			this.cxt.initialize(watchListItems);
	}

	// For testing, to force and accuracyMode
	public QuickMatcherImpl(final List<HashMap<String, String>> watchListItems, final String accuracyMode) {
		this.cxt = new MatchingContext();

		// sanity check
		if (watchListItems == null)
			this.cxt.initialize(new ArrayList<>(), accuracyMode);
		else
			this.cxt.initialize(watchListItems, accuracyMode);
	}

	public MatchingContext getCxt() {
		return cxt;
	}

	@Override
	public MatchingResult match(Passenger passenger) {
		return this.match(passenger, cxt.getJaroWinklerThreshold(), cxt.getDobYearOffset());
	}

	@Override
	public MatchingResult match(Passenger passenger, float threshold, int dobYearOffset, Set<Long> foundDerogIds) {

		List<HashMap<String, String>> passengers = new ArrayList<>();
		HashMap<String, String> p = new HashMap<>();
		p.put("firstName", passenger.getPassengerDetails().getFirstName());
		p.put("lastName", passenger.getPassengerDetails().getLastName());
		p.put("dob", new SimpleDateFormat("yyyy-MM-dd").format(passenger.getPassengerDetails().getDob()));
		p.put("gtasId", passenger.getId().toString());
		passengers.add(p);

		this.cxt.setJaroWinklerThreshold(threshold);

		this.cxt.setDobYearOffset(dobYearOffset);

		return this.cxt.match(passengers, foundDerogIds.stream().map(String::valueOf).collect(Collectors.toSet()));

	}

	@Override
	public MatchingResult match(Passenger passenger, float threshold, int dobYearOffset) {
		return this.match(passenger, threshold, dobYearOffset, EMPTY_DEROGLIST);
	}
}
