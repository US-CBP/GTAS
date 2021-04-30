/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import gov.gtas.constant.DomainModelConstants;

@Entity
@Table(name = "user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	public User() {
	}

	public User(String userId, String password, String firstName, String lastName, int active, Set<Role> roles,
			boolean archived) {

		this.userId = userId;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.active = active;
		this.roles = roles;
		this.archived = archived;
	}

	public User(String userId, String password, String firstName, String lastName, int active, Set<Role> roles,
			String email, boolean isEmailEnabled, boolean highPriorityHitsEmailNotification, boolean archived,
			String phoneNumber) {

		this.userId = userId;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.active = active;
		this.roles = roles;
		this.email = email;
		this.isEmailEnabled = isEmailEnabled;
		this.highPriorityHitsEmailNotification = highPriorityHitsEmailNotification;
		this.archived = archived;
		this.phoneNumber = phoneNumber;
	}

	@Id
	@Column(name = "user_id", length = DomainModelConstants.GTAS_USERID_COLUMN_SIZE)
	private String userId;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "active")
	private int active;

	@Column(name = "consecutive_failed_login_attempts")
	private Integer consecutiveFailedLoginAttempts;

	@Column(name = "reset_token")
	private String resetToken;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "password_reset_token_id")
	private PasswordResetToken passwordResetToken;

	@Column(name = "email")
	private String email;

	@Column(name = "email_enabled")
	private boolean isEmailEnabled;

	@Column(name = "high_priority_hits_email")
	private boolean highPriorityHitsEmailNotification;

	@Column(name = "archived")
	private boolean archived;

	@Column(name = "phoneNumber")
	private String phoneNumber;

	@ManyToMany(targetEntity = UserGroup.class, fetch = FetchType.LAZY)
	@JoinTable(name = "ug_user_join", inverseJoinColumns = @JoinColumn(name = "ug_id"), joinColumns = @JoinColumn(name = "user_id"))
	private Set<UserGroup> userGroups = new HashSet<>();

	// Notification that the user is a part of (elected or assigned)
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<UserNotification> notifications = new HashSet<>();

	// Notifications that the user created / owns.
	@OneToMany(mappedBy = "notificationOwner", fetch = FetchType.LAZY)
	private Set<Notification> notificationOwners = new HashSet<>();

	@ManyToMany(targetEntity = Role.class, cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
	private Set<HitMaker> hitMakers = new HashSet<>();

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Integer getConsecutiveFailedLoginAttempts() {
		return consecutiveFailedLoginAttempts;
	}

	public void setConsecutiveFailedLoginAttempts(Integer consecutiveFailedLoginAttempts) {
		this.consecutiveFailedLoginAttempts = consecutiveFailedLoginAttempts;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public boolean getHighPriorityHitsEmailNotification() {
		return highPriorityHitsEmailNotification;
	}

	public void setHighPriorityHitsEmailNotification(boolean highPriorityHitsEmailNotification) {
		this.highPriorityHitsEmailNotification = highPriorityHitsEmailNotification;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getEmailEnabled() {
		return isEmailEnabled;
	}

	public void setEmailEnabled(Boolean emailEnabled) {
		isEmailEnabled = emailEnabled;
	}

	public int getActive() {
		return active;
	}

	public boolean isActive() {
		return active == 1;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public PasswordResetToken getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(PasswordResetToken passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	public boolean getArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.userId.toUpperCase(), this.password, this.firstName, this.lastName, this.active);
	}

	@Override
	public boolean equals(Object target) {
		if (this == target) {
			return true;
		}

		if (!(target instanceof User)) {
			return false;
		}

		User dataTarget = ((User) target);

		return new EqualsBuilder().append(this.userId.toUpperCase(), dataTarget.getUserId().toUpperCase())
				.append(this.firstName, dataTarget.getFirstName()).append(this.lastName, dataTarget.getLastName())
				.append(this.password, dataTarget.getPassword()).append(this.active, dataTarget.getActive())
				.append(this.roles, dataTarget.getRoles()).isEquals();
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", password=" + password + ", firstName=" + firstName + ", lastName="
				+ lastName + ", active=" + active + ", roles=" + roles + "]";
	}

	public Set<UserNotification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<UserNotification> notifications) {
		this.notifications = notifications;
	}

	public Set<Notification> getNotificationOwners() {
		return notificationOwners;
	}

	public void setNotificationOwners(Set<Notification> notificationOwners) {
		this.notificationOwners = notificationOwners;
	}

	public Set<HitMaker> getHitMakers() {
		return hitMakers;
	}

	public void setHitMakers(Set<HitMaker> hitMakers) {
		this.hitMakers = hitMakers;
	}

	public Set<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
}
