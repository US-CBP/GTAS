package gov.gtas.services;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.enumtype.HitSeverityEnum;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.BookingDetail;
import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitMaker;
import gov.gtas.model.ManualHit;
import gov.gtas.model.MutableFlightDetails;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.User;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.services.dto.PassengerNoteSetDto;
import gov.gtas.services.dto.PaxDetailPdfDocRequest;
import gov.gtas.vo.HitDetailVo;
import gov.gtas.vo.NoteVo;
import gov.gtas.vo.passenger.FlightVoForFlightHistory;

@RunWith(MockitoJUnitRunner.class)
public class EventReportServiceTest {

	@InjectMocks
	EventReportServiceImpl eventReportService;
	@Mock
	HitDetailService hitDetailService;
	@Mock
	PassengerService passengerService;
	@Mock
	PassengerNoteService passengerNoteService;

	PaxDetailPdfDocRequest paxDetailPdfDocRequest = new PaxDetailPdfDocRequest();

	@Before
	public void before() {
		initMocks(this);
		Long paxId1 = 1L;
		User u = new User();
		u.setLastName("L");
		u.setFirstName("F");
		HitCategory hitcat = new HitCategory();
		hitcat.setDescription("Hit1");
		hitcat.setSeverity(HitSeverityEnum.HIGH);
		hitcat.setName("HitName");
		HitMaker hitMaker = new ManualHit();
		hitMaker.setAuthor(u);
		hitMaker.setHitCategory(hitcat);
		HitDetail hitDetail = new HitDetail(HitTypeEnum.USER_DEFINED_RULE);
		hitDetail.setHitMaker(hitMaker);
		hitDetail.setCreatedDate(Calendar.getInstance().getTime());
		hitDetail.setFlightId(100L);
		hitDetail.setTitle("Hit 1");
		hitDetail.setPassengerId(1L);
		Set<HitDetail> hitDetailSet = new HashSet<HitDetail>();
		Flight flight = new Flight();
		flight.setId(150L);
		flight.setFlightNumber("501");
		flight.setOrigin("IAD");
		flight.setDestination("ADD");
		flight.setEtdDate(Calendar.getInstance().getTime());
		MutableFlightDetails mfd = new MutableFlightDetails();
		mfd.setEta(Calendar.getInstance().getTime());
		flight.setMutableFlightDetails(mfd);
		hitDetail.setFlight(flight);
		hitDetailSet.add(hitDetail);

		List<Passenger> passengerList = new ArrayList<Passenger>();
		Passenger passenger = new Passenger();
		BookingDetail bookingDetails = new BookingDetail();
		bookingDetails.setFlight(flight);
		bookingDetails.setId(22L);
		bookingDetails.setFlightNumber("501");
		bookingDetails.setOrigin("IAD");
		bookingDetails.setDestination("ADD");
		bookingDetails.setEtdDate(Calendar.getInstance().getTime());
		bookingDetails.setEtaDate(Calendar.getInstance().getTime());
		Set<BookingDetail> bookingDetailSet = new HashSet<BookingDetail>();
		bookingDetailSet.add(bookingDetails);
		PassengerDetails passengerDetails = new PassengerDetails();
		passengerDetails.setFirstName("AAA");
		passengerDetails.setLastName("BBB");
		passengerDetails.setGender("M");
		passenger.setId(paxId1);
		passenger.setPassengerDetails(passengerDetails);
		passenger.setFlight(flight);
		passenger.setBookingDetails(bookingDetailSet);
		passengerList.add(passenger);
		NoteVo noteVo = new NoteVo();
		noteVo.setId(1L);
		noteVo.setCreatedBy("ZZZ");
		noteVo.setPlainTextNote("Note1Note1");
		LinkedHashSet<NoteVo> noteVoSet = new LinkedHashSet<NoteVo>();
		noteVoSet.add(noteVo);
		PassengerNoteSetDto passengerNoteSetDto = new PassengerNoteSetDto(noteVoSet, 1L);

		Mockito.when(hitDetailService.getByPassengerId(paxId1)).thenReturn(hitDetailSet);
		Mockito.when(passengerService.getBookingDetailHistoryByPaxID(paxId1)).thenReturn(passengerList);
		Mockito.when(passengerNoteService.getAllEventNotes(paxId1)).thenReturn(passengerNoteSetDto);

	}

	/** Tests cover page table creation */
	@Test
	public void setsetHitInformationTest() {

		try {

			eventReportService.setHitInformation(paxDetailPdfDocRequest, 1L);
			LinkedHashSet<HitDetailVo> hitDetailSet = paxDetailPdfDocRequest.getHitDetailVoList();
			Assert.assertNotNull(hitDetailSet);
			Assert.assertEquals(1, hitDetailSet.size());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void setFlightHistoryTest() {

		try {
			eventReportService.setFlightHistory(paxDetailPdfDocRequest, 1L);
			List<FlightVoForFlightHistory> flightHistory = paxDetailPdfDocRequest.getFlightHistoryVoList();
			Assert.assertEquals(2, flightHistory.size());
			FlightVoForFlightHistory fHistory = flightHistory.get(0);
			FlightVoForFlightHistory bdHistory = flightHistory.get(1);
			Assert.assertEquals(String.valueOf("150"), fHistory.getFlightId());
			Assert.assertEquals(String.valueOf("501"), bdHistory.getFlightNumber());
			Assert.assertEquals(String.valueOf("22"), bdHistory.getFlightId());
			Assert.assertEquals(String.valueOf("501"), bdHistory.getFlightNumber());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void setNotesTest() {

		try {

			eventReportService.setNotes(paxDetailPdfDocRequest, 1L);
			LinkedHashSet<NoteVo> noteVoSet = paxDetailPdfDocRequest.getEventNotesSet();
			Assert.assertNotNull(noteVoSet);
			for (NoteVo noteVo : noteVoSet) {
				Assert.assertEquals("Note1Note1", noteVo.getPlainTextNote());
				Assert.assertEquals("ZZZ", noteVo.getCreatedBy());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
