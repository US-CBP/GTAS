/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.usedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;

public class LOC extends Segment {
	public enum LocCode {
		DEPARTURE, ARRIVAL, PLACE_OF_TRANSIT
	}

	private LocCode locationCode;
	private String iataCountryCode;
	private String iataAirportCode;
	private String c_codeListIdentifier;

	public LOC(List<Composite> composites) throws ParseException {
		super(LOC.class.getSimpleName(), composites);

		// location code
		Composite c = getComposite(0);
		String code = c.getElement(0);
		switch (code) {
		case "5":
		case "005":
			this.locationCode = LocCode.DEPARTURE;
			break;
		case "8":
		case "008":
			this.locationCode = LocCode.ARRIVAL;
			break;
		case "PT":
			// place of transit (for multi-leg itineraries)
			this.locationCode = LocCode.PLACE_OF_TRANSIT;
			break;
		default:
			throw new ParseException("LOC: unknown location code: " + code);
		}

		// Two-character Country code (IATA), followed by a 3-character Airport code
		// (IATA)
		c = getComposite(1);
		code = c.getElement(0);
		if (code != null) {
			this.iataCountryCode = code.substring(0, 2);
			this.iataAirportCode = code.substring(2, code.length());
		}
		this.c_codeListIdentifier = c.getElement(1);
	}

	public LocCode getLocationCode() {
		return locationCode;
	}

	public String getIataCountryCode() {
		return iataCountryCode;
	}

	public String getIataAirportCode() {
		return iataAirportCode;
	}

	public String getC_codeListIdentifier() {
		return c_codeListIdentifier;
	}
}
