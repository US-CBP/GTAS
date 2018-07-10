/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.ApisMessageVo;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.PassengerVo;

public final class PaxlstParserUNedifactTest {
    EdifactParser<ApisMessageVo> parser; 
    
    String header = 
            "UNA:+.? '" + 
            "UNB+UNOA:4+APIS*ABE+USADHS+070429:0900+000000001++USADHS'" + 
            "UNH+PAX001+PAXLST:D:05B:UN:IATA'" +
            "BGM+745'";
    
    String trailer = 
            "CNT+3:2'" +
            "UNT+135+1'" + 
            "UNZ+1+020A07'";
    
    @Before
    public void setUp() {
        this.parser = new PaxlstParserUNedifact();
    }

    @Test
    public void testSingleTdtWithOneLeg() throws ParseException {
        String apis = header + 
                "TDT+20+UA123+++UA'" + 
                "LOC+125+YVR'" + 
                "DTM+189:0704291230:201'" + 
                "LOC+87+JFK'" + 
                "DTM+232:0704291600:201'" +
                trailer;

        ApisMessageVo vo = parser.parse(apis);
        List<FlightVo> flights = vo.getFlights();
        assertEquals(1, flights.size());
        FlightVo f = flights.get(0);
        assertEquals("0123", f.getFlightNumber());
        assertEquals("YVR", f.getOrigin());
        assertEquals("JFK", f.getDestination());
    }

    @Test
    public void testMultipleTdtWithOneLegEach() throws ParseException {
        String apis = header + 
                "TDT+20+UA123+++UA'" + 
                "LOC+125+YVR'" + 
                "DTM+189:0704291230:201'" + 
                "LOC+87+JFK'" + 
                "DTM+232:0704291600:201'" + 
                "TDT+20+UA124+++UA'" + 
                "LOC+92+JFK'" + 
                "DTM+189:0704291730:201'" + 
                "LOC+92+ATL'" + 
                "DTM+232:0704291945:201'" +
                trailer;

        ApisMessageVo vo = parser.parse(apis);
        List<FlightVo> flights = vo.getFlights();
        assertEquals(2, flights.size());
        FlightVo f1 = flights.get(0);
        assertEquals("0123", f1.getFlightNumber());
        assertEquals("YVR", f1.getOrigin());
        assertEquals("JFK", f1.getDestination());
        FlightVo f2 = flights.get(1);
        assertEquals("0124", f2.getFlightNumber());
        assertEquals("JFK", f2.getOrigin());
        assertEquals("ATL", f2.getDestination());
    }
    
    @Test
    public void testMultipleTdtWithLoc92() throws ParseException {
        String apis = header + 
                "TDT+20+KE250+++KE'" +
                "LOC+92+JFK'" +
                "DTM+189:1402010220:201'" +
                "LOC+92+ANC'" +
                "DTM+232:1402010550:201'" + 
                "TDT+20+KE250+++KE'" +
                "LOC+125+ANC'" +
                "DTM+189:1402010650:201'" +
                "LOC+87+ICN'" +
                "DTM+232:1402020840:201'" +
                trailer;
        
        ApisMessageVo vo = parser.parse(apis);
        List<FlightVo> flights = vo.getFlights();
        assertEquals(2, flights.size());
        
        FlightVo f1 = flights.get(0);
        assertEquals("0250", f1.getFlightNumber());
        assertEquals("JFK", f1.getOrigin());
        assertEquals("ANC", f1.getDestination());
        
        FlightVo f2 = flights.get(1);
        assertEquals("0250", f2.getFlightNumber());
        assertEquals("ANC", f2.getOrigin());
        assertEquals("ICN", f2.getDestination());
    } 

    /**
     * Should produce the exact same output as testMultipleTdtWithLoc92
     */
    @Test
    public void testSingleTdtWithLoc92() throws ParseException {
        String apis = header +
                "TDT+20+KE250+++KE'" +
                "LOC+92+JFK'" +
                "DTM+189:1402010220:201'" +
                "LOC+92+ANC'" +
                "DTM+232:1402010550:201'" + 
                "LOC+125+ANC'" +
                "DTM+189:1402010650:201'" +
                "LOC+87+ICN'" +
                "DTM+232:1402020840:201'" +
                trailer;
        
        ApisMessageVo vo = parser.parse(apis);
        List<FlightVo> flights = vo.getFlights();
        assertEquals(2, flights.size());
        
        FlightVo f1 = flights.get(0);
        assertEquals("0250", f1.getFlightNumber());
        assertEquals("JFK", f1.getOrigin());
        assertEquals("ANC", f1.getDestination());
        
        FlightVo f2 = flights.get(1);
        assertEquals("0250", f2.getFlightNumber());
        assertEquals("ANC", f2.getOrigin());
        assertEquals("ICN", f2.getDestination());        
    } 

    @Test
    public void testWithLoc130() throws ParseException {
        // TODO
    }
   
    @Test
    public void testRandomApis() throws ParseException {
        String apis = "UNA:+.?*' " + 
                "UNB+UNOA:4+AIR1+DHS+070218:1545+000006640++DHS' " + 
                "UNG+PAXLST+AIR1+ DHS+070218:1545+1+UN+D:05B' " + 
                "UNH+PAX001+PAXLST:D:05B:UN:IATA' " + 
                "BGM+745'" + 
                "RFF+TN:AJYTR1070219:::001'" + 
                "NAD+MS+++JOHN SMITH' " + 
                "COM+703-555-1212:TE+703-555-4545:FX' " + 
                "TDT+20+AA123+++AA'" + 
                "LOC+92+ATL'" + 
                "DTM+189:0702191540:201'" + 
                "LOC+92+ORD'" + 
                "DTM+232:0702191740:201'" + 
                "TDT+20+AA124+++AA'" + 
                "LOC+92+ORD'" + 
                "DTM+189:0702191840:201'" + 
                "LOC+92+JFK'" + 
                "DTM+232:0702191955:201'" + 
                "NAD+FL+++CLARK:MICHAEL' " + 
                "ATT+2++M' " + 
                "DTM+329:720907'" + 
                "LOC+178+ATL'" + 
                "LOC+179+JFK'" + 
                "RFF+AVF:TYR123'" + 
                "RFF+ABO:ABC123'" + 
                "RFF+AEA:1234567890ABC'" + 
                "RFF+CR:20060907NY123'" + 
                "NAD+FL+++CLARK:CHERYL' " + 
                "ATT+2++F'" + 
                "DTM+329:730407'" + 
                "LOC+178+ORD'" + 
                "LOC+179+JFK'" + 
                "RFF+AVF:TYR123'" + 
                "RFF+ABO:TYL009'" + 
                "CNT+42:2'" + 
                "UNT+33+PAX001' " + 
                "UNE+1+1' " + 
                "UNZ+1+000006640'";
        
        ApisMessageVo vo = parser.parse(apis);
        List<FlightVo> flights = vo.getFlights();
        assertEquals(2, flights.size());
        System.out.println(vo);
    }
    
    /**
     * Tests parsing bag information where both bag weight and count are included in the MEA segment of the APIS message
     * 
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testAPISParsingWithBagWeightAndCountInMEASegment() throws ParseException, IOException, URISyntaxException {
     
    	String message = header + 
    			"NAD+MS+++UNITED HELPDESK SIN'" + 
				"COM+044 222 222222:TE'" + 
				"TDT+20+UA988'" + 
				"LOC+125+FRA'" + 
				"DTM+189:1806261220:201'" + 
				"LOC+87+IAD'" + 
				"DTM+232:1806261505:201'" +
    			"NAD+FL+++FENNER:JACKI:ANJA'ATT+2++F'DTM+329:700602'MEA+CT++4'MEA+WT++KGM:80'FTX+BAG+++UA0443'FTX+BAG+++UA0444'FTX+BAG+++UA0445'FTX+BAG+++UA0446'LOC+178+WDH'LOC+179+FRA'LOC+174+USA'NAT+2+USA'RFF+AVF:9C7313'RFF+SEA:44G'DOC++448763368'DTM+36:220518'LOC+91+USA'DOC++'DTM+36:190919'LOC+91+'" + 
    			trailer;
    			
        ApisMessageVo vo = parser.parse(message);
    	
    	assertNotNull(vo);
    	
    	PassengerVo pvo = vo.getPassengers().get(0);
    	 
    	assertNotNull(pvo);
    
    	assertEquals("80", pvo.getTotalBagWeight());
    	assertEquals("4",pvo.getBagNum());
    }
    
    
    /**
     * Tests parsing bag information where only bag weight is included in the MEA segment of the APIS message
     * 
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testAPISParsingWithOnlyBagWeightInMEASegment() throws ParseException, IOException, URISyntaxException {
     
    	String message = header + 
    			"NAD+MS+++UNITED HELPDESK SIN'" + 
				"COM+044 222 222222:TE'" + 
				"TDT+20+UA988'" + 
				"LOC+125+FRA'" + 
				"DTM+189:1806261220:201'" + 
				"LOC+87+IAD'" + 
				"DTM+232:1806261505:201'" +
    			"NAD+FL+++FENNER:JACKI:ANJA'ATT+2++F'DTM+329:700602'MEA+WT++KGM:80'FTX+BAG+++UA0443'FTX+BAG+++UA0444'FTX+BAG+++UA0445'FTX+BAG+++UA0446'LOC+178+WDH'LOC+179+FRA'LOC+174+USA'NAT+2+USA'RFF+AVF:9C7313'RFF+SEA:44G'DOC++448763368'DTM+36:220518'LOC+91+USA'DOC++'DTM+36:190919'LOC+91+'" + 
    			trailer;
    			
        ApisMessageVo vo = parser.parse(message);
    	
    	assertNotNull(vo);
    	
    	PassengerVo pvo = vo.getPassengers().get(0);
    	 
    	assertNotNull(pvo);
    
    	assertEquals("80", pvo.getTotalBagWeight());
    	assertEquals(null,pvo.getBagNum());
    }
    
    /**
     * Tests parsing bag information where only bag count is included in the MEA segment of the APIS message 
     * 
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testAPISParsingWithOnlyBagCountInMEASegment() throws ParseException, IOException, URISyntaxException {
     
    	String message = header + 
    			"NAD+MS+++UNITED HELPDESK SIN'" + 
				"COM+044 222 222222:TE'" + 
				"TDT+20+UA988'" + 
				"LOC+125+FRA'" + 
				"DTM+189:1806261220:201'" + 
				"LOC+87+IAD'" + 
				"DTM+232:1806261505:201'" +
    			"NAD+FL+++FENNER:JACKI:ANJA'ATT+2++F'DTM+329:700602'MEA+CT++4'FTX+BAG+++UA0443'FTX+BAG+++UA0444'FTX+BAG+++UA0445'FTX+BAG+++UA0446'LOC+178+WDH'LOC+179+FRA'LOC+174+USA'NAT+2+USA'RFF+AVF:9C7313'RFF+SEA:44G'DOC++448763368'DTM+36:220518'LOC+91+USA'DOC++'DTM+36:190919'LOC+91+'" + 
    			trailer;
    			
        ApisMessageVo vo = parser.parse(message);
    	
    	assertNotNull(vo);
    	
    	PassengerVo pvo = vo.getPassengers().get(0);
    	 
    	assertNotNull(pvo);
    	assertEquals(null, pvo.getTotalBagWeight());
    	assertEquals("4",pvo.getBagNum());
    }
    
    /**
     * Tests parsing bag information where no MEA segment is included in the APIS message
     * 
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testAPISParsingWithNoMEASegment() throws ParseException, IOException, URISyntaxException {
     
    	String message = header + 
				"NAD+MS+++UNITED HELPDESK SIN'" + 
				"COM+044 222 222222:TE'" + 
				"TDT+20+UA988'" + 
				"LOC+125+FRA'" + 
				"DTM+189:1806261220:201'" + 
				"LOC+87+IAD'" + 
				"DTM+232:1806261505:201'" +
    			"NAD+FL+++FENNER:JACKI:ANJA'ATT+2++F'DTM+329:700602'FTX+BAG+++UA0443'FTX+BAG+++UA0444'FTX+BAG+++UA0445'FTX+BAG+++UA0446'LOC+178+WDH'LOC+179+FRA'LOC+174+USA'NAT+2+USA'RFF+AVF:9C7313'RFF+SEA:44G'DOC++448763368'DTM+36:220518'LOC+91+USA'DOC++'DTM+36:190919'LOC+91+'" + 
    			trailer;
    			
        ApisMessageVo vo = parser.parse(message);
    	
    	assertNotNull(vo);
    	
    	PassengerVo pvo = vo.getPassengers().get(0);
    	 
    	assertNotNull(pvo);
    	assertEquals("",pvo.getTotalBagWeight());
    	assertEquals("1",pvo.getBagNum());
    }
    
    /**
     * Tests parsing bag information where no MEA segment is included in the APIS message
     * 
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testAPISParsingWithNoMEASegmentAndTwoBagCountOnFTXSegment() throws ParseException, IOException, URISyntaxException {
     
    	String message = header + 
				"NAD+MS+++UNITED HELPDESK SIN'" + 
				"COM+044 222 222222:TE'" + 
				"TDT+20+UA988'" + 
				"LOC+125+FRA'" + 
				"DTM+189:1806261220:201'" + 
				"LOC+87+IAD'" + 
				"DTM+232:1806261505:201'" +
    			"NAD+FL+++FENNER:JACKI:ANJA'ATT+2++F'DTM+329:700602'FTX+BAG+++UA0443'FTX+BAG+++UA0444'FTX+BAG+++UA0445'FTX+BAG+++UA0446:2'LOC+178+WDH'LOC+179+FRA'LOC+174+USA'NAT+2+USA'RFF+AVF:9C7313'RFF+SEA:44G'DOC++448763368'DTM+36:220518'LOC+91+USA'DOC++'DTM+36:190919'LOC+91+'" + 
    			trailer;
    			
        ApisMessageVo vo = parser.parse(message);
    	
    	assertNotNull(vo);
    	
    	PassengerVo pvo = vo.getPassengers().get(0);
    	 
    	assertNotNull(pvo);
    	assertEquals("",pvo.getTotalBagWeight());
    	assertEquals("2",pvo.getBagNum());
    }
}
