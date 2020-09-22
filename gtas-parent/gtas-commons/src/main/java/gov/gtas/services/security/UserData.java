/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2939774474055002106L;
	private final String userId;
	private String password;
	private final String firstName;
	private final String lastName;
	private final int active;
	private Set<RoleData> roles = new HashSet<RoleData>();
	private String email;
	private Boolean emailEnabled;
	private Boolean highPriorityEmail;
	private boolean archived;
	private String phoneNumber;

	public UserData(@JsonProperty("userId") String userId, @JsonProperty("password") String password,
			@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName,
			@JsonProperty("active") int active, @JsonProperty("roles") Set<RoleData> roles,
			@JsonProperty("email") String email, @JsonProperty("emailEnabled") Boolean emailEnabled,
			@JsonProperty("highPriorityEmail") Boolean highPriorityEmail, @JsonProperty("archived") boolean archived,
			@JsonProperty("phoneNumber") String phoneNumber) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.active = active;
		this.password = password;
		this.roles = roles;
		this.email = email;
		this.emailEnabled = emailEnabled;
		this.highPriorityEmail = highPriorityEmail;
		this.archived = archived;
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("userId")
	public final String getUserId() {
		return userId;
	}

	@JsonProperty("password")
	public final String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JsonProperty("firstName")
	public final String getFirstName() {
		return firstName;
	}

	@JsonProperty("lastName")
	public final String getLastName() {
		return lastName;
	}

	@JsonProperty("active")
	public final int getActive() {
		return active;
	}

	public final Set<RoleData> getRoles() {
		return roles;
	}

	@JsonProperty("archived")
	public boolean getArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.userId, this.password, this.firstName, this.lastName, this.active);
	}

	@Override
	public boolean equals(Object target) {

		if (this == target) {
			return true;
		}

		if (!(target instanceof UserData)) {
			return false;
		}

		UserData dataTarget = ((UserData) target);

		return new EqualsBuilder().append(this.userId, dataTarget.getUserId())
				.append(this.firstName, dataTarget.getFirstName()).append(this.lastName, dataTarget.getLastName())
				.append(this.password, dataTarget.getPassword()).append(this.active, dataTarget.getActive())
				.append(this.roles, dataTarget.getRoles()).isEquals();
	}

	@Override
	public String toString() {
		return "UserData [userId=" + userId + ", password=" + password + ", firstName=" + firstName + ", lastName="
				+ lastName + ", active=" + active + ", roles=" + roles + "]";
	}

	public Boolean getEmailEnabled() {
		return emailEnabled;
	}

	public void setEmailEnabled(Boolean emailEnabled) {
		this.emailEnabled = emailEnabled;
	}

	public Boolean getHighPriorityEmail() {
		return highPriorityEmail;
	}

	public void setHighPriorityEmail(Boolean highPriorityEmail) {
		this.highPriorityEmail = highPriorityEmail;
	}
}
