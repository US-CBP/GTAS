/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PnrRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class PnrServiceImplTest {


    @Mock
    PnrRepository pnrRespository;

    @InjectMocks
    PnrServiceImpl pnrService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(pnrRespository);
        ReflectionTestUtils.setField(pnrService, "pnrRepository", pnrRespository);
    }

    @Test
    public void testPnrReturn() {
        Flight flight = new Flight();
        flight.setId(3L);

        FlightLeg flightLeg = new FlightLeg();
        flightLeg.setFlight(flight);
        List<FlightLeg> flightLegs = new ArrayList<>();
        flightLegs.add(flightLeg);

        Passenger p = new Passenger();
        p.setId(3L);
        Set<Passenger> passengerSet = new HashSet<>();
        passengerSet.add(p);

        Pnr pnr = new Pnr();
        pnr.setFlightLegs(flightLegs);
        pnr.setPassengers(passengerSet);
        pnr.setRecordLocator("1");
        pnr.setCarrier("1");
        pnr.setHashCode("This is first hash");


        Pnr pnr1 = new Pnr();
        pnr1.setFlightLegs(flightLegs);
        pnr1.setPassengers(passengerSet);
        pnr1.setRecordLocator("1");
        pnr1.setCarrier("1");
        pnr1.setHashCode("This is second hash");

        Set<Pnr> mockedPnr = new HashSet<>();

        //Testing equality HERE.
        mockedPnr.add(pnr);
        mockedPnr.add(pnr1);

        Mockito.when(pnrRespository.getPnrsByPassengerIdAndFlightId(3L,3L)).thenReturn(mockedPnr);
        List<Pnr> testService =  pnrService.findPnrByPassengerIdAndFlightId(3L,3L);

        Assert.assertEquals(2, mockedPnr.size());
        Assert.assertEquals(2, testService.size());

    }

}
