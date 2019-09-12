package gov.gtas.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import gov.gtas.model.Flight;
import gov.gtas.model.FlightPassenger;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.repository.FlightPassengerRepository;
import gov.gtas.util.EntityResolverUtils;

@RunWith(MockitoJUnitRunner.class)
public class ServiceUtilTest {
	private static final Logger logger = LoggerFactory.getLogger(ServiceUtilTest.class);
	@Mock
	FlightPassengerRepository flightPassengerRepository;


	@InjectMocks
	ServiceUtil serviceUtil;

	Flight flight;	
	Long flightId = 1L;
	List<FlightPassenger> list;


	@Before
	public void setUp() {
		
		flight = new Flight();
		flight.setId(flightId);
		
		Passenger passenger = new Passenger();		
		passenger.setId(1L);
		passenger.setFlight(flight);
		
		FlightPassenger flightPassenger = new FlightPassenger();
		flightPassenger.setPassenger(passenger);

		list = new ArrayList<FlightPassenger>();
		list.add(flightPassenger);

	}


	@Test
	public void testsFindPassengerUsingREF() {
		String pnrReservationReferenceNumber = "123";
		String recordLocator = "123ABC";
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, null);

		pvo.setPnrRecordLocator(recordLocator);
		pvo.setPnrReservationReferenceNumber(pnrReservationReferenceNumber);

		Mockito.when(flightPassengerRepository.getPassengerUsingREF(flightId, pnrReservationReferenceNumber, recordLocator)).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertTrue(passenger.getId().equals(1L));
		assertTrue(passenger.getFlight().getId().equals(flightId));

	}

	@Test
	public void testsFindPassengerByIdTag() {
		PassengerVo pvo = createPassengerVo("gtas", "awesome", createSimpleDate("01/01/2019"), "M");				
		String passengerIdTag = createPassengerIdTag(pvo);

		Mockito.when(flightPassengerRepository.getPassengerByIdTag(flightId, passengerIdTag)).thenReturn(list);		
		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertTrue(passenger.getId().equals(1L));
		assertTrue(passenger.getFlight().getId().equals(flightId));
	}

	@Test
	public void testsFindPassengerByFirstNameLastNameAndDOB() {
		PassengerVo pvo = createPassengerVo("gtas", "awesome", createSimpleDate("01/01/2019"), null);
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);
		Mockito.when(flightPassengerRepository.getPassengerByFirstNameLastNameAndDOB(
				flightId, pvo.getFirstName(), pvo.getLastName(), pvo.getDob())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertTrue(passenger.getId().equals(1L));
		assertTrue(passenger.getFlight().getId().equals(flightId));

	}

	//do not allow loosen entity resolution
	@Test
	public void testsFindPassengerByFirstNameLastNameAndDOB2() {
		PassengerVo pvo = createPassengerVo("gtas", "awesome", createSimpleDate("01/01/2019"), null);		
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", false);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertNull(passenger);

	}

	@Test
	public void testsFindPassengerByFirstNameLastNameAndGender() {
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, "M");
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);

		Mockito.when(flightPassengerRepository.getPassengerByFirstNameLastNameAndGender(
				flightId, pvo.getFirstName(), pvo.getLastName(), pvo.getGender())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);
		assertTrue(passenger.getId().equals(1L));
		assertTrue(passenger.getFlight().getId().equals(flightId));
	}

	//do not allow loosen entity resolution
	@Test
	public void testsFindPassengerByFirstNameLastNameAndGender2() {
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, "M");
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", false);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);
		assertNull(passenger);
	}


	private PassengerVo createPassengerVo(String firstName, String lastName, Date dob, String gender) {
		PassengerVo pvo = new PassengerVo();

		pvo.setFirstName(firstName);
		pvo.setLastName(lastName);
		pvo.setGender(gender);
		pvo.setDob(dob);

		return pvo;
	}

	private String createPassengerIdTag(PassengerVo pvo) {
		String input = String.join("",Arrays.asList(
				pvo.getFirstName().toUpperCase(), 
				pvo.getLastName().toUpperCase(), 
				pvo.getGender().toUpperCase(), 
				new SimpleDateFormat("MM/dd/yyyy").format(pvo.getDob())));

		String passengerIdTag = null;

		try {
			passengerIdTag = EntityResolverUtils.makeSHA1Hash(input);			
		} catch (NoSuchAlgorithmException e) {
			logger.error("ERROR! An error occured when trying to create passengerIdTag", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("ERROR! An error occured when trying to create passengerIdTag", e);
		}

		return passengerIdTag;

	}
	
	private Date createSimpleDate(String input)  {
		Date date = null;
		
		try {
			date = new SimpleDateFormat("MM/dd/yyyy").parse(input);
		} catch (ParseException e) {
			logger.error("ERROR! An error occured when parsing date");
		}
		
		return date;
	}

}
