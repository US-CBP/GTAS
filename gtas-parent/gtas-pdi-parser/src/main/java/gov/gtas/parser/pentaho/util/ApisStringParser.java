package gov.gtas.parser.pentaho.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class implements general purpose string manipulation methods which are
 * used to assist parsing input data.
 *
 */
public class ApisStringParser {

	/**
	 * @param txt
	 *            Input string
	 * 
	 * @return An md5 hash of the input string
	 */
	public static String getMd5Hash(String txt) throws NoSuchAlgorithmException {

		String result = null;

		if (txt != null && !txt.trim().isEmpty()) {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(txt.getBytes(StandardCharsets.US_ASCII));
			StringBuilder sb = new StringBuilder();
			for (byte b : array) {
				sb.append(String.format("%02X", b));
			}

			if (sb.length() > 0) {
				result = sb.toString();
			}

		}
		return result;
	}

	/**
	 * @param dateValue
	 *            Date value as a String
	 * @param format
	 *            The format of the date string
	 * @return java.util.Date A date object formatted specified by the format
	 *         parameter
	 * 
	 *
	 */
	public static Date parseDateTime(String dateValue, String format) throws ParseException {

		DateFormat timeFormat = null;
		Date convertedDate = null;
		try {

			if (dateValue != null && dateValue.length() >= 6) {
				timeFormat = new SimpleDateFormat(format, Locale.ENGLISH);
				convertedDate = timeFormat.parse(dateValue);
			}

			return convertedDate;

		} catch (java.text.ParseException pe) {
			throw pe;
		}

	}

	/**
	 * @param dateValue
	 *            Date value as a String
	 * @param currentFormat
	 *            The format of the input date string
	 * @Param targetFormat The format of the desired output string
	 * @return java.lang.String A date value of string type which has a format
	 *         specified by targetFormat.
	 */

	public static String parseDateTimeAsStr(String dateValue, String currentFormat, String targetFormat)
			throws ParseException {
		try {

			SimpleDateFormat timeFormat = null;
			Date convertedDate = null;
			String newDateString = null;

			if (dateValue != null && dateValue.length() >= 6) {
				timeFormat = new SimpleDateFormat(currentFormat, Locale.ENGLISH);
				convertedDate = timeFormat.parse(dateValue);
				timeFormat.applyPattern(targetFormat);
				newDateString = timeFormat.format(convertedDate);
			}

			return newDateString;

		} catch (java.text.ParseException pe) {
			throw pe;
		}

	}

	/**
	 * @param str
	 *            The input string
	 * @param delimiter
	 *            The delimiter to be used
	 * @return java.lang.String The substring extracted from the first index up to
	 *         the delimiter
	 *
	 */

	public static String getSubstrFistToDelimiter(String str, String delimiter) {
		String substring = null;

		if (str != null && delimiter != null) {
			int index = str.indexOf(delimiter);

			if (index != -1) {
				substring = str.substring(0, index);
			}
		}

		return substring;
	}

	/**
	 * @param str
	 *            The input string
	 * @return boolean True if the string can be converted to a number, otherwise
	 *         false
	 *
	 */
	public static boolean isNumber(String str) {
		boolean digitString = true;

		if (str != null && !str.isEmpty()) {
			for (int i = 0; i < str.length(); i++) {
				if (!Character.isDigit(str.charAt(i))) {
					digitString = false;
					break;
				}

			}

		} else {
			digitString = false;
		}

		return digitString;
	}

}
