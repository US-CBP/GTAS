/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * GID GOODS ITEM DETAILS
 * <p>
 * Function: To indicate totals of a goods item.
 */
public class GID extends Segment {
	public GID(List<Composite> composites) {
		super(GID.class.getSimpleName(), composites);
	}
}
