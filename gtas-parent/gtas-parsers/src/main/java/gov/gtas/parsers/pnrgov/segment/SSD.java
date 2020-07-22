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
	private String cabinClass;

	private static final int SEAT_NUMBER_COMPOSITE = 0;
	private static final int SEAT_NUMBER_COMPONENT = 0;
	private static final int CABIN_CLASS_COMPOSITE = 4;
	private static final int CABIN_CLASS_COMPONENT = 0;
	public SSD(List<Composite> composites) {
		super(SSD.class.getSimpleName(), composites);
		Composite c = getComposite(SEAT_NUMBER_COMPOSITE);
		if (c != null) {
			this.seatNumber = c.getElement(SEAT_NUMBER_COMPONENT);
		}
		c = getComposite(CABIN_CLASS_COMPOSITE);
		if (c != null) {
			this.cabinClass = c.getElement(CABIN_CLASS_COMPONENT);
		}
	}

	public String getSeatNumber() {
		return seatNumber;
	}
	public String getCabinClass() {
		return cabinClass;
	}
}
