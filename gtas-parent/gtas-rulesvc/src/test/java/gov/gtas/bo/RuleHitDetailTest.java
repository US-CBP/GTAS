/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;

import gov.gtas.model.PassengerDetails;
import gov.gtas.model.RuleHitDetail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RuleHitDetailTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHashCodeEquals() {
		Passenger p = new Passenger();
		PassengerDetails passengerDetails = new PassengerDetails();
		passengerDetails.setPassengerType("P");
		passengerDetails.setPassenger(p);
		p.setPassengerDetails(passengerDetails);
		p.setId(1L);
		Flight f = new Flight();
		f.setId(35L);

		RuleHitDetail det1 = new RuleHitDetail(25L, 31L, "foo", p, f, "bar");
		RuleHitDetail det2 = new RuleHitDetail(25L, 31L, "blah", p, f, "blah");
		assertEquals(det1, det2);

		Set<RuleHitDetail> detset = new HashSet<RuleHitDetail>();
		detset.add(det1);
		detset.add(det2);
		assertEquals(1, detset.size());
	}

}
