/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class FlightUtilsTest {
    @Test
    public void testSeparateCarrierAndFlightNumberOkExamples() {
        String[] tests = {
                "UA0341",
                "UA123",
                "Z445",
                "3Z1",
                "3ZZ1",
                "XXXX"
        };
        
        String[][] expected = {
                {"UA", "0341"},
                {"UA", "123"},
                {"Z4", "45"},
                {"3Z", "1"},
                {"3ZZ", "1"},
                {"XXXX", ""}
        };
        
        int i = 0;
        for (String s : tests) {
            FlightNumber actual = FlightUtils.separateCarrierAndFlightNumber(s);
            assertEquals(expected[i][0], actual.getCarrier());
            assertEquals(expected[i][1], actual.getNumber());
            i++;
        }
    }

    @Test
    public void testDetermineFlightDate() {
        Date d = FlightUtils.determineFlightDate(null, null, null);
        assertNull(d);
        
        Date notNull = DateUtils.stripTime(new Date());
        d = FlightUtils.determineFlightDate(notNull, null, null);
        assertEquals(notNull, d);
        d = FlightUtils.determineFlightDate(null, notNull, null);
        assertEquals(notNull, d);
        d = FlightUtils.determineFlightDate(null, null, notNull);
        assertEquals(notNull, d);
    }
    
    @Test
    public void testpadFlightNumberWithZeroes() {
        String s = FlightUtils.padFlightNumberWithZeroes("1234");
        assertEquals("1234", s);
        s = FlightUtils.padFlightNumberWithZeroes("123");
        assertEquals("0123", s);
        s = FlightUtils.padFlightNumberWithZeroes("12");
        assertEquals("0012", s);
        s = FlightUtils.padFlightNumberWithZeroes("1");
        assertEquals("0001", s);
        s = FlightUtils.padFlightNumberWithZeroes("0");
        assertEquals("0000", s);
        s = FlightUtils.padFlightNumberWithZeroes("");
        assertEquals("0000", s);
    }
}
