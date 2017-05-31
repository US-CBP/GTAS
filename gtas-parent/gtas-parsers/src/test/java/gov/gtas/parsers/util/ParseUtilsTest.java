/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ParseUtilsTest {   
    @Test
    public void testPrepTelephoneNumber() {
        String num = ParseUtils.prepTelephoneNumber("+1-019-324- 1234");
        assertEquals("10193241234", num);
    }
    
    @Test
    public void testReturnNumberOrNull() {
        assertEquals(new Integer(33), ParseUtils.returnNumberOrNull("33"));
        assertEquals(new Integer(3), ParseUtils.returnNumberOrNull("3"));
        assertNull(ParseUtils.returnNumberOrNull("three"));
    }
}
