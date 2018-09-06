/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicErrorDetailsTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicErrorDetailsTest.class);

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
        //logger.info(String.join("\n", det));
    }

}
