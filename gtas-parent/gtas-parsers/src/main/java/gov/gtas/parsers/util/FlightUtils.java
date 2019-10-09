/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public final class FlightUtils {
	public static final int MAX_FLIGHT_NUM_LENG = 4;
	public static final int MIN_CARRIER_LENG = 2;

	private FlightUtils() {
	}

	/**
	 * Separate flight carrier and flight number from single input string.
	 * 
	 * @param s
	 * @return
	 */
	public static FlightNumber separateCarrierAndFlightNumber(String s) {

		StringBuffer fn = new StringBuffer();
		int j;
		for (j = s.length() - 1; j >= 0; j--) {
			char c = s.charAt(j);
			if (Character.isDigit(c)) {
				fn.append(c);
				if (s.length() - fn.length() == MIN_CARRIER_LENG) {
					break;
				} else if (fn.length() == MAX_FLIGHT_NUM_LENG) {
					break;
				}
			} else {
				break;
			}
		}

		String carrier = s.substring(0, s.length() - fn.length());
		return new FlightNumber(carrier, fn.reverse().toString());
	}

	public static String padFlightNumberWithZeroes(String fn) {
		if (StringUtils.isBlank(fn)) {
			return null;
		}
		StringBuffer buff = new StringBuffer();
		for (int j = 0; j < MAX_FLIGHT_NUM_LENG - fn.length(); j++) {
			buff.append("0");
		}
		buff.append(fn);
		return buff.toString();
	}

}
