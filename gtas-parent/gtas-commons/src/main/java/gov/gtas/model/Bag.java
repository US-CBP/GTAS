/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bag")
public class Bag extends BaseEntity {
    private static final long serialVersionUID = 1L;

    public Bag() {
    }

    @Column(name = "bag_identification", nullable = false)
    private String bagId;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Passenger passenger;

	@ManyToOne
	@JoinColumn(name = "flight_id",referencedColumnName = "id", nullable = false)
	private Flight flight;
	
    @Column(name = "data_source")
    private String data_source;
    
    private String destination;

    @Column(name = "destination_airport")
	private String destinationAirport;
    
    @Column(name = "airline")
	private String airline;
    
    @Column(name = "headpool")
	private boolean headPool=false;

    
	public boolean isHeadPool() {
		return headPool;
	}

	public void setHeadPool(boolean headPool) {
		this.headPool = headPool;
	}

	public String getDestinationAirport() {
		return destinationAirport;
	}

	public void setDestinationAirport(String destinationAirport) {
		this.destinationAirport = destinationAirport;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getData_source() {
		return data_source;
	}

	public void setData_source(String data_source) {
		this.data_source = data_source;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }
}
