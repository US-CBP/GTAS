package gov.gtas.rest.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CaseHitDispComment {

	private Long hitDispId;
	private Long hitDispCommentsId;
	private Date createdAt;
	private Date updatedAt;
	private String comments;
	
	
	@JsonIgnore
	@JsonProperty(value = "hitDispId")
	public Long getHitDispId() {
		return hitDispId;
	}
	public void setHitDispId(Long hitDispId) {
		this.hitDispId = hitDispId;
	}
	@JsonIgnore
	@JsonProperty(value = "hitDispCommentsId")
	public Long getHitDispCommentsId() {
		return hitDispCommentsId;
	}
	public void setHitDispCommentsId(Long hitDispCommentsId) {
		this.hitDispCommentsId = hitDispCommentsId;
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
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	
	
	
	

}
