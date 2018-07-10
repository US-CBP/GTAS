package gov.gtas.rest.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PassengerTravelDetail {
	
	
	private Long id;
	private Long flightId;
	private String fullFlightNumber;
	private String debarkation;
	private String debCountry;
	private String embarkation;
	private String embCountry;
	private Boolean headOfPool;
	private String msgSource;
	private String firstArrivalPort;
	private String refNumber;
	private String residenceCountry;
	private String travelerType;
	private Double averageBagWeight;
	private Integer bagCount;
	private Double bagWeight;
	private Long installAddressId;
	private Long passengerId;
	private String carrier;
	private Date eta;
	private Date etd;
	
	private Date flightDate;
	
	
	
	@JsonIgnore
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getDebarkation() {
		return debarkation;
	}
	public void setDebarkation(String debarkation) {
		this.debarkation = debarkation;
	}
	public String getDebCountry() {
		return debCountry;
	}
	public void setDebCountry(String debCountry) {
		this.debCountry = debCountry;
	}
	public String getEmbarkation() {
		return embarkation;
	}
	public void setEmbarkation(String embarkation) {
		this.embarkation = embarkation;
	}
	public String getEmbCountry() {
		return embCountry;
	}
	public void setEmbCountry(String embCountry) {
		this.embCountry = embCountry;
	}
	public Boolean getHeadOfPool() {
		return headOfPool;
	}
	public void setHeadOfPool(Boolean headOfPool) {
		this.headOfPool = headOfPool;
	}
	public String getMsgSource() {
		return msgSource;
	}
	public void setMsgSource(String msgSource) {
		this.msgSource = msgSource;
	}

	public String getFirstArrivalPort() {
		return firstArrivalPort;
	}

	public void setFirstArrivalPort(String firstArrivalPort) {
		this.firstArrivalPort = firstArrivalPort;
	}

	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}
	
	public String getResidenceCountry() {
		return residenceCountry;
	}

	public void setResidenceCountry(String residenceCountry) {
		this.residenceCountry = residenceCountry;
	}

	public String getTravelerType() {
		return travelerType;
	}
	public void setTravelerType(String travelerType) {
		this.travelerType = travelerType;
	}
	public Double getAverageBagWeight() {
		return averageBagWeight;
	}
	public void setAverageBagWeight(Double averageBagWeight) {
		this.averageBagWeight = averageBagWeight;
	}
	public Integer getBagCount() {
		return bagCount;
	}
	public void setBagCount(Integer bagCount) {
		this.bagCount = bagCount;
	}
	public Double getBagWeight() {
		return bagWeight;
	}
	public void setBagWeight(Double bagWeight) {
		this.bagWeight = bagWeight;
	}
	@JsonIgnore
	@JsonProperty(value = "flightId")
	public Long getFlightId() {
		return flightId;
	}
	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}
	@JsonIgnore
	@JsonProperty(value = "installAddressId")
	public Long getInstallAddressId() {
		return installAddressId;
	}
	public void setInstallAddressId(Long installAddressId) {
		this.installAddressId = installAddressId;
	}
	@JsonIgnore
	@JsonProperty(value = "passengerId")
	public Long getPassengerId() {
		return passengerId;
	}
	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getEta() {
		return eta;
	}
	public void setEta(Date eta) {
		this.eta = eta;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getEtd() {
		return etd;
	}
	public void setEtd(Date etd) {
		this.etd = etd;
	}
	public String getFullFlightNumber() {
		return fullFlightNumber;
	}
	public void setFullFlightNumber(String fullFlightNumber) {
		this.fullFlightNumber = fullFlightNumber;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getFlightDate() {
		return flightDate;
	}
	public void setFlightDate(Date flightDate) {
		this.flightDate = flightDate;
	}
	

	



}
