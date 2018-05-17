/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "flight_leg")
public class FlightLeg extends BaseEntity {
    private static final long serialVersionUID = 1L;  
    public FlightLeg() { }
    
    @ManyToOne
    private Flight flight;
    
    @ManyToOne
    private Pnr pnr;
    
    @ManyToOne
    private BookingDetail bookingDetail;

    @Column(name = "leg_number", nullable = false)
    private Integer legNumber;

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
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
}
