/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FlightNumber {
	private String carrier;
	private String number;

	public FlightNumber(String carrier, String number) {
		this.carrier = carrier;
		this.number = number;
	}

	public String getCarrier() {
		return carrier;
	}

	public String getNumber() {
		return number;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
