/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.*;

import javax.persistence.*;

@Entity
@Table(name = "hits_summary")
public class HitsSummary extends BaseEntity {

	@Column(name = "hs_passenger_id", columnDefinition = "bigint unsigned")
	private Long paxId;

	@Column(name = "flight_id", columnDefinition = "bigint unsigned")
	private Long flightId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight_id", referencedColumnName = "id", updatable = false, insertable = false)
	private Flight flight;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hs_passenger_id", referencedColumnName = "id", updatable = false, insertable = false)
	private Passenger passenger;

	@Column(name = "hs_rule_count")
	private int ruleHitCount = 0;

	@Column(name = "hs_wl_count")
	private int watchListHitCount = 0;

	@Column(name = "hs_graph_count")
	private int graphHitCount = 0;

	@Column(name = "hs_manual_count")
	private int manualHitCount = 0;

	@Column(name = "hs_external_count")
	private int externalHitCount = 0;

	@Column(name = "hs_partial_count")
	private int partialHitCount;

	@Transient
	boolean updated = false;

	public int getExternalHitCount() {
		return externalHitCount;
	}

	public void setExternalHitCount(int externalHitCount) {
		this.externalHitCount = externalHitCount;
	}

	public void setRuleHitCount(int ruleHitCount) {
		this.ruleHitCount = ruleHitCount;
	}

	public int getManualHitCount() {
		return manualHitCount;
	}

	public void setManualHitCount(int manualHitCount) {
		this.manualHitCount = manualHitCount;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public void setWatchListHitCount(int watchListHitCount) {
		this.watchListHitCount = watchListHitCount;
	}

	public void setGraphHitCount(int graphHitCount) {
		this.graphHitCount = graphHitCount;
	}

	public int getPartialHitCount() {
		return partialHitCount;
	}

	public void setPartialHitCount(int partialHitCount) {
		this.partialHitCount = partialHitCount;
	}

	public Integer getRuleHitCount() {
		return ruleHitCount;
	}

	public void setRuleHitCount(Integer ruleHitCount) {
		this.ruleHitCount = ruleHitCount;
	}

	public Integer getWatchListHitCount() {
		return watchListHitCount;
	}

	public Long getPaxId() {
		return paxId;
	}

	public void setPaxId(Long paxId) {
		this.paxId = paxId;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Integer getGraphHitCount() {
		return graphHitCount;
	}

	public boolean hasHits() {
		return (ruleHitCount + graphHitCount + watchListHitCount + manualHitCount + partialHitCount) > 0;
	}

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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof HitsSummary))
			return false;
		HitsSummary that = (HitsSummary) o;
		return getPaxId().equals(that.getPaxId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPaxId());
	}

}
