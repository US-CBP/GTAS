package gov.gtas.parsers.vo;

public class BagVo {

	private String bagId;
	private String data_source;
	private String destinationAirport;
	private String airline;
	private String firstName;
	private String lastName;
	private boolean headPool=false;
	
	public BagVo(String bid,String source,String airport,String aline,String fName,String lName){
		this.bagId=bid;
		this.data_source=source;
		this.destinationAirport=airport;
		this.airline=aline;
		this.firstName=fName;
		this.lastName=lName;
	}
	public BagVo(){
		
	}
	
	public boolean isHeadPool() {
		return headPool;
	}
	public void setHeadPool(boolean headPool) {
		this.headPool = headPool;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getDestinationAirport() {
		return destinationAirport;
	}
	public void setDestinationAirport(String destinationAirport) {
		this.destinationAirport = destinationAirport;
	}
	public String getAirline() {
		return airline;
	}
	public void setAirline(String airline) {
		this.airline = airline;
	}
	public String getBagId() {
		return bagId;
	}
	public void setBagId(String bagId) {
		this.bagId = bagId;
	}
	public String getData_source() {
		return data_source;
	}
	public void setData_source(String data_source) {
		this.data_source = data_source;
	}
	 
}
