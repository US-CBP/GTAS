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
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.vo.passenger.PassengerVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class PassengerServiceImplTest {

    @Mock
    PassengerRepository passengerRepository;

    @InjectMocks
    PassengerServiceImpl passengerService;

    private final Passenger p = new Passenger();

    private final Flight f = new Flight();

    private final String SEAT_NUMBER = "a non blank seat number";
    @Before
    public void before() {
        MockitoAnnotations.initMocks(passengerRepository);

        //Make non-mockito mocked test data
        Set<Long> messageId = new HashSet<>(Collections.singletonList(5L));
        Set<Long> flightId = new HashSet<>(Collections.singletonList(1L));
        Set<Seat> paxSeats = new HashSet<>();
        paxSeats.add(new Seat());
        Seat unusedSeat = new Seat();
        unusedSeat.setFlight(f);
        unusedSeat.setNumber(SEAT_NUMBER);
        unusedSeat.setPassenger(p);
        unusedSeat.setPaxId(p.getId());
        unusedSeat.setApis(false);

        p.setSeatAssignments(paxSeats);
        p.setDocuments(new HashSet<>());
        p.getSeatAssignments().add(unusedSeat);
        p.setPassengerDetails(new PassengerDetails(p));
        p.setPassengerTripDetails(new PassengerTripDetails(p));
        f.setId(1L);
        MutableFlightDetails mfd = new MutableFlightDetails(1L);
        mfd.setEtd(new Date());
        mfd.setEta(new Date());
        f.setMutableFlightDetails(mfd);
        f.setEtdDate(new Date());
        Mockito.when(passengerRepository.getPassengerMatchingInformation(messageId, flightId)).thenReturn(new HashSet<Passenger>()
        {{
            add(p);
        }});
        ReflectionTestUtils.setField(passengerService, "passengerRespository", passengerRepository);
    }

    @Test
    public void noBagInfoReturned() {

        List<Object[]> queryResultList = new ArrayList<>();

        Object [] itemOnQueryResultList = new Object[3];
        itemOnQueryResultList[0] = p;
        itemOnQueryResultList[1] = f;
        queryResultList.add(itemOnQueryResultList);

        Pair<Long, List<Object[]>> findByCriteriaResult = new ImmutablePair<>(1L, queryResultList);

        PassengersRequestDto prdto = new PassengersRequestDto();
        prdto.setPageSize(25);

        Mockito.when(passengerRepository.findByCriteria(1L, prdto)).thenReturn(findByCriteriaResult);

        PassengersPageDto passengersByCriteriaTest = passengerService.getPassengersByCriteria(1L, prdto);

        PassengerVo processedPassenger = passengersByCriteriaTest.getPassengers().get(0);
        Assert.assertTrue(StringUtils.isBlank(processedPassenger.getSeat()));
        Assert.assertNotEquals(SEAT_NUMBER, processedPassenger.getSeat());
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
