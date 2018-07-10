/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "flight_passenger",
uniqueConstraints={@UniqueConstraint(columnNames={"flight_id","passenger_id"})})
public class FlightPassenger {
	private static final long serialVersionUID = 1L;  
	
    @Id  
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Basic(optional = false)  
    @Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")  
    protected Long id;  
  
    public FlightPassenger() { }

    @Column(name = "flight_id", nullable = false)
    private String flightId;
    
    @Column(name = "passenger_id", nullable = false)
    private String passengerId;
    
    public String getPassengerId() {
		return passengerId;
    }

	public void setPassengerId(String passengerId) {
		this.passengerId = passengerId;
	}

	public String getFlightId() {
		return flightId;
	}

	public void setFlightId(String flightId) {
		this.flightId = flightId;
	}
	
    public Long getId() {  
        return id;  
    }

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Flight))
            return false;
        final FlightPassenger other = (FlightPassenger)obj;
        return Objects.equals(this.flightId, other.flightId)
                && Objects.equals(this.passengerId, other.passengerId);
    }
}
