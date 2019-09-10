package gov.gtas.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.vo.PassengerVo;

@RunWith(MockitoJUnitRunner.class)
public class ServiceUtilTest {
	@Mock
	ServiceUtil service;
	
	@Mock
	Flight flight;
	
	@Mock
	PassengerVo pvo;
	
	@Mock
	Passenger passenger;
	
	@Mock
	Passenger passenger2;
	
	@Test
	public void testFindPassengerOnFlight() {
		Mockito.when(service.findPassengerOnFlight(flight, pvo)).thenReturn(passenger);
		
		assertEquals(service.findPassengerOnFlight(flight, pvo), passenger);
		assertNotEquals(service.findPassengerOnFlight(flight, pvo), passenger2);
	}
	
	@Test
	public void testsFindPassengerOnFlight2() {
		flight.setId(null);
		
		assertNotNull(flight);
		assertEquals(service.findPassengerOnFlight(flight, pvo), null);
				
	}
	
	
}
