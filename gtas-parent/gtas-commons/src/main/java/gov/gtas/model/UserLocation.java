package gov.gtas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "user_location")
@IdClass(UserLocationPK.class)
public class UserLocation {

	@Id
	@Column(name = "user_id")
	private String userId;

	@Id
	@Column(name = "airport")
	private String airport;

	@Column(name = "primary_location")
	private Boolean primaryLocation;

	public UserLocation() {
	}

	public UserLocation(String userId, String airport, Boolean primaryLocation) {
		this.userId = userId;
		this.airport = airport;
		this.primaryLocation = primaryLocation;

	}

	public Boolean getPrimaryLocation() {
		return primaryLocation;
	}

	public void setPrimaryLocation(Boolean primaryLocation) {
		this.primaryLocation = primaryLocation;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAirport() {
		return airport;
	}

	public void setAirport(String airport) {
		this.airport = airport;
	}

}
