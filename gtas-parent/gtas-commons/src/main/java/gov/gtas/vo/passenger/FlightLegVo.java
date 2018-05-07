/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

public class FlightLegVo {
    
    private String flightReference;
    private String pnrReference;
    private String flightLeg;
    private String legNumber;
    private String flightNumber;
    private String originAirport;
    private String destinationAirport;
    private String flightDate;
    private String etd;
    private String eta;
    private String flightId;
    private String direction;
    private String bookingDetailId;
    
    
    
    public String getFlightId() {
		return flightId;
	}
	public void setFlightId(String flightId) {
		this.flightId = flightId;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getEta() {
		return eta;
	}
	public void setEta(String eta) {
		this.eta = eta;
	}
	public String getFlightReference() {
        return flightReference;
    }
    public void setFlightReference(String flightReference) {
        this.flightReference = flightReference;
    }
    public String getPnrReference() {
        return pnrReference;
    }
    public void setPnrReference(String pnrReference) {
        this.pnrReference = pnrReference;
    }
    public String getFlightLeg() {
        return flightLeg;
    }
    public void setFlightLeg(String flightLeg) {
        this.flightLeg = flightLeg;
    }
    public String getLegNumber() {
        return legNumber;
    }
    public void setLegNumber(String legNumber) {
        this.legNumber = legNumber;
    }
    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    public String getOriginAirport() {
        return originAirport;
    }
    public void setOriginAirport(String originAirport) {
        this.originAirport = originAirport;
    }
    public String getDestinationAirport() {
        return destinationAirport;
    }
    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }
    public String getFlightDate() {
        return flightDate;
    }
    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }
    public String getEtd() {
        return etd;
    }
    public void setEtd(String etd) {
        this.etd = etd;
    }
	public void setBookingDetailId(String bookingDetailId) {
		this.bookingDetailId = bookingDetailId;
	}
	public String getBookingDetailId() {
		return bookingDetailId;
	}
    

}
