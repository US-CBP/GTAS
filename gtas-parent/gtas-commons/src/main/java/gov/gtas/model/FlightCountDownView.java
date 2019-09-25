/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import org.springframework.data.annotation.Immutable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Immutable
@Table(name = "flight_countdown_view")
public class FlightCountDownView {

	@Id
	@Column(name = "fcdv_flight_id", columnDefinition = "bigint unsigned", updatable = false, insertable = false)
	private Long flightId;

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fcdv_flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	private Flight flight;

	@Column(name = "fcdv_countdown_timer")
	private Date countDownTimer; // This is the ETA of a flight if inbound and ETD of a flight if outbound.

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Date getCountDownTimer() {
		return countDownTimer;
	}

	public void setCountDownTimer(Date countDownTimer) {
		this.countDownTimer = countDownTimer;
	}
}
