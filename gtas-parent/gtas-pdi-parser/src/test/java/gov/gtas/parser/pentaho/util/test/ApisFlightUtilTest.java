package gov.gtas.parser.pentaho.util.test;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.gtas.parser.pentaho.util.ApisFlightUtil;

public class ApisFlightUtilTest {

	public static final String FLIGHT_1 = "AA371";
	public static final String FLIGHT_2 = "UA500";
	public static final String FLIGHT_3 = "DL121";

	@BeforeClass
	public static void setup() {

	}

	@Test
	public void getNumberOfFlightLegsTest() {

		assertEquals(Long.valueOf(0L), ApisFlightUtil.getNumberOfFlightLegs(null, null, null));
		assertEquals(Long.valueOf(1L), ApisFlightUtil.getNumberOfFlightLegs("MEX", null, null));
		assertEquals(Long.valueOf(2L), ApisFlightUtil.getNumberOfFlightLegs("MEX", "IAD", null));
		assertEquals(Long.valueOf(3L), ApisFlightUtil.getNumberOfFlightLegs("MEX", "IAD", "JFK"));
		assertNotEquals(Long.valueOf(1L), ApisFlightUtil.getNumberOfFlightLegs(null, null, null));
		assertNotEquals(Long.valueOf(2L), ApisFlightUtil.getNumberOfFlightLegs("MEX", null, null));
		assertNotEquals(Long.valueOf(3L), ApisFlightUtil.getNumberOfFlightLegs("MEX", "IAD", null));
		assertNotEquals(Long.valueOf(4L), ApisFlightUtil.getNumberOfFlightLegs("MEX", "IAD", "JFK"));
	}

	@Test
	public void getFlightNumber() {

		assertEquals("371", ApisFlightUtil.getFlightNumber(FLIGHT_1));
		assertEquals("500", ApisFlightUtil.getFlightNumber(FLIGHT_2));
		assertEquals("121", ApisFlightUtil.getFlightNumber(FLIGHT_3));
		assertNotEquals("311", ApisFlightUtil.getFlightNumber(FLIGHT_1));
		assertNotEquals("600", ApisFlightUtil.getFlightNumber(FLIGHT_2));
		assertNotEquals("421", ApisFlightUtil.getFlightNumber(FLIGHT_3));
	}

	@Test
	public void getCarrierCode() {

		assertEquals("AA", ApisFlightUtil.getCarrierCode(FLIGHT_1));
		assertEquals("UA", ApisFlightUtil.getCarrierCode(FLIGHT_2));
		assertEquals("DL", ApisFlightUtil.getCarrierCode(FLIGHT_3));
		assertNotEquals("ET", ApisFlightUtil.getCarrierCode(FLIGHT_1));
		assertNotEquals("QA", ApisFlightUtil.getCarrierCode(FLIGHT_2));
		assertNotEquals("TK", ApisFlightUtil.getCarrierCode(FLIGHT_3));
	}

}
