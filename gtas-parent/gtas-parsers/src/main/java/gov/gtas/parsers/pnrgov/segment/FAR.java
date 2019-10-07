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
 * <p>
 * FAR: FARE INFORMATION
 * <p>
 * Class FAR to hold Fare information for a passenger(s)
 */
public class FAR extends Segment {
	public FAR(List<Composite> composites) {
		super(FAR.class.getSimpleName(), composites);
	}
}
