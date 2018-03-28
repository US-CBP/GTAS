package gov.gtas.parser.pentaho.util.test;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import gov.gtas.parser.pentaho.bean.UNA;
import gov.gtas.parser.pentaho.util.UNAParser;

public class UNAParserTest {

	private static final String SERVICE_STRING_ADVICE_1 = "UNA:+.?*'";
	private static final String SERVICE_STRING_ADVICE_2 = "UNA:+.?";
	private static final String SERVICE_STRING_ADVICE_3 = "UNA";
	private static UNA una;

	@BeforeClass
	public static void setup() {
		una = new UNA();
		una.setServiceStringAdvice(":+.?*'");
		una.setComponentDataElementSep(":");
		una.setDataElementSep("+");
		una.setDecimalMark(".");
		una.setReleaseCharacter("?");
		una.setRepetitionSep("*");
		una.setSegmentTerminator("'");

	}

	@Test
	public void getServiceStringTest() {
		assertEquals(":+.?*'", UNAParser.getServiceString(SERVICE_STRING_ADVICE_1));
		assertEquals(":+.?*'", UNAParser.getServiceString(SERVICE_STRING_ADVICE_2));
		assertEquals(":+.?*'", UNAParser.getServiceString(SERVICE_STRING_ADVICE_3));
		assertEquals(":+.?*'", UNAParser.getServiceString(null));
		assertEquals(":+.?*'", UNAParser.getServiceString(" "));
	}

	@Test
	public void getUNATest() {
		assertEquals(":+.?*'", UNAParser.getUNA(SERVICE_STRING_ADVICE_1).getServiceStringAdvice());
		assertEquals(":", UNAParser.getUNA(SERVICE_STRING_ADVICE_1).getComponentDataElementSep());
		assertEquals("+", UNAParser.getUNA(SERVICE_STRING_ADVICE_1).getDataElementSep());
		assertEquals(".", UNAParser.getUNA(SERVICE_STRING_ADVICE_1).getDecimalMark());
		assertEquals("?", UNAParser.getUNA(SERVICE_STRING_ADVICE_1).getReleaseCharacter());
		assertEquals("*", UNAParser.getUNA(SERVICE_STRING_ADVICE_1).getRepetitionSep());
		assertEquals("'", UNAParser.getUNA(SERVICE_STRING_ADVICE_1).getSegmentTerminator());
		assertTrue(una.equals(UNAParser.getUNA(SERVICE_STRING_ADVICE_1)));
		assertEquals(una, UNAParser.getUNA(null));
	}

}
