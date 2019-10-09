package gov.gtas.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "dwell_time")
public class DwellTime implements Serializable {

	private static final long serialVersionUID = 1L;

	public DwellTime() {
	}

	public DwellTime(Date arrival, Date departure, String airport, Pnr pnr) {
		this.arrivalTime = arrival;
		this.departureTime = departure;
		this.location = airport;
		this.pnrs.add(pnr);
		// java.lang.NullPointerException issue #307 code fix
		if (this.departureTime != null && this.arrivalTime != null) {
			long diff = this.departureTime.getTime() - this.arrivalTime.getTime();
			if (diff > 0) {
				int minutes = (int) TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
				DecimalFormat df = new DecimalFormat("#.##");
				this.dwellTime = Double.valueOf(df.format((double) minutes / 60));
			}
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "arrival_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date arrivalTime;

	@Column(name = "departure_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date departureTime;

	@Column(name = "arrival_airport", length = 3)
	private String location;

	@ManyToMany(mappedBy = "dwellTimes", targetEntity = Pnr.class)
	private Set<Pnr> pnrs = new HashSet<Pnr>();

	@Column(name = "dwell_time")
	private Double dwellTime;

	@Column(name = "flying_from")
	private String flyingFrom;

	@Column(name = "flying_to")
	private String flyingTo;

	public String getFlyingFrom() {
		return flyingFrom;
	}

	public void setFlyingFrom(String flyingFrom) {
		this.flyingFrom = flyingFrom;
	}

	public String getFlyingTo() {
		return flyingTo;
	}

	public void setFlyingTo(String flyingTo) {
		this.flyingTo = flyingTo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getDwellTime() {
		return dwellTime;
	}

	public void setDwellTime(Double dwellTime) {
		this.dwellTime = dwellTime;
	}

	public Set<Pnr> getPnrs() {
		return pnrs;
	}

	public void setPnrs(Set<Pnr> pnrs) {
		this.pnrs = pnrs;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.location, this.flyingFrom, this.flyingTo, this.id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DwellTime))
			return false;
		final DwellTime other = (DwellTime) obj;
		return Objects.equals(this.id, other.id) && Objects.equals(this.location, other.location)
				&& Objects.equals(this.flyingFrom, other.flyingFrom) && Objects.equals(this.flyingTo, other.flyingTo);
	}
}
