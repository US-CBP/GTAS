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
 * SSD: SEAT SELECTION DETAILS
 * <p>
 * Cabin class is not standardized.
 * <p>
 * Ex:The passenger has been assigned seat 24A in coach.(SSD+24A++++Y’)
 */
public class SSD extends Segment {
	private String seatNumber;

	public SSD(List<Composite> composites) {
		super(SSD.class.getSimpleName(), composites);
		Composite c = getComposite(0);
		if (c != null) {
			this.seatNumber = c.getElement(0);
		}
	}

	public String getSeatNumber() {
		return seatNumber;
	}
}
