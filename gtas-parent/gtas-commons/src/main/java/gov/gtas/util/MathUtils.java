/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.util;

public class MathUtils {

	private static final double factor = 1 / 2.2046226218;

	public static double poundsToKilos(double pounds) {
		return factor * pounds;
	}
}