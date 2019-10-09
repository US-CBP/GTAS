/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.gtas.parsers.edifact.segment.UNA;
import gov.gtas.parsers.exception.ParseException;

public class SegmentTokenizerTest {
	SegmentTokenizer tokenizer;

	@Before
	public void setUp() throws Exception {
		UNA una = new UNA();
		this.tokenizer = new SegmentTokenizer(una);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHappyPath() throws ParseException {
		String text = "NAD+FL+++PAGE:TIFFANY:ANNE";
		Segment s = tokenizer.buildSegment(text);
		List<Composite> composites = s.getComposites();
		assertEquals(4, composites.size());
		assertEquals("NAD", s.getName());
		assertEquals("FL", composites.get(0).getElement(0));
		assertEquals("", composites.get(1).getElement(0));
		assertEquals("", composites.get(2).getElement(0));
		assertEquals(3, composites.get(3).numElements());
		assertEquals("PAGE", composites.get(3).getElement(0));
		assertEquals("TIFFANY", composites.get(3).getElement(1));
		assertEquals("ANNE", composites.get(3).getElement(2));
	}

	@Test
	public void testSegmentNameOnly() throws ParseException {
		Segment s = tokenizer.buildSegment("NAD");
		List<Composite> composites = s.getComposites();
		assertEquals(0, composites.size());
		assertEquals("NAD", s.getName());
	}

	@Test
	public void testEscapedDelimiters() throws ParseException {
		String seg = "NAD+FL?+MC?:MD+++PAGE:TIFFANY:ANNE";
		Segment s = tokenizer.buildSegment(seg);
		List<Composite> composites = s.getComposites();
		assertEquals(4, composites.size());
		assertEquals("FL+MC:MD", composites.get(0).getElement(0));
	}
}
