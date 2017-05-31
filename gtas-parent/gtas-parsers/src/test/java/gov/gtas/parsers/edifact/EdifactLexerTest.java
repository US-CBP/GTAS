/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import gov.gtas.parsers.edifact.segment.UNA;
import gov.gtas.parsers.exception.ParseException;

public class EdifactLexerTest {  
    /**
     * taken from https://en.wikipedia.org/wiki/EDIFACT
     */
    String payload = 
            "MSG+1:45'\r\n" + 
            "IFT+3+XYZCOMPANY AVAILABILITY'\r\n" + 
            "ERC+A7V:1:AMD'\r\n" + 
            "IFT+3+NO MORE FLIGHTS'\r\n" + 
            "ODI'\r\n" + 
            "TVL+240493:1000::1220+FRA+JFK+DL+400+C'\r\n" + 
            "PDI++C:3+Y::3+F::1'\r\n" + 
            "APD+74C:0:::6++++++6X'\r\n" + 
            "TVL+240493:1740::2030+JFK+MIA+DL+081+C'\r\n" + 
            "PDI++C:4'\r\n" + 
            "APD+EM2:0:1630::6+++++++DA'\r\n";

    String test = 
            "UNA:+.? '\r\n" + 
            "UNB+IATB:1+6XPPC+LHPPC+940101:0950+1'\r\n" + 
            "UNH+1+PAORES:93:1:IA'\r\n" + 
            payload + 
            "UNT+13+1'\r\n" + 
            "UNZ+1+1'";
    
    @Test
    public void testParse() throws ParseException {
        EdifactLexer lexer = new EdifactLexer(test);
        List<Segment> segments = lexer.tokenize();
        assertEquals(15, segments.size());
        for (Segment s : segments) {
            // random checks
            List<Composite> c = s.getComposites();
            switch(s.getName()) {
            case "UNH":
                assertEquals(2, c.size());
                assertEquals("1", c.get(0).getElement(0));
                assertEquals("PAORES", c.get(1).getElement(0));
                break;
            case "ODI":
                assertTrue(c.size() == 0);
                break;
            case "ERC":
                assertEquals(1, c.size());
                assertEquals("A7V", c.get(0).getElement(0));
                break;
            }
        }
    }

    @Test
    public void testGetStartOfSegment() {
        EdifactLexer lexer = new EdifactLexer(test);
        int index = lexer.getStartOfSegment("UNH");
        assertEquals(test.indexOf("UNH"), index);
        index = lexer.getStartOfSegment("ODI");
        assertEquals(test.indexOf("ODI"), index);
    }
    
    @Test
    public void testGetMessagePayload() {
        EdifactLexer lexer = new EdifactLexer(test);
        String payload = lexer.getMessagePayload("MSG", "UNT");
        assertEquals(payload, this.payload);
    }
    
    @Test
    public void testDefaultUna() {
        UNA una = new UNA();
        assertEquals(':', una.getComponentDataElementSeparator());
        assertEquals('+', una.getDataElementSeparator());
        assertEquals('.', una.getDecimalMark());
        assertEquals('?', una.getReleaseCharacter());
        assertEquals(' ', una.getRepetitionSeparator());
        assertEquals('\'', una.getSegmentTerminator());
    }
}
