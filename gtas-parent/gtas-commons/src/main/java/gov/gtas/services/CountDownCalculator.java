/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.vo.passenger.CountDownVo;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;

public class CountDownCalculator {

	private final Date currentTime;

	public CountDownCalculator(Date currentTime) {
		this.currentTime = currentTime;
	}

	CountDownCalculator() {
		this.currentTime = new Date();
	}

	public CountDownVo getCountDownFromDate(Date countDownTo) {
		return getCountDownFromDate(countDownTo, 30, 30);
	}

	@SuppressWarnings({ "SameParameterValue"})
	CountDownVo getCountDownFromDate(@NonNull Date countDownTo, int minBefore, int minAfter) {
		Objects.requireNonNull(countDownTo);
		long currentTimeMillis = currentTime.getTime();
		long countDownToMinusCurrentTimeInMillis = countDownTo.getTime() - currentTimeMillis;
		long countDownSeconds = countDownToMinusCurrentTimeInMillis / 1000;
		long daysLong = countDownSeconds / 86400;
		long secondsRemainder1 = countDownSeconds % 86400;
		long hoursLong = secondsRemainder1 / 3600;
		long secondsRemainder2 = secondsRemainder1 % 3600;
		long minutesLong = secondsRemainder2 / 60;
		StringBuilder stringBuilder = new StringBuilder();
		if (countDownSeconds < 0) {
		    stringBuilder.append("-");
        }
		if (!(daysLong == 0)) {
			stringBuilder.append(Math.abs(daysLong)).append("d "); // This is where the negative value will be
		}
		if (!(daysLong == 0) || Math.abs(hoursLong) != 0) {
			stringBuilder.append(Math.abs(hoursLong)).append("h ");
		}
		stringBuilder.append(Math.abs(minutesLong)).append("m");
		String countDownString = stringBuilder.toString();
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

	private boolean moreThanZeroDays(String daysString) {
		return (!"-0".equalsIgnoreCase(daysString) && !"0".equalsIgnoreCase(daysString));
	}

	private LocalDateTime convertToLocalDateViaMilisecond(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneOffset.UTC).toLocalDateTime();
	}
}
