/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "user_group_notifications")
public class UserGroupNotification extends Notification {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hm_id", referencedColumnName = "id", insertable = false, updatable = false)
	private UserGroup userGroup;

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}
}
