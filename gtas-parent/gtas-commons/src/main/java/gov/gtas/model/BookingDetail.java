package gov.gtas.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "BookingDetail")
public class BookingDetail extends BaseEntityAudit {

    @ManyToMany(
            mappedBy = "bookingDetails",
            targetEntity = FlightPax.class
        ) 
    private Set<FlightPax> flightPaxes = new HashSet<>();
   
    @ManyToMany(
            mappedBy = "bookingDetails",
            targetEntity = Pnr.class
        ) 
    private Set<Pnr> pnrs = new HashSet<>();

	public Set<FlightPax> getFlightPaxes() {
		return flightPaxes;
	}

	public void setFlightPaxes(Set<FlightPax> flightPaxes) {
		this.flightPaxes = flightPaxes;
	}

	public Set<Pnr> getPnrs() {
		return pnrs;
	}

	public void setPnrs(Set<Pnr> pnrs) {
		this.pnrs = pnrs;
	}

}
