package gov.gtas.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.gtas.repository.PassengerRepository;
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

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightPassenger;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.PassengerIDTag;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.util.EntityResolverUtils;

@RunWith(MockitoJUnitRunner.class)
public class ServiceUtilTest {
	private static final Logger logger = LoggerFactory.getLogger(ServiceUtilTest.class);
	@Mock
	PassengerRepository flightPassengerRepository;

	@InjectMocks
	ServiceUtil serviceUtil;

	Flight flight;
	Long flightId = 1L;
	Set<Passenger> list;

	@Before
	public void setUp() {

		flight = new Flight();
		flight.setId(flightId);

	}

	@Test
	public void testsFindPassengerUsingREF() {
		String pnrReservationReferenceNumber = "123";
		String recordLocator = "123ABC";
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, null);

		pvo.setPnrRecordLocator(recordLocator);
		pvo.setPnrReservationReferenceNumber(pnrReservationReferenceNumber);

		list = createFlightPassengerSet(null, null, null);

		Mockito.when(
				flightPassengerRepository.getPassengerUsingREF(flightId, pnrReservationReferenceNumber, recordLocator))
				.thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertNotNull(passenger);
		assertTrue(passenger.getId().equals(1L));

	}

	@Test
	public void testsFindPassengerByIdTag() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		PassengerVo pvo = createPassengerVo("gtas", "awesome", createSimpleDate("01/01/2019"), "M");
		String passengerIdTag = EntityResolverUtils.makeHashForPassenger(pvo.getFirstName(), pvo.getLastName(),
				pvo.getGender(), pvo.getDob());
		PassengerIDTag idTag = new PassengerIDTag();
		idTag.setIdTag(passengerIdTag);

		list = createFlightPassengerSet(null, null, idTag);

		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertTrue(passenger.getId().equals(1L));
		assertTrue(passenger.getPassengerIDTag().equals(idTag));
	}

	@Test
	public void testsFindPassengerByFirstNameLastNameDocNumberAndDob() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);

		Set<Document> documents = createSimpleDocumentList();
		List<DocumentVo> docVoList = createSimpleDocumentVoList();

		Date date = createSimpleDate("01/01/2019");
		PassengerVo pvo = createPassengerVo("gtas", "awesome", date, null);
		pvo.setDocuments(docVoList);

		list = createFlightPassengerSet(date, documents, null);

		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertNotNull(passenger);
		assertTrue(passenger.getId().equals(1L));

	}

	@Test
	public void testsFindPassengerByFirstNameLastNameDocNumberAndGender() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);

		Set<Document> documents = createSimpleDocumentList();
		List<DocumentVo> docVoList = createSimpleDocumentVoList();
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, "M");
		pvo.setDocuments(docVoList);

		list = createFlightPassengerSet(null, documents, null);

		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertNotNull(passenger);
		assertTrue(passenger.getId().equals(1L));
	}

	@Test
	public void testsFindPassengerByFirstNameLastNameDocAndNumber() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);

		Set<Document> documents = createSimpleDocumentList();
		List<DocumentVo> docVoList = createSimpleDocumentVoList();
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, null);
		pvo.setDocuments(docVoList);

		list = createFlightPassengerSet(null, documents, null);

		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertNotNull(passenger);
		assertTrue(passenger.getId().equals(1L));
	}

	@Test
	public void testsFindPassengerByFirstNameLastNameAndDOB() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);

		Date date = createSimpleDate("01/01/2019");
		PassengerVo pvo = createPassengerVo("gtas", "awesome", date, null);

		list = createFlightPassengerSet(date, null, null);
		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertTrue(passenger.getId().equals(1L));
		assertTrue(passenger.getPassengerDetails().getDob().equals(date));

	}

	// when we do not allow loosen entity resolution dob only
	@Test
	public void testsFindPassengerByFirstNameLastNameAndDOB2() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", false);

		Date date = createSimpleDate("01/01/2019");
		PassengerVo pvo = createPassengerVo("gtas", "awesome", date, null);

		list = createFlightPassengerSet(date, null, null);
		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertNull(passenger);

	}

	@Test
	public void testsFindPassengerByFirstNameLastNameAndGender() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, "M");

		list = createFlightPassengerSet(null, null, null);
		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertTrue(passenger.getId().equals(1L));
		assertTrue(passenger.getPassengerDetails().getGender().equals("M"));
	}

	// when we do not allow loosen entity resolution on gender only
	@Test
	public void testsFindPassengerByFirstNameLastNameAndGender2() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", false);
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, "M");

		list = createFlightPassengerSet(null, null, null);
		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertNull(passenger);
	}

	// Existing passenger has gender
	@Test
	public void testsFindPassengerByFirstNameAndLastName() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, "F");

		list = createFlightPassengerSet(null, null, null);
		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertNull(passenger);
	}

	@Test
	public void testsFindPassengerByFirstNameAndLastName2() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);
		PassengerVo pvo = createPassengerVo("gtas", "awesome", null, "F");

		list = createFlightPassengerSet(null, null, null);
		list.iterator().next().getPassengerDetails().setGender(null);
		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

		assertNotNull(passenger);
		assertTrue(passenger.getId().equals(1L));
	}

	@Test
	public void testNullPassengerIdTag() {
		ReflectionTestUtils.setField(serviceUtil, "allowLoosenResolution", true);
		Date dob = new Date();
		String fname = "gtas";
		String lname = "awesome";
		String gender = "M";
		PassengerVo pvo = createPassengerVo(fname, lname, dob, gender);

		list = createFlightPassengerSet(dob, null, null);
		list.iterator().next().setPassengerIDTag(null);
		Mockito.when(flightPassengerRepository.returnAPassengerFromParameters(flightId, pvo.getFirstName(),
				pvo.getLastName())).thenReturn(list);

		Passenger passenger = serviceUtil.findPassengerOnFlight(flight, pvo);

	}

	private Set<Document> createSimpleDocumentList() {
		Set<Document> documents = new HashSet<Document>();
		Document doc = new Document();
		doc.setDocumentNumber("12345");
		doc.setDocumentType("P");
		doc.setIssuanceCountry("USA");
		doc.setExpirationDate(createSimpleDate("01/01/2019"));

		documents.add(doc);
		return documents;
	}

	private List<DocumentVo> createSimpleDocumentVoList() {
		List<DocumentVo> documents = new ArrayList<DocumentVo>();
		DocumentVo doc = new DocumentVo();
		doc.setDocumentNumber("12345");
		doc.setDocumentType("P");
		doc.setIssuanceCountry("USA");
		doc.setExpirationDate(createSimpleDate("01/01/2019"));

		documents.add(doc);
		return documents;
	}

	private PassengerVo createPassengerVo(String firstName, String lastName, Date dob, String gender) {
		PassengerVo pvo = new PassengerVo();

		pvo.setFirstName(firstName);
		pvo.setLastName(lastName);
		pvo.setGender(gender);
		pvo.setDob(dob);

		return pvo;
	}

	private Set<Passenger> createFlightPassengerSet(Date dob, Set<Document> documents, PassengerIDTag idTag) {
		PassengerDetails pd = new PassengerDetails();
		pd.setFirstName("gtas");
		pd.setLastName("awesome");
		pd.setDob(dob);
		pd.setGender("M");

		Passenger passenger = new Passenger();
		passenger.setPassengerDetails(pd);
		passenger.setDocuments(documents);
		passenger.setPassengerIDTag(idTag);
		passenger.setId(1L);


		Set<Passenger> passengerSet = new HashSet<>();
		passengerSet.add(passenger);

		return passengerSet;
	}

	private Date createSimpleDate(String input) {
		Date date = null;

		try {
			date = new SimpleDateFormat("MM/dd/yyyy").parse(input);
		} catch (ParseException e) {
			logger.error("ERROR! An error occured when parsing date");
		}

		return date;
	}

}
