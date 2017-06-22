/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.watchlist.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import gov.gtas.enumtype.WatchlistEditEnum;
import gov.gtas.util.SampleDataGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WatchlistMappingTest {
    private static final String TEST_JSON =
        "{\"@class\": \"gov.gtas.model.watchlist.json.WatchlistSpec\","
       +" \"name\": \"PASSENGER_WL\","
       +" \"entity\": \"PASSENGER\","
       +"\"watchlistItems\": ["
        +"    {"
        +"        \"id\": null,"
        +"        \"action\": \"Create\","
        +"        \"terms\": ["
        +"            {  \"type\": \"string\","
          +"              \"field\": \"firstName\","
         +"               \"value\": \"John\" },"
         +"           { \"type\": \"string\","
         +"               \"field\": \"lastName\","
          +"              \"value\": \"Jones\" },"
         +"          {  \"type\": \"date\","
          +"              \"field\": \"dob\","
          +"              \"value\": \"1747-07-06\"  } ]},"

        +"    {"
        +"        \"id\": 29,"
        +"        \"action\": \"Delete\","
        +"        \"terms\": null},"

        +"  { "
        +"        \"id\": 32,"
        +"        \"action\": \"Update\","
        +"        \"terms\": [ {  \"type\": \"string\","
                        +"               \"field\": \"firstName\","
         +"               \"value\": \"The\" },"
         +"           {  \"type\": \"string\","
         +"               \"field\": \"lastName\","
         +"               \"value\": \"Donald\" },"
         +"           {  \"type\": \"date\","
          +"              \"field\": \"dob\","
         +"               \"value\": \"1957-04-01\" } ] } ] } ";       


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testUdrSpecToJson() {
        try{
        ObjectMapper mapper = new ObjectMapper();
        WatchlistSpec testObj = SampleDataGenerator.createSampleWatchlist("Passenger Watch List 1");
        
        //serialize
        String json=mapper.writeValueAsString(testObj);
        //de-serialize
        mapper.readValue(json, WatchlistSpec.class);
        
        } catch(Exception ex){
            ex.printStackTrace();
            fail("Got exception");
        }
    }
    @Test
    public void testJsonToUdrSpec() {
        try{
            ObjectMapper mapper = new ObjectMapper();
            //de-serialize
            WatchlistSpec testObj = mapper.readValue(TEST_JSON, WatchlistSpec.class);   
            assertNotNull(testObj);
            assertEquals("PASSENGER_WL", testObj.getName());
            assertEquals("PASSENGER", testObj.getEntity());
            List<WatchlistItemSpec> items = testObj.getWatchlistItems();
            assertNotNull(items);
            assertEquals(3, items.size());
            for(WatchlistItemSpec item:items){
                if(item.getAction().equalsIgnoreCase(WatchlistEditEnum.C.getOperationName())){
                    assertNull(item.getId());
                    WatchlistTerm[] terms = item.getTerms();
                    assertNotNull(terms);
                    assertEquals(3, terms.length);
                } else if(item.getAction().equalsIgnoreCase(WatchlistEditEnum.U.getOperationName())){
                    assertNotNull(item.getId());
                    WatchlistTerm[] terms = item.getTerms();
                    assertNotNull(terms);
                    assertEquals(3, terms.length);
                } else if(item.getAction().equalsIgnoreCase(WatchlistEditEnum.D.getOperationName())){
                    assertNotNull(item.getId());
                    WatchlistTerm[] terms = item.getTerms();
                    assertNull(terms);
                } else {
                        fail("Unexpected operation");
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
            fail("Got exception");
        }
    }
}
