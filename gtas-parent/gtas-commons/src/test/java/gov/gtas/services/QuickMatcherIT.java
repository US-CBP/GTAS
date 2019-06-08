/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import static gov.gtas.services.matcher.quickmatch.MatchingContext.DOB_YEAR_OFFSET;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.services.matcher.quickmatch.DerogHit;
import gov.gtas.services.matcher.quickmatch.MatchingResult;
import gov.gtas.services.matcher.quickmatch.QuickMatcher;
import gov.gtas.services.matcher.quickmatch.QuickMatcherImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class, CachingConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Rollback(true)
public class QuickMatcherIT {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(QuickMatcherIT.class);
	
	private QuickMatcher qm;
	private ObjectMapper mapper = new ObjectMapper();

	@SuppressWarnings("Duplicates")
	@Test
	public void testingStart() throws IOException, ParseException {
		String data = "{\"id\":null,\"action\":null,\"terms\":[{\"field\":\"dob\",\"type\":\"date\",\"value\":\"1992-07-15\"},{\"field\":\"firstName\",\"type\":\"string\",\"value\":\"FOO\"},{\"field\":\"lastName\",\"type\":\"string\",\"value\":\"BAR\"}]}";
		Passenger p = new Passenger();
		PassengerDetails pd = new PassengerDetails();
		List<HashMap<String, String>> derogList = new ArrayList<>();

		pd.setFirstName("FOO");
		pd.setLastName("BAR");
		pd.setDob((new SimpleDateFormat("yyyy-mm-dd").parse("1990-07-15")));
		pd.setMiddleName(null);
		p.setId(90909L);
		p.setPassengerDetails(pd);
		WatchlistItemSpec itemSpec = mapper.readValue(data, WatchlistItemSpec.class);
		HashMap<String, String> derogItem = new HashMap<>();
		derogItem.put(DerogHit.WATCH_LIST_NAME, "TEST");
		if (itemSpec != null && itemSpec.getTerms() != null) {
			for (int i = 0; i < itemSpec.getTerms().length; i++) {
				derogItem.put(itemSpec.getTerms()[i].getField(), itemSpec.getTerms()[i].getValue());
			}
		}
		derogList.add(derogItem);
		this.qm = new QuickMatcherImpl(derogList);
		MatchingResult result = qm.match(p, .99F, DOB_YEAR_OFFSET);
		result.getResponses();
	}

	@Test
	public void testJaroWinklerWithSameDOBYear() throws IOException {

		Passenger p = getTestPassenger(1, "John", "Doe", null, "1988-03-15");

		List<HashMap<String, String>> derogList = getTestWL(11, "John", "Doe", "1988-03-15");
		
		this.qm = new QuickMatcherImpl(derogList);
		MatchingResult result = qm.match(p, .80F, DOB_YEAR_OFFSET);
		result.getResponses();

		assertEquals(1, result.getTotalHits());

	}

	@Test
	public void jaroWinklerHitWith4YearsDOBYearOffsetShouldFail() throws IOException {

		Passenger p = getTestPassenger(2, "John", "Doe", null, "1988-03-15");

		List<HashMap<String, String>> derogList = getTestWL(12, "John", "Doe", "1984-03-15");

		this.qm = new QuickMatcherImpl(derogList);
		MatchingResult result = qm.match(p, .80F, DOB_YEAR_OFFSET);
		result.getResponses();

		assertEquals(0, result.getTotalHits());
	}
	
	@Test
	public void doubleMetaphoneMissAndjaroWinklerHitWithSameDOBShouldHit() throws IOException {

		Passenger p = getTestPassenger(2, "David", "Josph", null, "1988-03-15");

		List<HashMap<String, String>> derogList = getTestWL(12, "David", "Jospp", "1988-03-15");

		this.qm = new QuickMatcherImpl(derogList);
		MatchingResult result = qm.match(p, .96F, DOB_YEAR_OFFSET);
		result.getResponses();

		assertEquals(1, result.getTotalHits());
	}

	@Test
	public void doubleMetaphoneMissAndjaroWinklerHitWithDifferentDOBShouldNotHit() throws IOException {

		Passenger p = getTestPassenger(2, "David", "Josph", null, "1988-03-15");

		List<HashMap<String, String>> derogList = getTestWL(12, "David", "Jospp", "1988-03-16");

		this.qm = new QuickMatcherImpl(derogList);
		MatchingResult result = qm.match(p, .96F, DOB_YEAR_OFFSET);
		result.getResponses();

		assertEquals(0, result.getTotalHits());
	}
	
	@Test
	public void doubleMetaphoneAndjaroWinklerMatchWithDOBOffTheThresholdShouldNotHit() throws IOException {

		Passenger p = getTestPassenger(22322, "David", "Josph", null, "1988-03-15");

		List<HashMap<String, String>> derogList = getTestWL(312, "David", "Josph", "1980-03-16");
		this.qm = new QuickMatcherImpl(derogList);
		MatchingResult result = qm.match(p, .96F, DOB_YEAR_OFFSET);
		result.getResponses();

		assertEquals(0, result.getTotalHits());
	}
	
	@Test
	public void doubleMetaphoneMissAndjaroWinklerMatchWithDOBInTheThresholdShouldHit() throws IOException {

		Passenger p = getTestPassenger(22322, "Adve", "John", null, "1988-03-16");

		List<HashMap<String, String>> derogList = getTestWL(312, "dave", "John", "1988-03-16");
		this.qm = new QuickMatcherImpl(derogList);
		MatchingResult result = qm.match(p, .96F, DOB_YEAR_OFFSET);
		result.getResponses();

		assertEquals(1, result.getTotalHits());
	}
	
	private List<HashMap<String, String>> getTestWL(int id, String firstName, String lastName, String dob)
			throws IOException {

		String data = "{\"id\":" + id + ",\"action\":null,\"terms\":[{\"field\":\"dob\",\"type\":\"date\",\"value\":\""
				+ dob + "\"},{\"field\":\"firstName\",\"type\":\"string\",\"value\":\"" + firstName
				+ "\"},{\"field\":\"lastName\",\"type\":\"string\",\"value\":\"" + lastName + "\"}]}";

		WatchlistItemSpec itemSpec = mapper.readValue(data, WatchlistItemSpec.class);
		HashMap<String, String> derogItem = new HashMap<>();
		derogItem.put(DerogHit.WATCH_LIST_NAME, "Interpol");
		derogItem.put("derogId", String.valueOf(id));
		if (itemSpec != null && itemSpec.getTerms() != null) {
			for (int i = 0; i < itemSpec.getTerms().length; i++) {
				derogItem.put(itemSpec.getTerms()[i].getField(), itemSpec.getTerms()[i].getValue());
			}
		}
		List<HashMap<String, String>> derogList = new ArrayList<>();
		derogList.add(derogItem);

		return derogList;
	}

	private Passenger getTestPassenger(int id, String firstName, String lastName, String middleName, String dob) {

		Passenger p = new Passenger();
		PassengerDetails pd = new PassengerDetails();

		pd.setFirstName(firstName);
		pd.setLastName(lastName);
		pd.setMiddleName(middleName);
		try {
			pd.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		p.setId(Long.valueOf(id));
		p.setPassengerDetails(pd);

		return p;
	}
	
}
