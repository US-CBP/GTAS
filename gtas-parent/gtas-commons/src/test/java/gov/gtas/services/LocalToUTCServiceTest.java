/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.lookup.Airport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class LocalToUTCServiceTest {

    private final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2019,9,2,0,0);
    private final Date DATE_TIME = Date.from(LOCAL_DATE_TIME.atZone(ZoneOffset.UTC).toInstant());
    private final Instant INSTANT = LOCAL_DATE_TIME.atZone(ZoneOffset.UTC).toInstant();
    private final long FOUR_HOURS_IN_MILLIS_DULLES_OFFSET_EDT = 14400000;


    private final LocalDateTime LOCAL_NON_DST = LocalDateTime.of(2019,1,15,0,0);
    private final Date DATE_TIME_NON_DST = Date.from(LOCAL_NON_DST.atZone(ZoneOffset.UTC).toInstant());
    private final Instant INSTANT_NON_DST = LOCAL_NON_DST.atZone(ZoneOffset.UTC).toInstant();
    private final long FIVE_HOURS_IN_MILLIS_DULLES_OFFSET_EST = 18000000;


    @Mock
    AirportService airportService;

    @InjectMocks
    LocalToUTCServiceImpl localToUTCService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Airport iad = new Airport();
        iad.setCity("DULLES");
        iad.setCountry("USA");
        iad.setIcao("IAD");
        iad.setIcao("KIAD");
        iad.setLatitude(new BigDecimal(38.944500d));
        iad.setLongitude(new BigDecimal(-77.455803d));
        iad.setName("Washington Dulles International Airport");
        Mockito.when(airportService.getAirportByThreeLetterCode("IAD")).thenReturn(AirportServiceImpl.buildAirportVo(iad));
        Mockito.when(airportService.getAirportByFourLetterCode("KIAD")).thenReturn(AirportServiceImpl.buildAirportVo(iad));
    }


    @Test
    public void fourLetterIcaoTest() {
        long secondsSinceEpoch = INSTANT.toEpochMilli();
        long expected = secondsSinceEpoch + FOUR_HOURS_IN_MILLIS_DULLES_OFFSET_EDT;
        Date offsetDate = localToUTCService.convertFromAirportCode("KIAD", DATE_TIME);
        long actual = offsetDate.getTime();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void threeLetterIataTest() {
        long secondsSinceEpoch = INSTANT.toEpochMilli();
        long expected = secondsSinceEpoch + FOUR_HOURS_IN_MILLIS_DULLES_OFFSET_EDT;
        Date offsetDate = localToUTCService.convertFromAirportCode("IAD", DATE_TIME);
        long actual = offsetDate.getTime();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void standardTimeTestIcao() {
        long secondsSinceEpoch = INSTANT_NON_DST.toEpochMilli();
        long expected = secondsSinceEpoch + FIVE_HOURS_IN_MILLIS_DULLES_OFFSET_EST;
        Date offsetDate = localToUTCService.convertFromAirportCode("IAD", DATE_TIME_NON_DST);
        long actual = offsetDate.getTime();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void standardTimeTestIata() {
        long secondsSinceEpoch = INSTANT_NON_DST.toEpochMilli();
        long expected = secondsSinceEpoch + FIVE_HOURS_IN_MILLIS_DULLES_OFFSET_EST;
        Date offsetDate = localToUTCService.convertFromAirportCode("KIAD", DATE_TIME_NON_DST);
        long actual = offsetDate.getTime();
        Assert.assertEquals(expected, actual);
    }
}
