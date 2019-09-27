/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.Date;
import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;

/**
 * <p>
 * TVL: TRAVEL PRODUCT INFORMATION (LEVEL 0)
 * <p>
 * Specifies flight (departure date/time, origin, destination, operating airline
 * code, flight number, and operation suffix) for which passenger data is being
 * sent.
 * <p>
 * Dates and times in the TVL are in Local Time Departure and arrival points of
 * the transborder segment for a given country are the ones of the leg which
 * makes the segment eligible for push to a given country
 * <p>
 * Examples
 * <p>
 * The passenger information being sent is for Delta flight 10 from ATL to LGW
 * on 30MAR which departs at 5:00 pm.(TVL+300310:1700+ATL+DFW+DL+10’)
 * <p>
 * The passenger information being sent is for Delta flight 9375 from ATL to AMS
 * on 24 FEB which departs at 9:35 pm.(TVL+240210:2135+ATL+AMS+DL+9375’)
 */
public class TVL_L0 extends Segment {
	private Date etd;
	private Date eta;
	private String origin;
	private String destination;
	private String carrier;
	private String flightNumber;

	public TVL_L0(List<Composite> composites) throws ParseException {
		super("TVL", composites);
		for (int i = 0; i < numComposites(); i++) {
			Composite c = getComposite(i);
			switch (i) {
			case 0:
				Date[] tmp = TVL.getEtdEta(c);
				etd = tmp[0];
				eta = tmp[1];
				break;
			case 1:
				origin = c.getElement(0);
				break;
			case 2:
				destination = c.getElement(0);
				break;
			case 3:
				carrier = c.getElement(0);
				break;
			case 4:
				flightNumber = c.getElement(0);
				break;
			}
		}
	}

	public Date getEtd() {
		return etd;
	}

	public Date getEta() {
		return eta;
	}

	public String getOrigin() {
		return origin;
	}

	public String getDestination() {
		return destination;
	}

	public String getCarrier() {
		return carrier;
	}

	public String getFlightNumber() {
		return flightNumber;
	}
}
