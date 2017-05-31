/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bag")
public class Bag extends BaseEntity {
    private static final long serialVersionUID = 1L;

    public Bag() {
    }

    @Column(name = "bag_identification", nullable = false)
    private String bagId;

    @Column(name = "flight_id")
    private Long flightId;

    @Column(name = "passenger_id")
    private Long passengerId;

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }
}
