package gov.gtas.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;



public class UserLocationPK implements Serializable {
	
	/**
	 * A Composite key class for user_location table
	 */
	private static final long serialVersionUID = 1L;


	
	private String userId;
	
	private String airport;

	public String getUserId() {
		return userId;
	}

	public UserLocationPK() {}
	
	public UserLocationPK(String userId, String airport) 
	{
		this.userId = userId;
		this.airport = airport;
		
	}
	
	public String getAirport() {
		return airport;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((airport == null) ? 0 : airport.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserLocationPK other = (UserLocationPK) obj;
		if (airport == null) {
			if (other.airport != null)
				return false;
		} else if (!airport.equals(other.airport))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	
	

}
