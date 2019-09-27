package gov.gtas.common;

public class UserLocationStatus {

	private boolean primaryLocationEnabledInDb;
	private String primaryLocationAirport;
	private boolean primaryLocationCreated;

	public boolean isPrimaryLocationEnabledInDb() {
		return primaryLocationEnabledInDb;
	}

	public void setPrimaryLocationEnabledInDb(boolean primaryLocationEnabledInDb) {
		this.primaryLocationEnabledInDb = primaryLocationEnabledInDb;
	}

	public String getPrimaryLocationAirport() {
		return primaryLocationAirport;
	}

	public void setPrimaryLocationAirport(String primaryLocationAirport) {
		this.primaryLocationAirport = primaryLocationAirport;
	}

	public boolean isPrimaryLocationCreated() {
		return primaryLocationCreated;
	}

	public void setPrimaryLocationCreated(boolean primaryLocationCreated) {
		this.primaryLocationCreated = primaryLocationCreated;
	}

}
