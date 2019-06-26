/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;
import java.util.Objects;
import javax.persistence.*;


@Entity
@Table(name = "passenger_wl_timestamp")
public class PassengerWLTimestamp {

	@SuppressWarnings("unused")
	public PassengerWLTimestamp() {
	}

	public PassengerWLTimestamp(Long paxId, Date watchlistCheckTimestamp) {
		this.paxId = paxId;
		this.watchlistCheckTimestamp = watchlistCheckTimestamp;
	}

	@Id
	@Column(name = "pwlt_id", columnDefinition = "bigint unsigned")
	private
	Long paxId;

	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "pwlt_id", referencedColumnName = "id", updatable = false, insertable = false)
	Passenger passenger;

    @Column(name = "pwlt_watchlist_check_timestamp")
    private Date watchlistCheckTimestamp;

	@Column(name = "pwlt_hit_count")
	private Integer hitCount = 0;

	public Date getWatchlistCheckTimestamp() {
		return watchlistCheckTimestamp;
	}

	@SuppressWarnings("unused")
	public void setWatchlistCheckTimestamp(Date watchlistCheckTimestamp) {
		this.watchlistCheckTimestamp = watchlistCheckTimestamp;
	}

	public Integer getHitCount() {
		return hitCount;
	}

	public void setHitCount(Integer hitCount) {
		this.hitCount = hitCount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PassengerWLTimestamp)) return false;
		PassengerWLTimestamp that = (PassengerWLTimestamp) o;
		return paxId.equals(that.paxId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(paxId);
	}
}