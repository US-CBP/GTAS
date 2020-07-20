/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import java.util.List;

import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.edifact.segment.UNA;

public final class EdifactUtils {
	private EdifactUtils() {
	}

	/**
	 * Return the UNA segment for the given edifact message. If we can't find one,
	 * return the default UNA segment.
	 *
	 * @param msg
	 * @return
	 */
	public static UNA getUnaSegment(String msg) {
		String regex = String.format("UNA.{%d}\\s*UNB", UNA.NUM_UNA_CHARS);
		int unaIndex = TextUtils.indexOfRegex(regex, "UNA", msg);

		if (unaIndex != -1) {
			int endIndex = unaIndex + "UNA".length() + UNA.NUM_UNA_CHARS;
			String delims = msg.substring(unaIndex, endIndex);
			return new UNA(delims);
		}

		return new UNA();
	}

	/**
	 * Create a formatted string of segments, putting a carriage return at the end
	 * of each segment.
	 *
	 *
	 * @param unaText
	 * @param segments
	 * @return
	 */
	public static String prettyPrint(String unaText, List<Segment> segments) {
		StringBuffer buff = new StringBuffer();
		buff.append(unaText).append("\n");
		for (Segment s : segments) {
			buff.append(s.getText()).append("\n");
		}
		return buff.toString();
	}
}
