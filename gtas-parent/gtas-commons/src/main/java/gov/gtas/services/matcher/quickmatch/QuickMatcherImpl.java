/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matcher.quickmatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.gtas.model.Passenger;

@Component
@Scope("prototype")
public class QuickMatcherImpl implements QuickMatcher {

	private final MatchingContext cxt;

	public QuickMatcherImpl() {
		this.cxt = new MatchingContext();
	}

	public MatchingContext getCxt() {
		return cxt;
	}

	@Override
	public MatchingResult match(Passenger passenger, final List<HashMap<String, String>> watchListItems) {
		return this.match(passenger, watchListItems);
	}

	@Override
	public MatchingResult match(Passenger passenger, List<HashMap<String, String>> watchListItems, float threshold) {

		List<HashMap<String, String>> passengers = new ArrayList<>();
		HashMap<String, String> p = new HashMap<>();
		p.put("firstName", passenger.getPassengerDetails().getFirstName());
		p.put("lastName", passenger.getPassengerDetails().getLastName());
		p.put("middleName",passenger.getPassengerDetails().getMiddleName());
		p.put("gtasId", passenger.getId().toString());
		passengers.add(p);

		//

		this.cxt.initialize(watchListItems);

		this.cxt.setJARO_WINKLER_THRESHOLD(threshold);

		return this.cxt.match(passengers);
	}

}

