package gov.gtas.parser.pentaho.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.gtas.parser.pentaho.util.ApisStringParser;

public class ApisStringParserTest {

	private static final String dateString1 = "140216";
	private static final String dateString2 = "220720";

	private static final String fullName = "FENLON:AMBER:MADDISON";
	private static final String fullName2 = "AMBER:MADDISON";

	private static final String string1 = "123";
	private static final String string2 = "12X";

	@BeforeClass
	public static void setup() {

	}

	@Test
	public void getNumberOfFlightLegsTest() {

		try {

			assertNotNull(ApisStringParser.getMd5Hash("XMLDFD"));
			assertNotNull(ApisStringParser.getMd5Hash("X"));
			assertNull(ApisStringParser.getMd5Hash(null));
			assertNull(ApisStringParser.getMd5Hash(" "));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void parseDateTimeAsStrTest() {
		try {

			assertEquals("2014-02-16", ApisStringParser.parseDateTimeAsStr(dateString1, "yyMMdd", "yyyy-MM-dd"));
			assertEquals("14-02-16", ApisStringParser.parseDateTimeAsStr(dateString1, "yyMMdd", "yy-MM-dd"));
			assertEquals("2014/02/16", ApisStringParser.parseDateTimeAsStr(dateString1, "yyMMdd", "yyyy/MM/dd"));
			assertEquals("20140216", ApisStringParser.parseDateTimeAsStr(dateString1, "yyMMdd", "yyyyMMdd"));
			assertEquals("07-20-2022", ApisStringParser.parseDateTimeAsStr(dateString2, "yyMMdd", "MM-dd-yyyy"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void getSubstrFistToDelimiterTest() {
		assertEquals("FENLON", ApisStringParser.getSubstrFistToDelimiter(fullName, ":"));
		assertEquals("AMBER", ApisStringParser.getSubstrFistToDelimiter(fullName2, ":"));
		assertNotEquals("MADDISON", ApisStringParser.getSubstrFistToDelimiter(fullName, ":"));
	}

	@Test
	public void isNumberTest() {
		assertTrue(ApisStringParser.isNumber(string1));
		assertFalse(ApisStringParser.isNumber(string2));
		assertFalse(ApisStringParser.isNumber(null));
	}

}
