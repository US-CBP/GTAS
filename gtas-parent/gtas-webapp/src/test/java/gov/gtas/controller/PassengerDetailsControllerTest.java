package gov.gtas.controller;

import gov.gtas.common.PassengerDetailService;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.BagRepository;
import gov.gtas.repository.BookingDetailRepository;
import gov.gtas.services.*;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.util.PaxDetailVoUtil;
import gov.gtas.vo.passenger.AddressVo;
import gov.gtas.vo.passenger.PassengerVo;
import gov.gtas.vo.passenger.PnrVo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

import java.sql.SQLException;
import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class PassengerDetailsControllerTest {

	@Mock
	private ApisService apisService;

	@Mock
	private PassengerService pService;

	@Mock
	private FlightService fService;

	@Mock
	private BookingDetailRepository bookingDetailService;

	@Mock
	private PnrService pnrService;

	@Mock
	private MatchingService matchingService;

	@Mock
	private BagRepository bagRepository;

	@Mock
	private ApisMessageRepository apisMessageRepository;

	@Mock
	private HitDetailService hitDetailService;

	@Mock
	private PassengerNoteService paxNoteService;

	@Mock
	private NoteTypeService noteTypeService;

	@Mock
	private SeatService seatService;

	@Mock
	private PassengerDetailService passengerDetailService;

	@InjectMocks
	PassengerDetailsController passengerDetailsController;

	@Test
	public void passengerDetailControllerHappyPathTest() throws SQLException {
//		Passenger wally = TestData.getPassenger();
//		Mockito.when(fService.findById(2L)).thenReturn(TestData.getFlight());
//		Mockito.when(pService.findByIdWithFlightAndDocumentsAndMessageDetails(1L)).thenReturn(wally);
//		List<Pnr> pnrs = TestData.getPnrList();
//		Mockito.when(pnrService.findPnrByPassengerIdAndFlightId(1L, 2L)).thenReturn(pnrs);
//		Mockito.when(bagRepository.findFromFlightAndPassenger(2L, 1L)).thenReturn(TestData.getBags());
//		Mockito.when(apisMessageRepository.findByFlightIdAndPassengerId(2L, 1L))
//				.thenReturn(Collections.singletonList(TestData.getApisMessage()));
//		Mockito.when(apisMessageRepository.findPaxByFlightIdandPassengerId(2L, 1L))
//				.thenReturn(TestData.getPassenger());
		Mockito.when(passengerDetailService.generatePassengerVO(any(), any())).thenReturn(new PassengerVo());
		PassengerVo passengerVo = passengerDetailsController.getPassengerByPaxIdAndFlightId("1", "2");
		Assert.assertNotNull(passengerVo);
	}

	@Test
	public void passengerVoTest() {
		PassengerDetailsController passengerDetailsController = new PassengerDetailsController();
		PnrVo pnrVo = new PnrVo();
		AddressVo addressVo = new AddressVo();

		ArrayList<AddressVo> addresses = new ArrayList<AddressVo>();
		addresses.add(addressVo);
		pnrVo.setRaw("ADD++702:4327 LEGGETT AVENUE::::PT::904151599751'");
		pnrVo.setAddresses(addresses);

		try {
			assertNull(addressVo.getCity());
			PaxDetailVoUtil.parseRawMessageToSegmentList(pnrVo);
		} catch (Exception e) {
			fail("This method should not throw an exception when City is null!!!");
		}

	}
}
