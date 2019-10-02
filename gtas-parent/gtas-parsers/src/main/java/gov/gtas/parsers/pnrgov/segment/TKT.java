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
 * TKT: TICKET NUMBER DETAILS
 * <p>
 * Ex:
 * <ul>
 * <li>The ticket number for a passenger(TKT+0062230534212:T')
 * <li>Conjunctive ticket – 2 booklets(TKT+0271420067693:T:2')
 * <li>A Ticketless passenger(TKT+:1’)
 * </ul>
 */
public class TKT extends Segment {
	private static final String TICKETLESS_CODE = "1";
	private String ticketNumber;
	private String ticketType;
	private String numberOfBooklets;
	private boolean ticketless;

	public TKT(List<Composite> composites) {
		super(TKT.class.getSimpleName(), composites);
		Composite c = getComposite(0);
		if (c != null) {
			this.ticketNumber = c.getElement(0);
			this.ticketType = c.getElement(1);
			this.ticketless = TICKETLESS_CODE.equals(this.ticketType);
			this.numberOfBooklets = c.getElement(2);
		}
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public String getTicketType() {
		return ticketType;
	}

	public String getNumberOfBooklets() {
		return numberOfBooklets;
	}

	public boolean isTicketless() {
		return ticketless;
	}
}
