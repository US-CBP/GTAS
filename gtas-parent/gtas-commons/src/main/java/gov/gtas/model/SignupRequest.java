package gov.gtas.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.gtas.enumtype.SignupRequestStatus;

@Entity
@Table(name = "signup_request")
public class SignupRequest extends BaseEntityAudit {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4089021659160676960L;

	@Column
	private String username;
	
	private String firstName;
	private String lastName;

	@Column
	private String email;

	@Column
	private String supervisor;

	@Column(name = "signup_location_id", insertable = false, updatable = false, nullable = false, columnDefinition = "bigint unsigned")
	private Long signupLocationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "signup_location_id", updatable = false)
	private SignupLocation signupLocation;

	@Enumerated(EnumType.STRING)
	@Column(name = "request_status")
	private SignupRequestStatus status;

	@Column(name = "reviewed_by")
	private String reviewedBy;

	@Column(name = "reviewed_date")
	private Date reviewedDate;
	
	
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

	public Long getSignupLocationId() {
		return signupLocationId;
	}

	public void setSignupLocationId(Long  signupLocationId) {
		this.signupLocationId = signupLocationId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public SignupLocation getSignupLocation() {
		return signupLocation;
	}

	public void setSignupLocation(SignupLocation signupLocation) {
		this.signupLocation = signupLocation;
	}

	public SignupRequestStatus getStatus() {
		return status;
	}

	public void setStatus(SignupRequestStatus status) {
		this.status = status;
	}

	public String getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(String reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

	public Date getReviewedDate() {
		return reviewedDate;
	}

	public void setReviewedDate(Date reviewedDate) {
		this.reviewedDate = reviewedDate;
	}

}
