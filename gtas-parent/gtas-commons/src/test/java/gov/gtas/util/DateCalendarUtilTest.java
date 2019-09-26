/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.util;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateCalendarUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddOneDay() throws Exception {
		String dtstr = "2014-12-31";
		assertEquals("2015-01-01", DateCalendarUtils.addOneDayToJsondateString(dtstr));
		dtstr = "2016-02-28";
		assertEquals("2016-02-29", DateCalendarUtils.addOneDayToJsondateString(dtstr));
		dtstr = "2015-02-28";
		assertEquals("2015-03-01", DateCalendarUtils.addOneDayToJsondateString(dtstr));
	}

	@Test
	public void testDateRoundedEquals() throws Exception {
		Date dt1 = DateCalendarUtils.parseJsonDate("2014-12-31");
		Date dt2 = new Date(dt1.getTime() + 3600000L);
		assertFalse("Did not expect Rounded Eqality at second granularity to succeeded",
				DateCalendarUtils.dateRoundedEquals(dt1, dt2));
		assertFalse("Did not expect Rounded Eqality at hour granularity to succeeded",
				DateCalendarUtils.dateRoundedEquals(dt1, dt2, Calendar.HOUR_OF_DAY));
		assertTrue("Rounded Eqality at Day granularity failed",
				DateCalendarUtils.dateRoundedEquals(dt1, dt2, Calendar.DAY_OF_MONTH));
	}

	@Test
	public void testDateRoundedLess() throws Exception {
		Date dt1 = DateCalendarUtils.parseJsonDate("2014-12-31");
		Date dt2 = new Date(dt1.getTime() + 3600000L);
		assertTrue("Did not expect Rounded less than at second granularity to fail",
				DateCalendarUtils.dateRoundedLess(dt1, dt2, Calendar.SECOND));
		assertTrue("Did not expect Rounded less than at hour granularity to fail",
				DateCalendarUtils.dateRoundedLess(dt1, dt2, Calendar.HOUR_OF_DAY));
		assertFalse("Did not expect Rounded less than at Day granularity to succeed",
				DateCalendarUtils.dateRoundedLess(dt1, dt2, Calendar.DAY_OF_MONTH));
	}

	@Test
	public void testDateRoundedGreater() throws Exception {
		Date dt1 = DateCalendarUtils.parseJsonDate("2014-12-31");
		Date dt2 = new Date(dt1.getTime() + 3600000L);
		assertTrue("Did not expect Rounded less than at second granularity to fail",
				DateCalendarUtils.dateRoundedGreater(dt2, dt1, Calendar.SECOND));
		assertTrue("Did not expect Rounded less than at hour granularity to fail",
				DateCalendarUtils.dateRoundedGreater(dt2, dt1, Calendar.HOUR_OF_DAY));
		assertFalse("Did not expect Rounded less than at Day granularity to succeed",
				DateCalendarUtils.dateRoundedGreater(dt2, dt1, Calendar.DAY_OF_MONTH));
	}
}
