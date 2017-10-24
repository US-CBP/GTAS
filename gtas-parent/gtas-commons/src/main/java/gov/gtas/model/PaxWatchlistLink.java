/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import gov.gtas.model.watchlist.WatchlistItem;

@Entity
@Table(name = "pax_watchlist_link")
public class PaxWatchlistLink {

	@Id  
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Basic(optional = false)  
    @Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")  
    private Long id;  
    
    @Column(name= "percent_match")
    private float percentMatch;
    
    @Column(name= "last_run_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRunTimestamp;
    
    @ManyToOne
    @JoinColumn(name="watchlist_item_id", nullable = true, referencedColumnName = "id")
    private WatchlistItem watchlistItem;
    
    @ManyToOne
    @JoinColumn(name="passenger_id")
    private Passenger passenger;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public float getPercentMatch() {
		return percentMatch;
	}

	public void setPercentMatch(float percentMatch) {
		this.percentMatch = percentMatch;
	}

	public Date getLastRunTimestamp() {
		return lastRunTimestamp;
	}

	public void setLastRunTimestamp(Date lastRunTimestamp) {
		this.lastRunTimestamp = lastRunTimestamp;
	}

	public WatchlistItem getWatchlistItem() {
		return watchlistItem;
	}

	public void setWatchlistItem(WatchlistItem watchlistItem) {
		this.watchlistItem = watchlistItem;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public int getVerifiedStatus() {
		return verifiedStatus;
	}

	public void setVerifiedStatus(int verifiedStatus) {
		this.verifiedStatus = verifiedStatus;
	}

	@Column(name="verified_status")
    private int verifiedStatus;
    
}
