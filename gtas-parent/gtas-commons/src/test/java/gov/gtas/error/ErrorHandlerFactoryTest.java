/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import static org.junit.Assert.*;

import java.util.Date;

import gov.gtas.constant.CommonErrorConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ErrorHandlerFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBasicHandlerIsDefault() {
        ErrorHandler errorHandler1 = ErrorHandlerFactory.getErrorHandler();
        ErrorHandler errorHandler2 = ErrorHandlerFactory.getErrorHandler();
        assertNotNull(errorHandler1);
        assertTrue(errorHandler1 instanceof BasicErrorHandler);
        assertTrue(errorHandler1 == errorHandler2);
    }

    @Test
    public void testUnknownError() {
        CommonServiceException ex = ErrorHandlerFactory.getErrorHandler().createException("xyz", "bar");
        assertNotNull(ex);
        assertEquals(CommonErrorConstants.UNKNOWN_ERROR_CODE,ex.getErrorCode());
    }

    @Test
    public void testRegisteredHandlerIsCalled() {
        ErrorHandlerFactory.registerErrorHandler(new TestHandler());
        CommonServiceException ex = ErrorHandlerFactory.getErrorHandler().createException("fooCode", "bar");
        assertNotNull(ex);
        assertEquals("fooCode",ex.getErrorCode());
        assertEquals("msg:bar",ex.getMessage());
        
        //test for exception processor
        ErrorDetailInfo det = ErrorHandlerFactory.getErrorHandler().processError(new TestException());
        assertTrue(det instanceof TestErrorDetails);
    }
    @Test
    public void processErrorTest(){
        ErrorDetailInfo det = ErrorHandlerFactory.getErrorHandler().processError(new NullPointerException());
        assertNotNull(det);
        assertNull(det.getErrorId());
        assertEquals(CommonErrorConstants.SYSTEM_ERROR_CODE, det.getErrorCode());
    }
    private static class TestHandler extends BasicErrorHandler{
        public TestHandler(){
            super.addErrorCodeToHandlerMap("fooCode", "msg:%s");
            super.addCustomErrorProcesssor(TestException.class,
                    (ex)->{return new TestErrorDetails(ex);}
                    );
        }
    }
    private static class TestErrorDetails extends BasicErrorDetailInfo{
        public TestErrorDetails(Exception ex){
            super(1L, CommonErrorConstants.SYSTEM_ERROR_CODE, new Date(), ex.getMessage(), null);
        }
    }
    private static class TestException extends Exception{
        private static final long serialVersionUID = 1L;        
    }
}

