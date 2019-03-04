/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;
import javax.persistence.*;


@Entity
@Table(name = "passenger_wl_timestamp")
public class PassengerWLTimestamp extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;
    
    public PassengerWLTimestamp() {
    }
    
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="passenger_id")
    Passenger passenger; 
    
    private Date watchlistCheckTimestamp;

	public Date getWatchlistCheckTimestamp() {
		return watchlistCheckTimestamp;
	}

	public void setWatchlistCheckTimestamp(Date watchlistCheckTimestamp) {
		this.watchlistCheckTimestamp = watchlistCheckTimestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		//int result = super.hashCode();
		int result = 10;
		result = prime * result + ((passenger == null) ? 0 : passenger.hashCode());
		result = prime * result + ((watchlistCheckTimestamp == null) ? 0 : watchlistCheckTimestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PassengerWLTimestamp other = (PassengerWLTimestamp) obj;
		if (passenger == null) {
			if (other.passenger != null)
				return false;
		} else if (!passenger.equals(other.passenger))
			return false;
		if (watchlistCheckTimestamp == null) {
			if (other.watchlistCheckTimestamp != null)
				return false;
		} else if (!watchlistCheckTimestamp.equals(other.watchlistCheckTimestamp))
			return false;
		return true;
	}

	
}