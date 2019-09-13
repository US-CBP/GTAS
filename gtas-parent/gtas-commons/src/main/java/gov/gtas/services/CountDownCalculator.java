/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.vo.passenger.CountDownVo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

class CountDownCalculator {

    private final Date currentTime;

    CountDownCalculator(Date currentTime) {
        this.currentTime = currentTime;
    }

    CountDownVo getCountDownFromDate(Date countDownTo) {
        return getCountDownFromDate(countDownTo, 30, 30);
    }

    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    CountDownVo getCountDownFromDate(Date countDownTo, int minBefore, int minAfter) {
        long currentTimeMillis = currentTime.getTime();
        long countDownToMinusCurrentTimeInMillis = countDownTo.getTime() - currentTimeMillis;
        long countDownSeconds = countDownToMinusCurrentTimeInMillis / 1000;
        long daysLong = countDownSeconds / 86400;
        long secondsRemainder1 = countDownSeconds % 86400;
        long hoursLong = secondsRemainder1 / 3600;
        long secondsRemainder2 = secondsRemainder1 % 3600;
        long minutesLong = secondsRemainder2 / 60;
        String daysString = (countDownSeconds < 0 && daysLong == 0) ? "-" + daysLong
                : Long.toString(daysLong);
        String countDownString = daysString + "d " + Math.abs(hoursLong) + "h " + Math.abs(minutesLong) + "m";
        LocalDateTime currentDate = convertToLocalDateViaMilisecond(currentTime);
        LocalDateTime minutesBefore = currentDate.minusMinutes(minBefore);
        LocalDateTime minutesAfter = currentDate.plusMinutes(minAfter);
        LocalDateTime currentDateAsLocalDateTime = convertToLocalDateViaMilisecond(countDownTo);
        boolean closeToCountDown = false;
        if (currentDateAsLocalDateTime.isAfter(minutesBefore) && currentDateAsLocalDateTime.isBefore(minutesAfter)) {
            closeToCountDown = true;
        }
        return new CountDownVo(countDownString, closeToCountDown, countDownToMinusCurrentTimeInMillis);
    }
    private LocalDateTime convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }
}
