/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

public class TextUtilsTest {
    @Test
    public void testSplit() {
        List<String> segs = TextUtils.splitWithEscapeChar("mc?'foo'bar", '\'', '?');
        assertEquals("mc'foo", segs.get(0));
        assertEquals("bar", segs.get(1));
    }
    
    @Test
    public void testSplitHappyPath() {
        String segmentText = "DTM*36:10109$LOC*31*USA$NAD*FL***ANDREWS:TIFFANY:PAGE$ATT*2**F$";
        List<String> segs = TextUtils.splitWithEscapeChar(segmentText, '$', '?');
        assertEquals(4, segs.size());
        assertEquals("DTM*36:10109", segs.get(0));
        assertEquals("LOC*31*USA", segs.get(1));
        assertEquals("NAD*FL***ANDREWS:TIFFANY:PAGE", segs.get(2));
        assertEquals("ATT*2**F", segs.get(3));
    }

    @Test
    public void testSplitWithEscaped() {
        String segmentText = "DTM*36:10109'LOC*31*USA'NAD*FL***MC?'ANDREWS:TIFFANY:PAGE'ATT*2**F'";
        List<String> segs = TextUtils.splitWithEscapeChar(segmentText, '\'', '?');
        assertEquals(4, segs.size());
        assertEquals("NAD*FL***MC'ANDREWS:TIFFANY:PAGE", segs.get(2));
    }
    
    @Test
    public void testEscapedEscaped() {
        String segmentText = "DTM*36:10109'LOC*31*USA'NAD*FL***MC?'ANDREWS?:TIFF??ANY:PAGE'ATT*2**F'";
        List<String> segs = TextUtils.splitWithEscapeChar(segmentText, '\'', '?');
        assertEquals(4, segs.size());
        assertEquals("NAD*FL***MC'ANDREWS?:TIFF??ANY:PAGE", segs.get(2));
    }

    @Test
    public void testSplitSegmentsWithExtraneousWhitespace() {
        String segmentText = "DTM*36:10109  $   LOC*31*USA  $NAD*FL***ANDREWS:TIFFANY:PAGE\r\n $\n\n\n\n ATT*2**F$";
        List<String> segs = TextUtils.splitWithEscapeChar(segmentText, '$', '?');
        assertEquals(4, segs.size());
        assertEquals("DTM*36:10109", segs.get(0));
        assertEquals("LOC*31*USA", segs.get(1));
        assertEquals("NAD*FL***ANDREWS:TIFFANY:PAGE", segs.get(2));
        assertEquals("ATT*2**F", segs.get(3));
    }

    @Test
    public void testSplitElementsWithExtraneousWhitespace() {
        String segmentText = " ANDREWS:  \r\n  TIFFANY : PAGE ";
        List<String> elements = TextUtils.splitWithEscapeChar(segmentText, ':', '?');
        assertEquals("ANDREWS", elements.get(0));
        assertEquals("TIFFANY", elements.get(1));
        assertEquals("PAGE", elements.get(2));
    }
        
    @Test
    public void testMd5() {
        String str1 = "gtas";
        String expected = "2F005A6B1EA39FAA5D75C6CCCF59E63A";
        String actual = TextUtils.getMd5Hash(str1, StandardCharsets.US_ASCII);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testConvertToSingleLine() {
        String input = "   hello    \r\n\r\n  there\r   gtas team\n   ";
        String actual = TextUtils.convertToSingleLine(input);
        assertEquals("hellotheregtas team", actual);
    }
    
    @Test
    public void testConvertToSingleLineWhitespace() {
        assertEquals("", TextUtils.convertToSingleLine("      "));
    }
    
    @Test
    public void testIndexOfRegex() {
        String input = "aaaABCbbb";
        String regex = "[A-Z]+";
        int i = TextUtils.indexOfRegex(regex, input);
        assertEquals(input.indexOf('A'), i);
    }
}
