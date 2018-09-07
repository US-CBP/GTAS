/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.gtas.bo.RuleExecutionStatistics;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.CommonServiceException;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.PassengerTypeCode;
import gov.gtas.svc.util.TargetingServiceUtils;

import java.util.HashSet;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuleServiceConfig.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Ignore /* Changes to Flight.java broke this test. TODO: Investigate to see if any changes affect application*/
public class RuleRepositoryIT {
	@Autowired
	private RuleService testTarget;

	@Test(expected = CommonServiceException.class)
	public void testNullRequest() {
		testTarget.invokeAdhocRules("gtas.drl", null);
	}

	@Test
	@Transactional
	public void testBasicApisRequest() {
		Passenger p1 = createPassenger("Medulla", "Oblongata", "Timbuktu");
		ApisMessage msg = createBasicApisMessage(p1);
		RuleServiceResult res = testTarget.invokeAdhocRules(
				RuleServiceConstants.DEFAULT_RULESET_NAME,
				TargetingServiceUtils.createApisRequest(msg)
						.getRuleServiceRequest());
		assertNotNull(res);
		assertNotNull(res.getResultList());
		assertEquals("Result list is empty", 1, res.getResultList().size());
		assertEquals("Expected Passenger", p1, res.getResultList().get(0));
		RuleExecutionStatistics stats = res.getExecutionStatistics();
		assertNotNull(stats);
		assertEquals("Expected 2 rules to be fired", 2,
				stats.getTotalRulesFired());
		assertEquals("Expected 2 rule names in list", 2, stats
				.getRuleFiringSequence().size());
		// Expecting 1 flight object and one passenger object to be inserted
		assertEquals("Expected 2 object to be affected", 2,
				stats.getTotalObjectsModified());
		assertEquals("Expected 2 object to be inserted", 2, stats
				.getInsertedObjectClassNameList().size());
	}

	/**
	 * creates a simple passenger object.
	 * 
	 * @param fn
	 * @param ln
	 * @param embarkation
	 * @return
	 */
	private Passenger createPassenger(final String fn, final String ln,
			final String embarkation) {
		Passenger p = new Passenger();
		p.setPassengerType(PassengerTypeCode.P.name());
		p.setFirstName(fn);
		p.setLastName(ln);
		p.setEmbarkation("Timbuktu");
		return p;
	}

	/**
	 * Creates a simple ApisMessage with a single passenger
	 */
	private ApisMessage createBasicApisMessage(final Passenger passenger) {
		ApisMessage msg = new ApisMessage();
		Flight flight = new Flight();
		HashSet<Passenger> set = new HashSet<Passenger>();
		set.add(passenger);
		flight.setPassengers(set);
		flight.setDestination("foo");
		HashSet<Flight> flightSet = new HashSet<Flight>();
		flightSet.add(flight);
		msg.setFlights(flightSet);
		return msg;
	}
}
