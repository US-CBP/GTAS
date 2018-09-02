package gov.gtas.repository;

import static org.junit.Assert.assertTrue;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.services.dto.FlightsRequestDto;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
		CachingConfig.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Ignore //TODO:Fix or delete integration test below
public class FlightRepositoryIT extends
		AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private FlightRepository flightDao;

	@Autowired
	private FlightRepository flightRepository;

	@Before
	public void setUp() throws Exception {
		// DomesticFlight 1
		Flight f = new Flight();
		f.setCarrier("XX");
		f.setDirection("O");
		f.setFlightDate(new Date());
		f.setFlightNumber("0012");
		f.setFullFlightNumber("XX0012");
		f.setOrigin("LAX");
		f.setDestination("JFK");
		f.setOriginCountry("USA");
		f.setDestinationCountry("USA");
		Passenger p = new Passenger();
		p.setPassengerType("P");
		p.setFirstName("john");
		p.setLastName("doe");
		f.getPassengers().add(p);
		flightDao.save(f);

		// DomesticFlight 2
		f = new Flight();
		f.setCarrier("XX");
		f.setDirection("O");
		f.setFlightDate(new Date());
		f.setFlightNumber("0010");
		f.setFullFlightNumber("XX0010");
		f.setOrigin("LAS");
		f.setDestination("IAD");
		f.setOriginCountry("USA");
		f.setDestinationCountry("USA");
		p = new Passenger();
		p.setPassengerType("P");
		p.setFirstName("johnny");
		p.setLastName("dal");
		f.getPassengers().add(p);
		flightDao.save(f);

		// International Flight 1
		f = new Flight();
		f.setCarrier("YY");
		f.setDirection("O");
		f.setFlightDate(new Date());
		f.setFlightNumber("0013");
		f.setFullFlightNumber("YY0013");
		f.setOrigin("LAS");
		f.setDestination("MTY");
		f.setOriginCountry("USA");
		f.setDestinationCountry("MEX");
		p = new Passenger();
		p.setPassengerType("P");
		p.setFirstName("ted");
		p.setLastName("bart");
		f.getPassengers().add(p);
		flightDao.save(f);

		// International Flight 2
		f = new Flight();
		f.setCarrier("YY");
		f.setDirection("I");
		f.setFlightDate(new Date());
		f.setFlightNumber("0014");
		f.setFullFlightNumber("YY0014");
		f.setOrigin("BTE");
		f.setDestination("AEX");
		f.setOriginCountry("SLE");
		f.setDestinationCountry("USA");
		p = new Passenger();
		p.setPassengerType("P");
		p.setFirstName("mike");
		p.setLastName("great");
		f.getPassengers().add(p);
		flightDao.save(f);

		// International Flight 3
		f = new Flight();
		f.setCarrier("YY");
		f.setDirection("I");
		f.setFlightDate(new Date());
		f.setFlightNumber("0016");
		f.setFullFlightNumber("YY0016");
		f.setOrigin("MLW");
		f.setDestination("MFR");
		f.setOriginCountry("LBR");
		f.setDestinationCountry("USA");
		p = new Passenger();
		p.setPassengerType("P");
		p.setFirstName("lora");
		p.setLastName("speedier");
		f.getPassengers().add(p);
		flightDao.save(f);
	}

	@Test
	@Transactional
	public void testRetrieveInternationalFlights() {
		FlightsRequestDto frd = new FlightsRequestDto();
		frd.setFlightCategory("International");
		Pair<Long, List<Flight>> result = flightRepository.findByCriteria(frd);
		int count = 0;
		List<Flight> flights = result.getRight();
		for (Flight f : flights) {
			if ("YY".equalsIgnoreCase(f.getCarrier())) {
				count++;
			}
		}
		assertTrue(count == 3);
		assertTrue(result.getLeft() >= 3);
	}

	@Test
	@Transactional
	public void testRetrieveDomesticFlights() {
		FlightsRequestDto frd = new FlightsRequestDto();
		frd.setFlightCategory("Domestic");

		Pair<Long, List<Flight>> result = flightRepository.findByCriteria(frd);
		int count = 0;
		List<Flight> flights = result.getRight();
		for (Flight f : flights) {
			if ("XX".equalsIgnoreCase(f.getCarrier())) {
				count++;
			}
		}
		assertTrue(count == 2);
		assertTrue(result.getLeft() >= 2);
	}

	@Test
	@Transactional
	public void testRetrieveAllFlightsWithDTOValueALL() {
		FlightsRequestDto frd = new FlightsRequestDto();
		frd.setFlightCategory("All");

		Pair<Long, List<Flight>> result = flightRepository.findByCriteria(frd);
		int count = 0;
		List<Flight> flights = result.getRight();
		for (Flight f : flights) {
			if ("XX".equalsIgnoreCase(f.getCarrier())
					|| "YY".equalsIgnoreCase(f.getCarrier())) {
				count++;
			}
		}
		assertTrue(count == 5);
		assertTrue(result.getLeft() >= 5);
	}

	@Test
	@Transactional
	public void testRetrieveAllFlightsWithDTONull() {
		FlightsRequestDto frd = new FlightsRequestDto();

		Pair<Long, List<Flight>> result = flightRepository.findByCriteria(frd);
		int count = 0;
		List<Flight> flights = result.getRight();
		for (Flight f : flights) {
			if ("XX".equalsIgnoreCase(f.getCarrier())
					|| "YY".equalsIgnoreCase(f.getCarrier())) {
				count++;
			}
		}
		assertTrue(count == 5);
		assertTrue(result.getLeft() >= 5);
	}

}
