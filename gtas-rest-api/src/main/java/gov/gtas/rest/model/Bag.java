package gov.gtas.rest.model;

public class Bag extends BaseModel {
	
	private Long id;
	private String bagId;
	private String dataSource;
	private Long passengerId;
	private Long flightId;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBagId() {
		return bagId;
	}
	public void setBagId(String bagId) {
		this.bagId = bagId;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public Long getPassengerId() {
		return passengerId;
	}
	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}
	public Long getFlightId() {
		return flightId;
	}
	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}
	
	
	
	
	

}
