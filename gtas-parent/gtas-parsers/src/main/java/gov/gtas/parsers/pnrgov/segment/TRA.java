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
 * TRA: Transport Identifier
 * <p>
 * Used when the operating airline is different than the marketing airline.
 * <p>
 * Examples: Flight number 123 operated by Delta(TRA+DL+123:Y”) Gr.5 portion of
 * the message TVL+121210:0915::1230+LHR+JFK+DL+324:B' TRA+KL+8734:B’ Operating
 * carrier information
 */
public class TRA extends Segment {
	private String airline;
	private String flightNumber;
	private String bookingDesignator;
	private String flightSuffix;

	public TRA(List<Composite> composites) {
		super(TRA.class.getSimpleName(), composites);

		Composite c = getComposite(0);
		if (c != null) {
			this.airline = c.getElement(0);
		}

		c = getComposite(1);
		if (c != null) {
			this.flightNumber = c.getElement(0);
			this.bookingDesignator = c.getElement(1);
			this.flightSuffix = c.getElement(2);
		}
	}

	public String getAirline() {
		return airline;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public String getBookingDesignator() {
		return bookingDesignator;
	}

	public String getFlightSuffix() {
		return flightSuffix;
	}
}
