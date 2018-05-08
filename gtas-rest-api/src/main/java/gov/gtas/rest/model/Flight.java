package gov.gtas.rest.model;

import java.util.Date;

public class Flight extends BaseModel{
	
	private Long id;
	private String carrier;
	private String destination;
	private String direction;
	private Date eta;
	private Date etd;
	private Date flightDate;
	private Boolean operatingFlight;
	private Boolean marketingFlight;
	private String flightNumber;
	private String fullFlightNumber;
	private String origin;
	private String originCountry;
	private String passengerCount;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Date getEta() {
		return eta;
	}
	public void setEta(Date eta) {
		this.eta = eta;
	}
	public Date getEtd() {
		return etd;
	}
	public void setEtd(Date etd) {
		this.etd = etd;
	}
	public Date getFlightDate() {
		return flightDate;
	}
	public void setFlightDate(Date flightDate) {
		this.flightDate = flightDate;
	}
	public Boolean getOperatingFlight() {
		return operatingFlight;
	}
	public void setOperatingFlight(Boolean operatingFlight) {
		this.operatingFlight = operatingFlight;
	}
	public Boolean getMarketingFlight() {
		return marketingFlight;
	}
	public void setMarketingFlight(Boolean marketingFlight) {
		this.marketingFlight = marketingFlight;
	}
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public String getFullFlightNumber() {
		return fullFlightNumber;
	}
	public void setFullFlightNumber(String fullFlightNumber) {
		this.fullFlightNumber = fullFlightNumber;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getOriginCountry() {
		return originCountry;
	}
	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}
	public String getPassengerCount() {
		return passengerCount;
	}
	public void setPassengerCount(String passengerCount) {
		this.passengerCount = passengerCount;
	}

	

	
	

}
