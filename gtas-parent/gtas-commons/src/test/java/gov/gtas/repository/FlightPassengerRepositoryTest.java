package gov.gtas.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.model.FlightPassenger;

@RunWith(MockitoJUnitRunner.class)
public class FlightPassengerRepositoryTest {
	@Mock
	FlightPassengerRepository repository;
	
	@Mock
	FlightPassenger flightPassenger;
	
	Long flightId = 1L;
	String recordLocator = "123";
	String recordLocator2 = "12345";
	String pnrReservationReferenceNumber = "ABC123";
	
	@Test
	public void TestsGetPassengerUsingREF5() {
		ArrayList<FlightPassenger> list = new ArrayList<FlightPassenger>();
		list.add(flightPassenger);
		
		Mockito.when(repository.getPassengerUsingREF(flightId, pnrReservationReferenceNumber, recordLocator)).thenReturn(list);
		
		assertEquals(repository.getPassengerUsingREF(flightId, pnrReservationReferenceNumber, recordLocator).get(0), flightPassenger);
		assertTrue(repository.getPassengerUsingREF(flightId, pnrReservationReferenceNumber, recordLocator2).isEmpty());
	}
}
