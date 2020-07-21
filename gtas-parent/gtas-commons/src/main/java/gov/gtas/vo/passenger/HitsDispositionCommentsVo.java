/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.vo.passenger;

import gov.gtas.model.PIIObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class HitsDispositionCommentsVo implements Serializable, PIIObject {

	private static final long serialVersionUID = 1L;

	private Date createdAt;

	private String createdBy;

	private Date updatedAt;

	private String updatedBy;

	public HitsDispositionCommentsVo() {
	}

	private String comments;

	private long hitDispId;

	private long hitId;

	private Set<AttachmentVo> attachmentSet = new HashSet<AttachmentVo>();

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public long getHitDispId() {
		return hitDispId;
	}

	public void setHitDispId(long hitDispId) {
		this.hitDispId = hitDispId;
	}

	public long getHitId() {
		return hitId;
	}

	public void setHitId(long hitId) {
		this.hitId = hitId;
	}

	public Set<AttachmentVo> getAttachmentSet() {
		return attachmentSet;
	}

	public void setAttachmentSet(Set<AttachmentVo> attachmentSet) {
		this.attachmentSet = attachmentSet;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public PIIObject deletePII() {
		this.comments = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.comments = "MASKED";
		return this;
	}
}
