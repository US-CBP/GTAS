/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.enums;

import org.junit.Test;
import org.springframework.util.Assert;

import gov.gtas.enumtype.TripTypeEnum;

public class enumTests {

	@Test
	public void tripTypeTest() {
		TripTypeEnum ttm = TripTypeEnum.MULTICITY;
		String mc = ttm.toString();
		Assert.isTrue(mc.equals("MULTI-CITY"), "Multi-city trip type text does not equal `MULTI-CITY`");

		TripTypeEnum ttr = TripTypeEnum.ROUNDTRIP;
		String rt = ttr.toString();
		Assert.isTrue(rt.equals("ROUND-TRIP"), "ROUNDTRIP trip type text does not equal `ROUND-TRIP`");

		TripTypeEnum tto = TripTypeEnum.ONEWAY;
		String ow = tto.toString();
		Assert.isTrue(ow.equals("ONE-WAY"), "ONEWAY trip type text does not equal `ONE-WAY`");

		TripTypeEnum ttj = TripTypeEnum.OPENJAW;
		String oj = ttj.toString();
		Assert.isTrue(oj.equals("OPEN JAW"), "OPENJAW trip type text does not equal `OPEN JAW`");

		TripTypeEnum ttn = TripTypeEnum.NONCONTIGUOUS;
		String nc = ttn.toString();
		Assert.isTrue(nc.equals("NON-CONTIGUOUS"), "NONCONTIGUOUS trip type text does not equal `NON-CONTIGUOUS`");
	}
}
