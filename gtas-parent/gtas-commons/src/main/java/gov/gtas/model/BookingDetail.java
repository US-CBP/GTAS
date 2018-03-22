package gov.gtas.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "BookingDetail")
public class BookingDetail extends BaseEntityAudit {

    @ManyToMany(fetch=FetchType.EAGER, targetEntity = FlightPax.class, cascade = { CascadeType.ALL })
    @JoinTable(name = "pnr_booking", joinColumns = @JoinColumn(name = "booking_detail_id"), inverseJoinColumns = @JoinColumn(name = "flight_pax_id"))    
    private Set<FlightPax> flightPaxes = new HashSet<>();
   
    @ManyToMany(fetch=FetchType.EAGER, targetEntity = Pnr.class, cascade = { CascadeType.ALL })
    @JoinTable(name = "pnr_booking", joinColumns = @JoinColumn(name = "booking_detail_id"), inverseJoinColumns = @JoinColumn(name = "pnr_id"))     
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
