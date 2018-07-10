package gov.gtas.rest.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CaseHitDisposition {

	private Long caseId;
	private Long hitDispId;
	private Long id;
	private Date createdAt;
	private Date updatedAt;
	private String description;
	private Long hitId;
	private String status;
	private String valid;
	private Long ruleCatId;
	private String category;
	private String categoryDescription;
	private List<CaseHitDispComment> caseHitDispComment;
	
	@JsonIgnore
	@JsonProperty(value = "caseId")
	public Long getCaseId() {
		return caseId;
	}
	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}
	@JsonIgnore
	@JsonProperty(value = "hitDispId")
	public Long getHitDispId() {
		return hitDispId;
	}
	public void setHitDispId(Long hitDispId) {
		this.hitDispId = hitDispId;
	}
	@JsonIgnore
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@JsonIgnore
	@JsonProperty(value = "hitId")
	public Long getHitId() {
		return hitId;
	}
	public void setHitId(Long hitId) {
		this.hitId = hitId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	
	
	public List<CaseHitDispComment> getCaseHitDispComment() {
		return caseHitDispComment;
	}
	public void setCaseHitDispComment(List<CaseHitDispComment> caseHitDispComment) {
		this.caseHitDispComment = caseHitDispComment;
	}
	@JsonIgnore
	@JsonProperty(value = "ruleCatId")
	public Long getRuleCatId() {
		return ruleCatId;
	}
	public void setRuleCatId(Long ruleCatId) {
		this.ruleCatId = ruleCatId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategoryDescription() {
		return categoryDescription;
	}
	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}
	
	
	
	
	
	

}
