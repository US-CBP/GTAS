package gov.gtas.rest.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class HitDetail {
	
	private Long id;
	private Long hitsSummaryId;
	private  String title;
	private String description;
	private Date createdDate;
	private String hitType;
	private String condText;
	private String ruleId;
	
	@JsonIgnore
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JsonIgnore
	@JsonProperty(value = "hitsSummaryId")
	public Long getHitsSummaryId() {
		return hitsSummaryId;
	}
	public void setHitsSummaryId(Long hitsSummaryId) {
		this.hitsSummaryId = hitsSummaryId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	
	public String getCondText() {
		return condText;
	}
	public void setCondText(String condText) {
		this.condText = condText;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@JsonIgnore
	@JsonProperty(value = "ruleId")
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	
	

}
