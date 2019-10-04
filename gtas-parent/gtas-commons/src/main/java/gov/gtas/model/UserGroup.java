/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import gov.gtas.model.lookup.HitCategory;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_group")
public class UserGroup extends BaseEntityAudit {

	@Column(name = "ug_name", unique = true)
	private String groupName;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = User.class)
	@JoinTable(name = "ug_user_join", joinColumns = @JoinColumn(name = "ug_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> groupMembers = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = HitCategory.class)
	@JoinTable(name = "ug_hit_category_join", joinColumns = @JoinColumn(name = "ug_id"), inverseJoinColumns = @JoinColumn(name = "hc_id"))
	private Set<HitCategory> hitCategories = new HashSet<>();

	@OneToMany(mappedBy = "userGroup", fetch = FetchType.LAZY)
	private Set<UserGroupNotification> notifications = new HashSet<>();

	public Set<User> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(Set<User> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Set<HitCategory> getHitCategories() {
		return hitCategories;
	}

	public void setHitCategories(Set<HitCategory> hitCategories) {
		this.hitCategories = hitCategories;
	}

	public Set<UserGroupNotification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<UserGroupNotification> notifications) {
		this.notifications = notifications;
	}
}
