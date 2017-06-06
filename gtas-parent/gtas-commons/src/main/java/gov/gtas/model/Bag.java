/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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
	@JoinColumn(nullable = false)
	private Flight flight;
	
    @Column(name = "weight", nullable = false)
	private float weight;

    public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
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
