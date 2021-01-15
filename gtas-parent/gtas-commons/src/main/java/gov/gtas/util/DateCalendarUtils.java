/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import gov.gtas.constant.RuleConstants;

import java.lang.*;

/**
 * Basic adaptation of commons DateUtils.
 */
public final class DateCalendarUtils {

	private DateCalendarUtils() {
	}

	private static final long MILLIS_IN_ONE_DAY = 86400000L;

	public static String addOneDayToJsondateString(String jsonDateString) throws ParseException {
		Date dt = parseJsonDate(jsonDateString);
		dt = new Date(dt.getTime() + MILLIS_IN_ONE_DAY);
		return formatJsonDate(dt);
	}

	public static Date addOneDayToDate(Date date) {
		return new Date(date.getTime() + MILLIS_IN_ONE_DAY);
	}

	public static Date subtractOneDayFromDate(Date date) {
		return new Date(date.getTime() - MILLIS_IN_ONE_DAY);
	}

	/**
	 * set the time portion of a Date to all 0's
	 */
	public static Date stripTime(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * return starting and ending dates within a day
	 */
	public static Date[] getStartAndEndDate(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date startDate = cal.getTime();

		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date endDate = cal.getTime();

		return new Date[] { startDate, endDate };
	}

	/**
	 * Calculates a date time offset from GMT.
	 * 
	 * the input GMT date.
	 * 
	 * @return the local date.
	 */
	public static Long calculateOffsetFromGMT(Date date) {
		Calendar cal = Calendar.getInstance();
		/*
		 * get the default time zone object and then calculate the signed offset from
		 * GMT.
		 */
		long offset = cal.getTimeZone().getOffset(date.getTime());
		return offset;
	}

	public static boolean dateRoundedEquals(Date dt1, Date dt2) {
		return dateRoundedEquals(dt1, dt2, Calendar.SECOND);
	}

	public static boolean dateRoundedEquals(Date dt1, Date dt2, int granularity) {
		if (dt1 != null && dt2 != null) {
			return DateUtils.truncatedEquals(dt1, dt2, granularity);
		} else {
			return dt1 == dt2;
		}
	}

	public static boolean dateRoundedGreater(Date dt1, Date dt2, int granularity) {
		if (dt1 != null && dt2 != null) {
			return DateUtils.truncatedCompareTo(dt1, dt2, granularity) > 0 ? true : false;
		} else {
			return false;
		}
	}

	public static boolean dateRoundedLess(Date dt1, Date dt2, int granularity) {
		if (dt1 != null && dt2 != null) {
			return DateUtils.truncatedCompareTo(dt1, dt2, granularity) < 0 ? true : false;
		} else {
			return false;
		}
	}

	public static Date parseJsonDate(final String dateString) throws ParseException {
		return parse(dateString, RuleConstants.UDR_DATE_FORMAT);
	}

	public static Date parseJsonDateTime(final String dateString) throws ParseException {
		return parse(dateString, RuleConstants.UDR_DATETIME_FORMAT);
	}

	public static Date parseJsonDateTimeUTCFromISO(final String dateString) throws ParseException {
		TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(dateString);
		Instant i = Instant.from(ta);
		Date d = Date.from(i);
		return d;
	}

	public static String formatJsonDate(final Date date) {
		return format(date, RuleConstants.UDR_DATE_FORMAT);
	}

	public static String formatJsonDateTime(final Date date) {
		// Display current time in 12 hour format with AM/PM
		if (date == null) {
			return null;
		}
		return format(date, "yyyy-MM-dd HH:mm");
	}

	public static Date parseRuleEngineDate(final String dateString) throws ParseException {
		return parse(dateString, RuleConstants.RULE_ENGINE_DATE_FORMAT);
	}

	public static String formatRuleEngineDate(final Date date) {
		return format(date, RuleConstants.RULE_ENGINE_DATE_FORMAT);
	}

	public static String formatRuleEngineDateTime(final Date date) {
		return format(date, RuleConstants.RULE_ENGINE_DATETIME_FORMAT);
	}

	/**
	 * Get the year value of the { @date } passed in
	 * 
	 * @param date
	 * @return
	 */
	public static int getYearOfDate(String date, DateTimeFormatter dateTimeFormatter) {
		LocalDate travLocalDate = parseLocalDate(date, dateTimeFormatter);
		return travLocalDate.getYear();
	}

	public static LocalDate parseLocalDate(String date, DateTimeFormatter dateTimeFormatter) {
		return LocalDate.parse(date, dateTimeFormatter);
	}

	/**
	 * Gets the month-of-year field
	 * 
	 * @param date
	 * @param dateTimeFormatter
	 * @return
	 */
	public static int getMonthOfDate(String date, DateTimeFormatter dateTimeFormatter) {
		LocalDate travLocalDate = parseLocalDate(date, dateTimeFormatter);
		return travLocalDate.getMonth().getValue();
	}

	/**
	 * Gets the day-of-month field.
	 * 
	 * @param date
	 * @param dateTimeFormatter
	 * @return
	 */
	public static int getDayOfDate(String date, DateTimeFormatter dateTimeFormatter) {
		LocalDate travLocalDate = parseLocalDate(date, dateTimeFormatter);
		return travLocalDate.getDayOfMonth();
	}

	public static String format(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	public static Date parse(String stringDate, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(stringDate);
	}

}