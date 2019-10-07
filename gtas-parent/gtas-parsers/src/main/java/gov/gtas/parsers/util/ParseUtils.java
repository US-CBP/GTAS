/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.DocumentVo;

public final class ParseUtils {
	private static final Logger logger = LoggerFactory.getLogger(ParseUtils.class);

	private ParseUtils() {
	}

	/**
	 * Some telecommunications transmission protocols require various communication
	 * type headers and trailers to facilitate addressing, routing, security, and
	 * other purposes.
	 * 
	 * These headers and trailers are typically delimited by special control
	 * characters STX and ETX. This method removes the header and trailer from the
	 * message. See https://en.wikipedia.org/wiki/Control_characters
	 * 
	 * @param text
	 *            header
	 * @return message text without header or footer
	 */
	public static String stripStxEtxHeaderAndFooter(String text) {
		String rv = text;
		final int STX_CODEPOINT = 2;
		final int ETX_CODEPOINT = 3;

		int stxIndex = rv.indexOf(STX_CODEPOINT);
		if (stxIndex != -1) {
			rv = rv.substring(stxIndex + 1);
		}
		int etxIndex = rv.indexOf(ETX_CODEPOINT);
		if (etxIndex != -1) {
			rv = rv.substring(0, etxIndex);
		}

		return rv;
	}

	public static Date parseBirthday(String dt, String format) {
		try {
			DateFormat timeFormat = new SimpleDateFormat(format, Locale.ENGLISH);
			Date parsedDate = timeFormat.parse(dt);
			if (parsedDate.after(new Date())) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(parsedDate);
				cal.add(Calendar.YEAR, -100);
				parsedDate = cal.getTime();
			}
			return parsedDate;
		} catch (java.text.ParseException pe) {
			logger.warn(String.format(
					"Could not make a birthday from %s using format %s. Birthday needs a day month and year to be created.",
					dt, format));
		}

		return null;
	}

	public static Date parseDateTime(String dt, String format) {
		try {
			DateFormat timeFormat = new SimpleDateFormat(format, Locale.ENGLISH);
			return timeFormat.parse(dt);
		} catch (java.text.ParseException pe) {
			logger.warn(String.format("Could not parse date %s using format : %s", dt, format));
		}

		return null;
	}

	public static Date parseExpirationDateForCC(String dt, String format) {
		try {
			DateFormat timeFormat = new SimpleDateFormat(format, Locale.ENGLISH);
			Date parsedDate = timeFormat.parse(dt);
			Calendar c = Calendar.getInstance();
			c.setTime(parsedDate);
			int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
			c.add(Calendar.DATE, daysInMonth - 1);
			return c.getTime();
		} catch (java.text.ParseException pe) {
			logger.warn(String.format("Could not parse date %s using format: %s", dt, format));
		}

		return null;
	}

	public static String prepTelephoneNumber(String number) {
		if (StringUtils.isBlank(number)) {
			return null;
		} else {
			return number.replaceAll("[^0-9]", "");
		}
	}

	public static String prepIFTTelephoneNumber(String textContainingTelephoneNumber) {
		StringBuilder formatedPhoneNumber = new StringBuilder();
		if (StringUtils.isBlank(textContainingTelephoneNumber)) {
			return null;
		} else {
			String[] tokenizedSegments = textContainingTelephoneNumber
					.split("[^A-Z0-9]+|(?<=[A-Z])(?=[0-9])|(?<=[0-9])(?=[A-Z])");
			boolean startedProcessingNumber = false;
			for (String token : tokenizedSegments) {
				if (token.matches("[a-zA-Z]") && startedProcessingNumber) {
					break;
				} else if (token.matches("[0-9]+")) {
					startedProcessingNumber = true;
					formatedPhoneNumber.append(token);
				}
			}
		}

		return formatedPhoneNumber.toString();
	}

	/*
	 * private static String validateOrScrubPhoneNumber(String phoneNumber) {
	 * PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance(); //ZZ is
	 * unknown region. boolean canBeAPhoneNumber =
	 * phoneNumberUtil.isPossibleNumber(phoneNumber, "ZZ"); if (!canBeAPhoneNumber)
	 * { phoneNumber = ""; //We are ignoring impossible phone numbers } return
	 * phoneNumber; }
	 */

	public static Integer returnNumberOrNull(String s) {
		if (StringUtils.isBlank(s)) {
			return null;
		}

		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static boolean isValidDocument(DocumentVo d) {
		return (StringUtils.isNotEmpty(d.getDocumentNumber()) && StringUtils.isNotEmpty(d.getDocumentType()));
	}

	/**
	 * Parses a APIS date in yyMMdd format and adjusts the year to be the current or
	 * previous century
	 * 
	 * This overrides the SimpleDateFormat two-digit year parsing using
	 * set2DigitYearStart()
	 * 
	 * resolves issue #948
	 * 
	 * @param dt
	 *            date
	 * @param format
	 *            (formatted -> yyMMdd)
	 */
	public static Date parseAPISDOB(String dt, String format) {

		SimpleDateFormat timeFormat = new SimpleDateFormat(format, Locale.ENGLISH);
		try {
			if (!"yyMMdd".equalsIgnoreCase(format)) {
				return timeFormat.parse(dt);
			} else {
				return ParseUtils.parseBirthday(dt, format);
			}
		} catch (java.text.ParseException e) {
			logger.error("Failed to parse DOB (APIS)");
		}
		return null;
	}
}
