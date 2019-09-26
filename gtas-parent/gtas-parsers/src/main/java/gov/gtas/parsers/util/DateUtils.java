/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

/**
 */
public final class DateUtils {
	public static final String TWENTY_FOUR_HOUR_TIME_FORMAT_SEC = "HHmmss";
	public static final String TWENTY_FOUR_HOUR_TIME_FORMAT = "HHmm";

	public static final String DATE_FORMAT_DAY_FIRST = "ddMMyy";
	public static final String DATE_FORMAT_YEAR_FIRST = "yyMMdd";

	public static final String DT_FORMAT_YEAR_FIRST = DATE_FORMAT_YEAR_FIRST + TWENTY_FOUR_HOUR_TIME_FORMAT;
	public static final String DT_FORMAT_DAY_FIRST = DATE_FORMAT_DAY_FIRST + TWENTY_FOUR_HOUR_TIME_FORMAT;
	public static final String DT_FORMAT_DAY_FIRST_SEC = DATE_FORMAT_DAY_FIRST + TWENTY_FOUR_HOUR_TIME_FORMAT_SEC;
	public static final String DT_FORMAT_MONTH_GMT = "ddMMM" + TWENTY_FOUR_HOUR_TIME_FORMAT + "Z";

	private DateUtils() {
	}

	/**
	 * set the time portion of a Date to all 0's
	 */
	public static Date stripTime(Date d) {
		if (d == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Integer calculateAge(Date dob) {
		if (dob == null) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(dob);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		LocalDate today = LocalDate.now();
		LocalDate birthday = LocalDate.of(year, month + 1, day); // cal is 0-based. yuck
		Period p = Period.between(birthday, today);
		if (p.getYears() < 0) {
			return 0;
		}
		return p.getYears();
	}

	public static Integer calculateValidVisaPeriod(Date etd, Date expDt) {
		if (etd == null || expDt == null) {
			return 0;
		}
		long etdMillis = etd.getTime();
		long expMillis = expDt.getTime();
		return (int) Math.round((expMillis - etdMillis) / (24 * 60 * 60 * 1000));
	}
}
