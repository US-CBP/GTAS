/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

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
    
}
