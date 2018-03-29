package gov.gtas.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "BookingDetail")
public class BookingDetail extends BaseEntityAudit {


	@Size(min = 4, max = 4)
	@Column(name = "flight_number", length = 4, nullable = false)
	private String flightNumber;

	/** calculated field */
	@Column(name = "etd_date")
	@Temporal(TemporalType.DATE)
	private Date etdDate;

	/** calculated field */
	@Column(name = "eta_date")
	@Temporal(TemporalType.DATE)
	private Date etaDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date etd;

	@Temporal(TemporalType.TIMESTAMP)
	private Date eta;

	@Column(nullable = false)
	private String origin;

	@Column(name = "origin_country", length = 3)
	private String originCountry;

	@Column(nullable = false)
	private String destination;

	@Column(name = "destination_country", length = 3)
	private String destinationCountry;

	@Column(name = "processed")
	private Boolean processed = Boolean.FALSE;

    @ManyToMany(fetch=FetchType.EAGER, targetEntity = Passenger.class, cascade = { CascadeType.ALL })
    @JoinTable(name = "pax_booking", joinColumns = @JoinColumn(name = "booking_detail_id"), inverseJoinColumns = @JoinColumn(name = "pax_id"))
    private Set<Passenger> passengers = new HashSet<>();
   
    @ManyToMany(fetch=FetchType.EAGER, targetEntity = Pnr.class, cascade = { CascadeType.ALL })
    @JoinTable(name = "pnr_booking", joinColumns = @JoinColumn(name = "booking_detail_id"), inverseJoinColumns = @JoinColumn(name = "pnr_id"))     
    private Set<Pnr> pnrs = new HashSet<>();

	public Set<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(Set<Passenger> passengers) {
		this.passengers = passengers;
	}

	public Set<Pnr> getPnrs() {
		return pnrs;
	}

	public void setPnrs(Set<Pnr> pnrs) {
		this.pnrs = pnrs;
	}


}
