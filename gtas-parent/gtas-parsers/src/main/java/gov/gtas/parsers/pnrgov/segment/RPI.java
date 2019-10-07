/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * Class RPI to hold Related product information To indicate quantity and action
 * required in relation to a product.
 *
 * Example:Flight booking status is holds confirmed for 3 passengers(RPI+3+HK')
 */
public class RPI extends Segment {
	public RPI(List<Composite> composites) {
		super(RPI.class.getSimpleName(), composites);
	}
}
