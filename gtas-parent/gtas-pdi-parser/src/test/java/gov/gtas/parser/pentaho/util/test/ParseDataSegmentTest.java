package gov.gtas.parser.pentaho.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.gtas.parser.pentaho.util.ParseDataSegment;

public class ParseDataSegmentTest {

	private static final String DATA_ELEMENT_SEPARATOR = "+";
	private static final String COMP_DATA_ELEMENT_SEPARATOR = ":";
	private static final String LOCATION_SEGMENT_1 = "LOC+178+MEX";
	private static final String DOCUMENT_SEGMENT_1 = "DOC+P+340210565";
	private static final String DOCUMENT_SEGMENT_2 = "DOC+V+96690777";
	private static final String DATE_SEGMENT_1 = "DTM+36:230212";
	private static final String DATE_SEGMENT_2 = "DTM+36:020212";
	private static final String FLIGHT_DATE_SEGMENT_1 = "DTM+189:1802191015:201";
	private static final String FLIGHT_DATE_SEGMENT_2 = "DTM+189:1805230927:201";
	private static final String CONTACT_SEGMENT_1 = "COM+044 222 222222:TE";
	private static final String CONTACT_SEGMENT_2 = "COM+202 628 9292:TE+202 628 4998:FX+davidsonr.at.iata.org:EM";

	@Test
	public void extractFieldDataTest() {
		assertEquals("MEX", ParseDataSegment.extractFieldData(LOCATION_SEGMENT_1, DATA_ELEMENT_SEPARATOR));
		assertEquals("340210565", ParseDataSegment.extractFieldData(DOCUMENT_SEGMENT_1, DATA_ELEMENT_SEPARATOR));
		// Not Equals test
		assertNotEquals("USA", ParseDataSegment.extractFieldData(LOCATION_SEGMENT_1, DATA_ELEMENT_SEPARATOR));
		assertNotEquals("123456", ParseDataSegment.extractFieldData(DOCUMENT_SEGMENT_1, DATA_ELEMENT_SEPARATOR));
	}

	@Test
	public void extractDateFieldDataTest() {
		try {
			assertEquals("2023-02-12", ParseDataSegment.extractDateFieldData(DATE_SEGMENT_1,
					COMP_DATA_ELEMENT_SEPARATOR, "yyMMdd", "yyyy-MM-dd"));
			assertEquals("2002-02-12", ParseDataSegment.extractDateFieldData(DATE_SEGMENT_2,
					COMP_DATA_ELEMENT_SEPARATOR, "yyMMdd", "yyyy-MM-dd"));
			assertNotEquals("2023-01-12", ParseDataSegment.extractDateFieldData(DATE_SEGMENT_1,
					COMP_DATA_ELEMENT_SEPARATOR, "yyMMdd", "yyyy-MM-dd"));
			assertNotEquals("2022-02-12", ParseDataSegment.extractDateFieldData(DATE_SEGMENT_2,
					COMP_DATA_ELEMENT_SEPARATOR, "yyMMdd", "yyyy-MM-dd"));
			assertNull(
					ParseDataSegment.extractDateFieldData(null, COMP_DATA_ELEMENT_SEPARATOR, "yyMMdd", "yyyy-MM-dd"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void extractFlightDateFieldTest() {
		try {

			assertEquals("2018-02-19",
					ParseDataSegment.extractFlightDateField(FLIGHT_DATE_SEGMENT_1, COMP_DATA_ELEMENT_SEPARATOR));
			assertEquals("2018-05-23",
					ParseDataSegment.extractFlightDateField(FLIGHT_DATE_SEGMENT_2, COMP_DATA_ELEMENT_SEPARATOR));
			assertNotEquals("2018-01-19",
					ParseDataSegment.extractFlightDateField(FLIGHT_DATE_SEGMENT_1, COMP_DATA_ELEMENT_SEPARATOR));
			assertNotEquals("2018-05-22",
					ParseDataSegment.extractFlightDateField(FLIGHT_DATE_SEGMENT_2, COMP_DATA_ELEMENT_SEPARATOR));
			assertNull(ParseDataSegment.extractFlightDateField(null, COMP_DATA_ELEMENT_SEPARATOR));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void extractFlightDateTimeFieldTest() {
		try {
			assertEquals("2018-02-19 10:15:00",
					ParseDataSegment.extractFlightDateTimeField(FLIGHT_DATE_SEGMENT_1, COMP_DATA_ELEMENT_SEPARATOR));
			assertEquals("2018-05-23 09:27:00",
					ParseDataSegment.extractFlightDateTimeField(FLIGHT_DATE_SEGMENT_2, COMP_DATA_ELEMENT_SEPARATOR));
			assertNotEquals("2018-03-19 10:15:00",
					ParseDataSegment.extractFlightDateTimeField(FLIGHT_DATE_SEGMENT_1, COMP_DATA_ELEMENT_SEPARATOR));
			assertNotEquals("2018-05-23 09:25:00",
					ParseDataSegment.extractFlightDateTimeField(FLIGHT_DATE_SEGMENT_2, COMP_DATA_ELEMENT_SEPARATOR));
			assertNull(ParseDataSegment.extractFlightDateTimeField(null, COMP_DATA_ELEMENT_SEPARATOR));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void extractMiddleFieldTest() {
		try {
			assertEquals("P", ParseDataSegment.extractMiddleField(DOCUMENT_SEGMENT_1, DATA_ELEMENT_SEPARATOR));
			assertEquals("V", ParseDataSegment.extractMiddleField(DOCUMENT_SEGMENT_2, DATA_ELEMENT_SEPARATOR));
			assertNotEquals("V", ParseDataSegment.extractMiddleField(DOCUMENT_SEGMENT_1, DATA_ELEMENT_SEPARATOR));
			assertNotEquals("P", ParseDataSegment.extractMiddleField(DOCUMENT_SEGMENT_2, DATA_ELEMENT_SEPARATOR));
			assertNull(ParseDataSegment.extractMiddleField(null, DATA_ELEMENT_SEPARATOR));
			assertNull(ParseDataSegment.extractMiddleField(DOCUMENT_SEGMENT_1, COMP_DATA_ELEMENT_SEPARATOR));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void getContactInfoTest() {
		try {
			// Equals
			String[] result1 = ParseDataSegment.getContactInfo(CONTACT_SEGMENT_1, DATA_ELEMENT_SEPARATOR,
					COMP_DATA_ELEMENT_SEPARATOR);
			String[] result2 = ParseDataSegment.getContactInfo(CONTACT_SEGMENT_2, DATA_ELEMENT_SEPARATOR,
					COMP_DATA_ELEMENT_SEPARATOR);
			assertEquals("044 222 222222", result1[0]);
			assertNull(result1[1]);
			assertNull(result1[2]);
			assertEquals("202 628 9292", result2[0]);
			assertEquals("202 628 4998", result2[1]);
			assertEquals("davidsonr.at.iata.org", result2[2]);

			// null
			String[] result3 = ParseDataSegment.getContactInfo(null, DATA_ELEMENT_SEPARATOR,
					COMP_DATA_ELEMENT_SEPARATOR);
			assertNotNull(result3);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
