/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP)
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.pnrgov.PnrUtils;

/**
 * <p>
 * TVL: TRAVEL PRODUCT INFORMATION (Gr5 at Level 2 and Gr.12 at Level 4)
 * <p>
 * Specifies flight (departure date/time, origin, destination, operating airline
 * code, flight number, and operation suffix) for which passenger data is being
 * sent.
 * <p>
 * Dates and times in the TVL are in Local Time Departure and arrival points of
 * the transborder segment for a given country are the ones of the leg which
 * makes the segment eligible for push to a given country
 * <p>
 * For OPEN and ARNK segments, the date, place of departure and place of arrival
 * are conditional. For an Airline/ Flight Number / class/ date / segment, the
 * date, place of departure and place of arrival are mandatory.
 * <p>
 * When referring to a codeshare flight, two TVLs are required (one as difined
 * in 5.28.2 for the marketing flight and one providing the operating flight
 * information as defined in 5.28.3). If the marketing and operating
 * carrier/flight are the same, only one TVL is used as defined in 5.28.2.
 * <p>
 * Flown segments are to be included in history. Departure and arrival
 * city/airport codes as contained in the passenger's booked itinerary.
 * 
 * This example contains an illustration of both the operating and the marketing
 * TVLs for a codeshare situation where the marketing carrier is DL and the
 * operating carrier is KL.. TVL+010410:2235: 020410:1200+ATL+AMS+DL:KL+9362:K
 * TVL+++++972:M
 */
public class TVL extends Segment {
	private Date etd;
	private Date eta;
	private String origin;
	private String destination;
	private String carrier;
	private String operatingCarrier;
	private String flightNumber;
	private String reservationBookingDesignator;

	public TVL(List<Composite> composites) throws ParseException {
		super(TVL.class.getSimpleName(), composites);
		for (int i = 0; i < numComposites(); i++) {
			Composite c = getComposite(i);

			switch (i) {
			case 0:
				Date[] tmp = getEtdEta(c);
				etd = tmp[0];
				eta = tmp[1];
				break;
			case 1:
				this.origin = c.getElement(0);
				break;
			case 2:
				this.destination = c.getElement(0);
				break;
			case 3:
				this.carrier = c.getElement(0);
				this.operatingCarrier = c.getElement(1);
				break;
			case 4:
				this.flightNumber = c.getElement(0);
				this.reservationBookingDesignator = c.getElement(1);
			}
		}
	}

	/**
	 * @return [etd, eta]
	 */
	public static Date[] getEtdEta(Composite c) throws ParseException {
		Date[] rv = new Date[2];

		String departureDate = c.getElement(0);
		String departureTime = c.getElement(1);
		if (departureTime != null) {
			departureDate += departureTime;
		}

		String arrivalDate = c.getElement(2);
		String arrivalTime = c.getElement(3);
		if (arrivalTime != null) {
			// As a shorthand some TVL segments leave out the
			// arrival date if it's the same as the departure date
			// e.g. TVL+121210:0915::1230
			if (StringUtils.isBlank(arrivalDate)) {
				arrivalDate = c.getElement(0);
			}
			arrivalDate += arrivalTime;
		}

		if (departureDate != null) {
			rv[0] = PnrUtils.parseDateTime(departureDate);
		}
		if (arrivalDate != null) {
			rv[1] = PnrUtils.parseDateTime(arrivalDate);
		}

		return rv;
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

	public String getOperatingCarrier() {
		return operatingCarrier;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public String getReservationBookingDesignator() {
		return reservationBookingDesignator;
	}
}
