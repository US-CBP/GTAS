/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.BookingDetail;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.PassengerRepository;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
		CachingConfig.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class PnrMessageServiceIT extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private Loader svc;

	private File message;

	@Autowired
	private FlightRepository flightDao;

	@Autowired
	private LoaderRepository loaderRepo;

	@Autowired
	private PassengerRepository paxDao;

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
		f.setFlightDate(new Date());
		f.setFlightNumber("0012");
		f.setOrigin("LAX");
		f.setDestination("IAD");
		Passenger p = new Passenger();
		p.setPassengerType("P");
		p.setFirstName("john");
		p.setLastName("doe");
		flightDao.save(f);
		assertNotNull(f.getId());
	}

	@Test
	@Transactional
	public void testFlightAndPax() throws ParseException {
		Flight f = new Flight();
		f.setCarrier("DL");
		f.setDirection("O");
		f.setFlightDate(new Date());
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
		pvo.setGender("M");

		pvo.setDob(new Date(LocalDate.of(1998, 5, 5).toEpochDay()));
		pvo.setLastName("doe");
		List<PassengerVo> passengers = new ArrayList<>();
		passengers.add(pvo);
		PassengerVo pvo2 = new PassengerVo();
		pvo2.setPassengerType("P");
		pvo2.setFirstName("sam2");
		pvo2.setDob(new Date(LocalDate.of(1979, 9, 12).toEpochDay()));
		pvo2.setGender("F");
		pvo2.setLastName("doe2");
		passengers.add(pvo2);

		Set<Flight> dummy = new HashSet<>();
		Set<Passenger> paxDummy = new HashSet<>();
		loaderRepo.processFlightsAndPassengers(flights, passengers, dummy,
				paxDummy, new ArrayList<>(),new String[]{"placeholder"},new HashSet<BookingDetail>());
		List<Passenger> pax = paxDao.getPassengersByLastName("doe");
		assertEquals(1, pax.size());
	}

	@Test()
	@Transactional
	public void testRunService() throws ParseException {
		svc.processMessage(this.message, new String[]{"placeholder"});
	}
}
