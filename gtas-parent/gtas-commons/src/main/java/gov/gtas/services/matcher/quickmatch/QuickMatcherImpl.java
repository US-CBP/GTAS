/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matcher.quickmatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.gtas.model.Passenger;
import gov.gtas.services.AppConfigurationService;

@Component
@Scope("prototype")
public class QuickMatcherImpl implements QuickMatcher {

	private final Logger logger = LoggerFactory.getLogger(QuickMatcherImpl.class);

	/*
	 * Do not make this a bean without taking multithreading into account.
	 */
	private final MatchingContext cxt;
	private final AppConfigurationService appConfigurationService;

	public QuickMatcherImpl(AppConfigurationService appConfigurationService) {
		this.appConfigurationService = appConfigurationService;
		this.cxt = new MatchingContext();
	}

	public MatchingContext getCxt() {
		return cxt;
	}

	@Override
	public MatchingResult match(Passenger passenger, final List<HashMap<String, String>> watchListItems) {
		return this.match(passenger, watchListItems, cxt.getJaroWinklerThreshold(), cxt.getDobYearOffset());
	}

	@Override
	public MatchingResult match(Passenger passenger, List<HashMap<String, String>> watchListItems, float threshold, int dobYearOffset) {

		List<HashMap<String, String>> passengers = new ArrayList<>();
		HashMap<String, String> p = new HashMap<>();
		p.put("firstName", passenger.getPassengerDetails().getFirstName());
		p.put("lastName", passenger.getPassengerDetails().getLastName());
		p.put("middleName", passenger.getPassengerDetails().getMiddleName());
		p.put("dob", new SimpleDateFormat("yyyy-MM-dd").format(passenger.getPassengerDetails().getDob()));
		p.put("gtasId", passenger.getId().toString());
		passengers.add(p);

		//

		this.cxt.initialize(watchListItems);

		this.cxt.setJaroWinklerThreshold(threshold);

	
		this.cxt.setDobYearOffset(dobYearOffset);

		return this.cxt.match(passengers);
	}

}
