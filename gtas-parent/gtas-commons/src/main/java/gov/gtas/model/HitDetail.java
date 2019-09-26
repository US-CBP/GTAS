/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "hit_detail")
public class HitDetail extends BaseEntity {
	private static final long serialVersionUID = 5219262569468670275L;

	public HitDetail() {
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hits_summary_id", nullable = false, referencedColumnName = "id")
	private HitsSummary parent;

	@Column(name = "title", nullable = false)
	private String Title;

	@Column(name = "description")
	private String Description;

	@Column(name = "hit_type", nullable = false, length = 3)
	private String hitType;
	/**
	 * String representation of matched conditions; it can be split into String[]
	 */
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "cond_text", columnDefinition = "TEXT NULL")
	private String ruleConditions;

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "rule_id", nullable = false)
	private Long ruleId;

	public HitsSummary getParent() {
		return parent;
	}

	public void setParent(HitsSummary parent) {
		this.parent = parent;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getRuleConditions() {
		return ruleConditions;
	}

	public void setRuleConditions(String ruleConditions) {
		this.ruleConditions = ruleConditions;
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getHitType() {
		return hitType;
	}

	public void setHitType(String hitType) {
		this.hitType = hitType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof HitDetail))
			return false;
		HitDetail hitDetail = (HitDetail) o;
		return getHitType().equals(hitDetail.getHitType()) && getRuleId().equals(hitDetail.getRuleId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getHitType(), getRuleId());
	}
}
