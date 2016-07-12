/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.svc.util.TargetingServiceUtils;
import gov.gtas.testdatagen.ApisDataGenerator;
import gov.gtas.testdatagen.PnrDataGenerator;

public class TargetingServiceUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 1pnr+1flt+4pass+4doc+4seat+2addr+1email+1phone+1ff+1cc+1agency + (4pass+ 7)links = 32 (pnr1)
     *   * 1pnr+1flt+2pass+2doc+2seat+1addr+1email+1phone+1ff+1cc+1agency + (2pass+ 6)links = 22  (pnr2)
 
     */
    @Test
    public void testPnrRuleRequestCreation() {
        Pnr msg = PnrDataGenerator.createTestPnr(1L);
        RuleServiceRequest request = TargetingServiceUtils.createPnrRequestContext(msg).getRuleServiceRequest();
        Collection<?> reqObjects = request.getRequestObjects();
        assertNotNull(reqObjects);
        assertEquals(32, reqObjects.size());

        msg = PnrDataGenerator.createTestPnr2(1L);
        request = TargetingServiceUtils.createPnrRequestContext(msg).getRuleServiceRequest();
        reqObjects = request.getRequestObjects();
        assertNotNull(reqObjects);
        assertEquals(22, reqObjects.size());
    }

    @Test
    public void testApisRuleRequestCreation() {
        ApisMessage message = ApisDataGenerator.createSimpleTestApisMesssage();
        assertNotNull(message);
        RuleServiceRequest request = TargetingServiceUtils
                        .createApisRequest(message).getRuleServiceRequest();
        Collection<?> reqObjects = request.getRequestObjects();
        assertNotNull(reqObjects);      
        assertEquals(2, reqObjects.size());//2 flights with 3 passengers each. Each passenger has 1 doc + 1 passenger has a seat
    //  assertEquals(15, reqObjects.size());//2 flights with 3 passengers each. Each passenger has 1 doc + 1 passenger has a seat   
    }

    @Test
    public void testApisPnrRuleRequestCreation() {
        ApisMessage apis = ApisDataGenerator.createSimpleTestApisMesssage();
        Pnr pnr = PnrDataGenerator.createTestPnr(1L);
        RuleServiceRequest request = TargetingServiceUtils
                        .createPnrApisRequestContext(Arrays.asList(apis), Arrays.asList(pnr)).getRuleServiceRequest();
        Collection<?> reqObjects = request.getRequestObjects();
        assertNotNull(reqObjects);      
        assertEquals(34, reqObjects.size());//32 PNR + 15 APIS  
        //assertEquals(47, reqObjects.size());//32 PNR + 15 APIS
    }
    
//  @Test
//  public void testApisPnrRuleRequestCreation2() {
//      /*
//       * PNR and APIS has 2 common flights and 3 passengers
//       */
//      ApisMessage apisMsg = ApisDataGenerator.createSimpleTestApisMesssage();
//      Pnr pnr = PnrDataGenerator.createTestPnr(1L);
//      
//      //BEGIN:create common flights and passengers
//      Collection<Flight> apisFlights = apisMsg.getFlights();
//      Collection<Passenger> apisFlt1Passengers = apisFlights.iterator().next().getPassengers();
//      for(Flight fl:apisFlights){
//          pnr.getFlights().add(fl);
//      }
//      //add 3 passengers
//      for(Passenger p:apisFlt1Passengers){
//          pnr.getPassengers().add(p);
//      }
//      //END:create common flights and passengers
//      
//      RuleServiceRequest request = TargetingServiceUtils
//                      .createPnrApisRequestContext(Arrays.asList(apisMsg), Arrays.asList(pnr)).getRuleServiceRequest();
//      Collection<?> reqObjects = request.getRequestObjects();
//      assertNotNull(reqObjects);      
//      assertEquals(44, reqObjects.size());//32 PNR + 15 APIS + 3 common passenger links   
//      //assertEquals(50, reqObjects.size());//32 PNR + 15 APIS + 3 common passenger links 
//  }
}
