/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.vo.passenger.CountDownVo;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class CountDownCalculatorTest {

    private final LocalDateTime ldt = LocalDateTime.of(1,1,1,1,1);
    private final ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
    private final Date timeCheck = Date.from(zdt.toInstant());


    @Test
    public void test29Minutes() {
        CountDownCalculator countDownCalculator = new CountDownCalculator(timeCheck);
        LocalDateTime twentyNineMinutesLater = ldt.plusMinutes(29);
        ZonedDateTime laterZoneDateTime = twentyNineMinutesLater.atZone(ZoneId.systemDefault());
        Date countDownFrom = Date.from(laterZoneDateTime.toInstant());
        CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(countDownFrom);
        Assert.assertEquals("0d 0h 29m", countDownVo.getCountDownTimer());
        Assert.assertTrue(countDownVo.isCloseToCountDown());
    }

    @Test
    public void test31Minutes() {
        CountDownCalculator countDownCalculator = new CountDownCalculator(timeCheck);
        LocalDateTime twentyNineMinutesLater = ldt.plusMinutes(31);
        ZonedDateTime laterZoneDateTime = twentyNineMinutesLater.atZone(ZoneId.systemDefault());
        Date countDownFrom = Date.from(laterZoneDateTime.toInstant());
        CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(countDownFrom);
        Assert.assertEquals("0d 0h 31m", countDownVo.getCountDownTimer());
        Assert.assertFalse(countDownVo.isCloseToCountDown());
    }

    @Test
    public void testHoursDaysAndMinutes() {
        CountDownCalculator countDownCalculator = new CountDownCalculator(timeCheck);
        LocalDateTime modifiedDateTime = ldt.plusMinutes(31);
        modifiedDateTime = modifiedDateTime.plusHours(5);
        modifiedDateTime = modifiedDateTime.plusDays(2);
        ZonedDateTime laterZoneDateTime = modifiedDateTime.atZone(ZoneId.systemDefault());
        Date countDownFrom = Date.from(laterZoneDateTime.toInstant());
        CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(countDownFrom);
        Assert.assertEquals("2d 5h 31m", countDownVo.getCountDownTimer());
        Assert.assertFalse(countDownVo.isCloseToCountDown());
    }

    @Test
    public void testCountDownMillis() {
        CountDownCalculator countDownCalculator = new CountDownCalculator(timeCheck);
        LocalDateTime modifiedDateTime = ldt.plusMinutes(1);
        ZonedDateTime laterZoneDateTime = modifiedDateTime.atZone(ZoneId.systemDefault());
        Date countDownFrom = Date.from(laterZoneDateTime.toInstant());
        CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(countDownFrom);
        Assert.assertEquals(60000L, countDownVo.getMillisecondsFromDate());
        Assert.assertTrue(countDownVo.isCloseToCountDown());
    }

    @Test
    public void testCountDownMillisNegative() {
        CountDownCalculator countDownCalculator = new CountDownCalculator(timeCheck);
        LocalDateTime modifiedDateTime = ldt.minusMinutes(1);
        ZonedDateTime laterZoneDateTime = modifiedDateTime.atZone(ZoneId.systemDefault());
        Date countDownFrom = Date.from(laterZoneDateTime.toInstant());
        CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(countDownFrom);
        Assert.assertEquals(-60000L, countDownVo.getMillisecondsFromDate());
        Assert.assertTrue(countDownVo.isCloseToCountDown());
    }

    @Test
    public void testHoursDaysAndMinutesNegative() {
        CountDownCalculator countDownCalculator = new CountDownCalculator(timeCheck);
        LocalDateTime modifiedDateTime = ldt.minusMinutes(31);
        modifiedDateTime = modifiedDateTime.minusHours(5);
        modifiedDateTime = modifiedDateTime.minusDays(2);
        ZonedDateTime laterZoneDateTime = modifiedDateTime.atZone(ZoneId.systemDefault());
        Date countDownFrom = Date.from(laterZoneDateTime.toInstant());
        CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(countDownFrom);
        Assert.assertEquals("-2d 5h 31m", countDownVo.getCountDownTimer());
        Assert.assertFalse(countDownVo.isCloseToCountDown());
    }

    @Test
    public void testCombinationPositiveNegative() {
        CountDownCalculator countDownCalculator = new CountDownCalculator(timeCheck);
        LocalDateTime modifiedDateTime = ldt.minusMinutes(20);
        modifiedDateTime = modifiedDateTime.minusHours(2);
        modifiedDateTime = modifiedDateTime.plusDays(2);
        ZonedDateTime laterZoneDateTime = modifiedDateTime.atZone(ZoneId.systemDefault());
        Date countDownFrom = Date.from(laterZoneDateTime.toInstant());
        CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(countDownFrom);
        Assert.assertEquals("1d 21h 40m", countDownVo.getCountDownTimer());
        Assert.assertFalse(countDownVo.isCloseToCountDown());
    }


}
