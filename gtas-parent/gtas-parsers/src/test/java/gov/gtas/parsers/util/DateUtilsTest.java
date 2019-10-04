/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import gov.gtas.parsers.exception.ParseException;

public class DateUtilsTest {

	@Test
	public void testCalculateAge() throws ParseException {
		int year = 1980;
		Date d = ParseUtils.parseDateTime("0101" + year, "MMddyyyy");
		int age = DateUtils.calculateAge(d);
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		assertEquals(thisYear - year, age);
	}

	@Test
	public void testDOBDateFormatYYMMDD() throws ParseException {
		Map<Integer, String> dates = new HashMap<Integer, String>();

		dates.put(1939, "391001");
		dates.put(2011, "111001");
		dates.put(1959, "591001");
		dates.put(1938, "381001");
		dates.put(2019, "191001");
		dates.put(2018, "181001");
		dates.put(1940, "401001");
		dates.put(2012, "120101");
		dates.put(1920, "200101");
		dates.put(1983, "830101");
		String DATE_FORMAT = "yyMMdd";

		SimpleDateFormat yyyy_fmt = new SimpleDateFormat("yyyy");

		dates.keySet().forEach(key -> {
			Date parsedDate = ParseUtils.parseAPISDOB(dates.get(key), DATE_FORMAT);
			assertEquals(key.intValue(), Integer.parseInt(yyyy_fmt.format(parsedDate)));
		});

	}
}
