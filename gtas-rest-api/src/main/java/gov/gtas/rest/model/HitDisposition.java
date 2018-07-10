package gov.gtas.rest.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HitDisposition {
	
	private Long id;
	private String ruleCategory;
	private String valid;
	private String status;
	private String description;
	private  Date createdAt;
	private Date updatedA;
	private List<HitDispositionComments> dispositionComments;
	
	@JsonIgnore
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRuleCategory() {
		return ruleCategory;
	}
	public void setRuleCategory(String ruleCategory) {
		this.ruleCategory = ruleCategory;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedA() {
		return updatedA;
	}
	public void setUpdatedA(Date updatedA) {
		this.updatedA = updatedA;
	}
	public List<HitDispositionComments> getDispositionComments() {
		return dispositionComments;
	}
	public void setDispositionComments(List<HitDispositionComments> dispositionComments) {
		this.dispositionComments = dispositionComments;
	}
	
	
	
}
