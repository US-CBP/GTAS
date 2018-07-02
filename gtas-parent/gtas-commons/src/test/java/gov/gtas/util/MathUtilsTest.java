/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathUtilsTest {

	@Test
	public void testReverseUnitConversion() {
		double tolerance = 0.00001;
		//1 kilogram is equals to 2.2046226218 pounds
		assertEquals(1, MathUtils.poundsToKilos(2.2046226218),tolerance);
	}
	
	@Test
	public void testUnitConversion() {
		double tolerance = 0.00001;
		//1 Pound is equals to 0.453592 Kgs
		assertEquals(0.453592, MathUtils.poundsToKilos(1),tolerance);
	}
	
}
