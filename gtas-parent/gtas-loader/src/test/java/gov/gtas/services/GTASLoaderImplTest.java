/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.enumtype.MessageType;
import gov.gtas.model.*;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GTASLoaderImplTest {

	@Mock
	PassengerRepository passengerDao;

	@Mock
	ReportingPartyRepository rpDao;

	@Mock
	LoaderServices loaderServices;

	@Mock
	FlightRepository flightDao;

	@Mock
	DocumentRepository docDao;

	@Mock
	PhoneRepository phoneDao;

	@Mock
	PaymentFormRepository paymentFormDao;

	@Mock
	CreditCardRepository creditDao;

	@Mock
	FlightPassengerCountRepository flightPassengerCountRepository;

	@Mock
	AddressRepository addressDao;

	@Mock
	AgencyRepository agencyDao;

	@Mock
	MessageRepository<Message> messageDao;

	@Mock
	PassengerIDTagRepository passengerIdTagDao;

	@Mock
	FlightPassengerRepository flightPassengerRepository;

	@Mock
	FrequentFlyerRepository ffdao;

	@Mock
	LoaderUtils utils;

	@Mock
	BookingDetailRepository bookingDetailDao;

	@Mock
	MutableFlightDetailsRepository mutableFlightDetailsRepository;

	@Mock
	BagMeasurementsRepository bagMeasurementsRepository;

	@InjectMocks
	GtasLoaderImpl gtasLoader;

	@Before
	public void before() {
	}

	@Test
	public void uniqueMessage() throws LoaderException {
		gtasLoader.checkHashCode("pew pew");
	}

	@Test(expected = DuplicateHashCodeException.class)
	public void duplicateMessageException() throws LoaderException {
		String foo = "foo";
		Mockito.when(messageDao.findByHashCode(foo)).thenReturn(new Message());
		gtasLoader.checkHashCode(foo);
	}

	@Test
	public void documentEquality() throws ParseException {
		Passenger p = new Passenger();
		p.setId(1L);
		PassengerVo pvo = new PassengerVo();

		Document doc = new Document();
		Document doc2 = new Document();
		doc.setPassengerId(1L);
		doc2.setPassengerId(1L);
		doc.setDocumentNumber("1234");
		doc2.setDocumentNumber("1234");

		DocumentVo docVo = new DocumentVo();
		DocumentVo docVo2 = new DocumentVo();

		docVo.setDocumentNumber("1234");
		docVo2.setDocumentNumber("5678");

		doc.setMessageType(MessageType.PNR);
		doc.setMessageType(MessageType.PNR);

		p.addDocument(doc);
		p.addDocument(doc2);

		pvo.addDocument(docVo);
		pvo.addDocument(docVo2);
		Pnr pnr = new Pnr();

		Mockito.when(utils.createNewDocument(docVo2, pnr)).thenReturn(doc2);
		Mockito.when(utils.createNewDocument(docVo, pnr)).thenReturn(doc);
		gtasLoader.updatePassenger(p, pvo, pnr);
		assertEquals(2, p.getDocuments().size());
	}

}
