/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
import gov.gtas.repository.PassengerRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class PassengerServiceImplTest {

    @Mock
    PassengerRepository passengerRepository;

    @InjectMocks
    PassengerServiceImpl passengerService;

    private final Passenger p = new Passenger();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(passengerRepository);
        Set<Long> messageId = new HashSet<>(Collections.singletonList(5L));
        Set<Long> flightId = new HashSet<>(Collections.singletonList(1L));
        Mockito.when(passengerRepository.getPassengerMatchingInformation(messageId, flightId)).thenReturn(new HashSet<Passenger>()
        {{
            add(p);
        }});
    }

    @Test
    public void correctlyPassesParameters() {
        MessageStatus messageStatus = new MessageStatus();
        messageStatus.setFlightId(1L);
        messageStatus.setMessageId(5L);
        Set<Passenger> passengers = passengerService.getPassengersForFuzzyMatching(Collections.singletonList(messageStatus));
        Assert.assertEquals(passengers.size(), 1);
        Assert.assertEquals(passengers.iterator().next(), p);
    }
}
