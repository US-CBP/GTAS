package gov.gtas.vo.passenger;

public class CodeShareVo {
	
	private String marketingFlightNumber;
	private String operatingFlightNumber;
	private String operatingCarrier;
	private String marketingCarrier;
	private String fullMarketingFlightNumber;
	private String fullOperatingFlightNumber;
	
	
	public CodeShareVo(){
		
	}
	public CodeShareVo(String mCarrier,String mNumber,String oCarrier,String oNumber){
		this.marketingCarrier=mCarrier;
		this.marketingFlightNumber=mNumber;
		this.operatingCarrier=oCarrier;
		this.operatingFlightNumber=oNumber;
		this.fullMarketingFlightNumber=mCarrier+mNumber;
		this.fullOperatingFlightNumber=oCarrier+oNumber;
		
	}
	public String getOperatingCarrier() {
		return operatingCarrier;
	}
	public void setOperatingCarrier(String operatingCarrier) {
		this.operatingCarrier = operatingCarrier;
	}
	public String getMarketingCarrier() {
		return marketingCarrier;
	}
	public void setMarketingCarrier(String marketingCarrier) {
		this.marketingCarrier = marketingCarrier;
	}
	public String getFullMarketingFlightNumber() {
		return fullMarketingFlightNumber;
	}

	public String getFullOperatingFlightNumber() {
		return fullOperatingFlightNumber;
	}

	public String getMarketingFlightNumber() {
		return marketingFlightNumber;
	}
	public void setMarketingFlightNumber(String marketingFlightNumber) {
		this.marketingFlightNumber = marketingFlightNumber;
	}
	public String getOperatingFlightNumber() {
		return operatingFlightNumber;
	}
	public void setOperatingFlightNumber(String operatingFlightNumber) {
		this.operatingFlightNumber = operatingFlightNumber;
	}
	
}
