/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.*;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class, CachingConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Rollback
public class HitsSummaryRepositoryIT {

	@Autowired
	private HitsSummaryRepository testTarget;

	@Autowired
	private PassengerRepository passengerRepository;

	@Autowired
	private FlightRepository flightRepository;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	protected Object[] createPassengerFlight() {
		Passenger p = new Passenger();
		p.setDeleted(false);
		PassengerDetails passengerDetails = new PassengerDetails(p);
		passengerDetails.setDeleted(false);
		passengerDetails.setPassengerType("P");
		PassengerTripDetails passengerTripDetails = new PassengerTripDetails(p);
		p.setPassengerDetails(passengerDetails);
		p.setPassengerTripDetails(passengerTripDetails);
		passengerRepository.save(p);

		Flight f = new Flight();
		f.setFlightNumber("899");
		f.setOrigin("IAD");
		f.setCarrier("DL");
		f.setDestination("DXB");
		f.setDirection("I");
		flightRepository.save(f);

		return new Object[] { p, f };
	}

	private HitsSummary createUdrHitsSummary(Long udrId, Passenger p, Flight f) {
		return createHitsSummary(udrId, "R", p, f);
	}

	private HitsSummary createWlHitsSummary(Long wlId, Passenger p, Flight f) {
		return createHitsSummary(wlId, "D", p, f);
	}

	private HitsSummary createHitsSummary(Long ruleId, String hitType, Passenger p, Flight f) {
		HitsSummary ret = new HitsSummary();
		ret.setPassenger(p);
		ret.setRuleHitCount(1);
		ret.setWatchListHitCount(0);

		Set<HitDetail> detList = new HashSet<HitDetail>();
		HitDetail det = new HitDetail(HitTypeEnum.USER_DEFINED_RULE);
		det.setCreatedDate(new Date());
		det.setDescription("jkkjhg");
		det.setHitType(hitType);
		det.setRuleId(ruleId);
		det.setTitle("Hello");
		detList.add(det);
		ret = testTarget.save(ret);
		return ret;
	}
}
