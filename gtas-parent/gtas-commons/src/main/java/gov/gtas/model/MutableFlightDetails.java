package gov.gtas.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mutable_flight_details", indexes = @Index(columnList = "full_utc_eta_timestamp", name = "utc_eta_index"))
public class MutableFlightDetails {

	@SuppressWarnings("unused")
	public MutableFlightDetails() {
	}

	public MutableFlightDetails(Long id) {
		this.flightId = id;
	}

	@Id
	@Column(name = "flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	private Flight flight;

	// ETD IS NOT UTC TIME. IT WILL BE STORED IN THE DATABASE AS UTC
	// BUT IS ACTUALLY WHATEVER AIRPORT THE TIME CAME FROM
	// TO SEE ETD LOOK AT etd
	@Column(name = "full_etd_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date localEtdDate;

	// ETA IS NOT UTC TIME. IT WILL BE STORED IN THE DATABASE AS UTC
	// BUT IS ACTUALLY WHATEVER AIRPORT THE TIME CAME FROM
	// TO SEE ETA TIME LOOK AT eta
	@Column(name = "full_eta_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date localEtaDate;

	@Column(name = "full_utc_etd_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date etd;

	@Column(name = "full_utc_eta_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date eta;

	/**
	 * calculated field THIS VALUE IS DERIVED FROM LOCAL TIME AND IS NOT IN UTC -
	 * E.G. A FLIGHT TAKING OFF IN TZ + 4 at 12:01AM 1/2 WILL BE SET TO 1/2 INSTEAD
	 * OF THE UTC DATE OF 1/1.
	 */
	@Column(name = "eta_date")
	@Temporal(TemporalType.DATE)
	private Date etaDate;

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public Date getLocalEtaDate() {
		return localEtaDate;
	}

	public void setLocalEtaDate(Date localEtaDate) {
		this.localEtaDate = localEtaDate;
	}

	public Date getEtaDate() {
		return etaDate;
	}

	public Date getLocalEtdDate() {
		return localEtdDate;
	}

	public void setLocalEtdDate(Date localEtdDate) {
		this.localEtdDate = localEtdDate;
	}

	public void setEtaDate(Date etaDate) {
		this.etaDate = etaDate;
	}

	public Date getEtd() {
		return etd;
	}

	public void setEtd(Date etd) {
		this.etd = etd;
	}

	public Date getEta() {
		return eta;
	}

	public void setEta(Date eta) {
		this.eta = eta;
	}
}
