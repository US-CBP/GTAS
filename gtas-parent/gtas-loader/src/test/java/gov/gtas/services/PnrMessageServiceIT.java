/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.repository.FlightLegRepository;
import gov.gtas.repository.FlightRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class,
		CachingConfig.class, })
@Rollback(true)
public class PnrMessageServiceIT extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private Loader svc;

	private File message;

	@Autowired
	private FlightRepository flightDao;
	
	@Autowired
	private FlightLegRepository flightLegRepository;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		this.message = new File(classLoader.getResource(
				"pnr-messages/2_pnrs_basic.edi").getFile());
	}

	@Test
	@Transactional
	public void testSaveFlight() throws ParseException {
		Flight f = new Flight();
		f.setCarrier("DL");
		f.setDirection("O");
		f.setFlightNumber("0012");
		f.setOrigin("LAX");
		f.setDestination("IAD");
		Passenger p = new Passenger();
		p.getPassengerDetails().setPassengerType("P");
		p.getPassengerDetails().setFirstName("john");
		p.getPassengerDetails().setLastName("doe");
		f.getPassengers().add(p);
		flightDao.save(f);
		assertNotNull(f.getId());
	}

	@Test
	@Transactional
	public void testFlightAndPax() throws ParseException {
		Flight f = new Flight();
		f.setCarrier("DL");
		f.setDirection("O");
		f.setFlightNumber("0012");
		f.setOrigin("LAX");
		f.setDestination("IAD");

		FlightVo fvo = new FlightVo();
		BeanUtils.copyProperties(f, fvo);
		List<FlightVo> flights = new ArrayList<>();
		flights.add(fvo);
		PassengerVo pvo = new PassengerVo();
		pvo.setPassengerType("P");
		pvo.setFirstName("sam");
		pvo.setLastName("doe");
		List<PassengerVo> passengers = new ArrayList<>();
		passengers.add(pvo);
		PassengerVo pvo2 = new PassengerVo();
		pvo2.setPassengerType("P");
		pvo2.setFirstName("sam2");
		pvo2.setLastName("doe2");
		passengers.add(pvo2);

		Set<Flight> dummy = new HashSet<>();
		Set<Passenger> paxDummy = new HashSet<>();
/*
		loaderRepo.processFlightsAndBookingDetails(flights, passengers, dummy,
				paxDummy, new ArrayList<FlightLeg>(),new String[]{"placeholder"},new HashSet<BookingDetail>());
*/
	//	List<Passenger> pax = paxDao.getPassengersByLastName("doe");
//		assertEquals(1, pax.size());
	}

	@Test()
	@Transactional
	public void testRunService() throws ParseException {
		svc.processMessage(this.message, new String[]{"placeholder"});
	}

	@Test()
	@Transactional
	public void testServiceWithBags() {
		ClassLoader classLoader = getClass().getClassLoader();
		this.message = new File(classLoader.getResource(
				"pnr-messages/pnrMessageExample.txt").getFile());
		svc.processMessage(this.message, new String[]{"placeholder"});
	}
	
	@Test
	@Transactional
	public void testProgressiveFlightWithDomesticContinuance() {
		this.message = new File(
				getClass().getClassLoader().getResource("pnr-messages/multiple_flight_leg_pnr.txt").getFile());
		svc.processMessage(this.message, new String[] { "FRA", "IAD", "UA", "0988", "1526097600000", "1526142000000" });
	}
}
