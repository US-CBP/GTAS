package gov.gtas.rest.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HitsSummary {
	
	private Long id;
	private Date createdDate;
	private String hitType;
	private Integer ruleHitCount;
	private Integer wlHitCount;
	private Long flightId;
	private Long passengerId;
	private List<HitDetail> hitDetail;
	private List<HitDisposition> hitDisposition;
	
	
	@JsonIgnore
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getHitType() {
		return hitType;
	}
	public void setHitType(String hitType) {
		this.hitType = hitType;
	}
	public Integer getRuleHitCount() {
		return ruleHitCount;
	}
	public void setRuleHitCount(Integer ruleHitCount) {
		this.ruleHitCount = ruleHitCount;
	}
	public Integer getWlHitCount() {
		return wlHitCount;
	}
	public void setWlHitCount(Integer wlHitCount) {
		this.wlHitCount = wlHitCount;
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
	@JsonProperty(value = "passengerId")
	public Long getPassengerId() {
		return passengerId;
	}
	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}
	public List<HitDetail> getHitDetail() {
		return hitDetail;
	}
	public void setHitDetail(List<HitDetail> hitDetail) {
		this.hitDetail = hitDetail;
	}
	public List<HitDisposition> getHitDisposition() {
		return hitDisposition;
	}
	public void setHitDisposition(List<HitDisposition> hitDisposition) {
		this.hitDisposition = hitDisposition;
	}
	
	

}
