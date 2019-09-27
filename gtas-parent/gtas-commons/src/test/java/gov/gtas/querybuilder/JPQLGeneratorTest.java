/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.querybuilder.exceptions.InvalidQueryRepositoryException;

@RunWith(MockitoJUnitRunner.class)
public class JPQLGeneratorTest {

	EntityEnum queryTypePassenger;
	EntityEnum entityEnumAddress;
	EntityEnum entityEnumDwellTime;
	EntityEnum entityEnumEmail;
	EntityEnum entityEnumFrequentFlyer;
	EntityEnum entityEnumBag;
	EntityEnum entityAgency;

	@Before
	public void before() {

		queryTypePassenger = EntityEnum.PASSENGER;
		entityEnumAddress = EntityEnum.ADDRESS;
		entityEnumDwellTime = EntityEnum.DWELL_TIME;
		entityEnumEmail = EntityEnum.EMAIL;
		entityEnumFrequentFlyer = EntityEnum.FREQUENT_FLYER;
		entityEnumBag = EntityEnum.BAG;
		entityAgency = EntityEnum.TRAVEL_AGENCY;
	}

	@Test
	public void testJoinPnrAddress() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumAddress, queryTypePassenger);
		Assert.assertEquals(" left join pnr.addresses a", joinCondition);

	}

	@Test
	public void testJoinPnrEmail() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumEmail, queryTypePassenger);
		Assert.assertEquals(" left join pnr.emails e", joinCondition);

	}

	@Test
	public void testJoinPnrDwellTime() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumDwellTime, queryTypePassenger);
		Assert.assertEquals(" left join pnr.dwellTimes dwell", joinCondition);

	}

	@Test
	public void testJoinPnrFrequentFlyer() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumFrequentFlyer, queryTypePassenger);
		Assert.assertEquals(" left join pnr.frequentFlyers ff", joinCondition);

	}

	@Test
	public void testJoinPassengerBag() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumBag, queryTypePassenger);
		Assert.assertEquals(" left join p.bags bag", joinCondition);

	}

	@Test
	public void testJoinTravelAgency() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityAgency, queryTypePassenger);
		Assert.assertEquals(" left join pnr.agencies ag", joinCondition);

	}

}
