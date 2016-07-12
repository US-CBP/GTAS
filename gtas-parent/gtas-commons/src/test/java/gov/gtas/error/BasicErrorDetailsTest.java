/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.gtas.constant.CommonErrorConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BasicErrorDetailsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDetails() {
        Throwable cause = new NullPointerException();
        ErrorDetailInfo err = ErrorUtils.createErrorDetails(new Exception("Test Error", cause));
        assertEquals(CommonErrorConstants.SYSTEM_ERROR_CODE, err.getErrorCode());
        String[] det = err.getErrorDetails();
        assertNotNull(det);
        assertEquals("Exception class:Exception", det[0]);
        assertTrue(det[1].endsWith("Test Error"));
        assertTrue(det.length > 0);
        //System.out.println(String.join("\n", det));
    }

}
