/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.gtas.enumtype.HitSeverityEnum;
import gov.gtas.model.BaseEntityAudit;
import gov.gtas.model.HitMaker;
import gov.gtas.model.PIIObject;
import gov.gtas.model.UserGroup;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "hit_category")
public class HitCategory extends BaseEntityAudit {
	private static final long serialVersionUID = 1L;

	@Column(name = "category", nullable = false, unique = true)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "severity", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private HitSeverityEnum severity;

	@OneToMany(mappedBy = "hitCategory")
	@JsonIgnore
	private Set<HitMaker> hitMakers = new HashSet<>();

	@ManyToMany(mappedBy = "hitCategories")
	@JsonIgnore
	private Set<UserGroup> userGroups = new HashSet<>();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof HitCategory))
			return false;
		HitCategory that = (HitCategory) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Set<HitMaker> getHitMakers() {
		return hitMakers;
	}

	public void setHitMakers(Set<HitMaker> hitMakers) {
		this.hitMakers = hitMakers;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public HitSeverityEnum getSeverity() {
		return severity;
	}

	public void setSeverity(HitSeverityEnum severity) {
		this.severity = severity;
	}

}
