package gov.gtas.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.model.Seat;
import gov.gtas.repository.SeatRepository;

@RunWith(MockitoJUnitRunner.class)
public class SeatServiceTest {
	@Mock
	SeatRepository seatRepository;

	SeatService seatService;

	Long flightId = 123L;
	Long paxId = 456L;

	@Before
	public void before() {
		seatService = new SeatServiceImpl(seatRepository);

	}

	@Test
	public void testFindSeatNumberByFlightIdAndPassengerId() {
		List<Seat> seatList = new ArrayList<Seat>();
		Seat seat = new Seat();
		seat.setNumber("50A");
		seatList.add(seat);

		Mockito.when(seatRepository.findByFlightIdAndPassengerId(flightId, paxId)).thenReturn(seatList);
		List<String> seatNumber = seatService.findSeatNumberByFlightIdAndPassengerId(flightId, paxId);
		assertEquals("50A", seatNumber.get(0));
	}

	public void testFindSeatNumberByFlightIdAndPassengerIdWithTwoSeats() {
		List<Seat> seatList = new ArrayList<Seat>();
		Seat seat1 = new Seat();
		Seat seat2 = new Seat();

		seat1.setNumber("50A");
		seat2.setNumber("50B");

		seatList.add(seat1);
		seatList.add(seat2);

		Mockito.when(seatRepository.findByFlightIdAndPassengerId(flightId, paxId)).thenReturn(seatList);
		List<String> seatNumber = seatService.findSeatNumberByFlightIdAndPassengerId(flightId, paxId);
		assertEquals("50A", seatNumber.get(0));
		assertEquals("50B", seatNumber.get(1));

	}

	public void testFindSeatNumberByFlightIdAndPassengerIdWithThreeSeats() {
		List<Seat> seatList = new ArrayList<Seat>();
		Seat seat1 = new Seat();
		Seat seat2 = new Seat();
		Seat seat3 = new Seat();

		seat1.setNumber("50A");
		seat2.setNumber("50B");
		seat3.setNumber("50C");

		seatList.add(seat1);
		seatList.add(seat2);
		seatList.add(seat3);

		Mockito.when(seatRepository.findByFlightIdAndPassengerId(flightId, paxId)).thenReturn(seatList);
		List<String> seatNumber = seatService.findSeatNumberByFlightIdAndPassengerId(flightId, paxId);
		assertEquals("50A", seatNumber.get(0));
		assertEquals("50B", seatNumber.get(1));
		assertEquals("50C", seatNumber.get(2));
	}

}
