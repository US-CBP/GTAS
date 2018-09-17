/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import gov.gtas.model.udr.json.util.UdrSpecificationBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdrSpecificationMappingTest {
    private static final Logger logger = LoggerFactory.getLogger(UdrSpecificationMappingTest.class);
    private static final String TEST_JSON =
            "{ \"details\": {"
              +"  \"@class\": \"gov.gtas.model.udr.json.QueryObject\","
               +" \"condition\": \"OR\","
               +" \"rules\": ["
                 +  " {"
                   +"   \"@class\": \"QueryTerm\","
                        + " \"entity\": \"Pax\","
                        + " \"field\": \"firstName\","
                        + " \"operator\": \"EQUAL\","
                        + " \"value\": [\"John\"]"
//                        + " \"values\": null "
                   +" },"
                   + " {"
                        + " \"@class\": \"QueryTerm\","
                        + " \"entity\": \"Pax\","
                        + " \"field\": \"lastName\","
                        + " \"operator\": \"EQUAL\","
                        + " \"value\": [\"Jones\"]"
//                        + " \"values\": null "
                    +"}"
                +"]"
            +"},"
            + " \"summary\": {"
                + " \"title\": \"Hello Rule 1\","
                + " \"description\": \"This is a test\","
                + " \"startDate\": \"2015-06-24\","
                + " \"endDate\": null,"
                +" \"author\": \"jpjones\","
                + " \"enabled\": false"
            + "}"
        +"}";
        


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
        UdrSpecification testObj = UdrSpecificationBuilder.createSampleSpec();
        
        //serialize
        String json=mapper.writeValueAsString(testObj);
        //de-serialize
        mapper.readValue(json, UdrSpecification.class);
        
        } catch(Exception ex){
            logger.error("error!", ex);
            fail("Got exception");
        }
    }
    @Test
    public void testJsonToUdrSpec() {
        try{
            ObjectMapper mapper = new ObjectMapper();
            //de-serialize
            UdrSpecification testObj = mapper.readValue(TEST_JSON, UdrSpecification.class); 
            assertNotNull(testObj);
            assertEquals("Hello Rule 1", testObj.getSummary().getTitle());
        } catch(Exception ex){
            logger.error("error!", ex);
            fail("Got exception");
        }
    }
}
