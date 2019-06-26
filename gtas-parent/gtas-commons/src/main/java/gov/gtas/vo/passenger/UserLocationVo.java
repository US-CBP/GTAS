package gov.gtas.vo.passenger;

/**
 * A view object for UserLocation data
 *
 */
public class UserLocationVo {

	private String userId;
	private String airport;
	private boolean primaryLocation;

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

	public boolean isPrimaryLocation() {
		return primaryLocation;
	}

	public void setPrimaryLocation(boolean primaryLocation) {
		this.primaryLocation = primaryLocation;
	}

}
