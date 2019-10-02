/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "flight_leg")
public class FlightLeg extends BaseEntity implements Comparable<FlightLeg> {
	private static final long serialVersionUID = 1L;

	public FlightLeg() {
	}

	@ManyToOne
	private Flight flight;

	@ManyToOne
	private Message message;

	@ManyToOne
	private BookingDetail bookingDetail;

	@Column(name = "leg_number", nullable = false)
	private Integer legNumber;

	public Flight getFlight() {
		return flight;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public Integer getLegNumber() {
		return legNumber;
	}

	public void setLegNumber(Integer legNumber) {
		this.legNumber = legNumber;
	}

	public BookingDetail getBookingDetail() {
		return bookingDetail;
	}

	public void setBookingDetail(BookingDetail bookingDetail) {
		this.bookingDetail = bookingDetail;
	}

	@Override
	public int compareTo(FlightLeg leg) {
		if (getLegNumber() == null || leg.getLegNumber() == null) {
			return 0;
		}
		return getLegNumber().compareTo(leg.getLegNumber());
	}
}
