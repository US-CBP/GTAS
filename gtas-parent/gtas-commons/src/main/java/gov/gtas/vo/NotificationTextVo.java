/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

public class NotificationTextVo {

	private String firstName;
	private String lastName;
	private String flightNumber;
	private String watchlistOrRuleName;
	private String dob;
	private String passportNo;
	private String timeRemaining;

	private Long wlCategoryId;

	public Long getWlCategoryId() {
		return wlCategoryId;
	}

	public void setWlCategoryId(Long wlCategoryId) {
		this.wlCategoryId = wlCategoryId;
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

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getWatchlistOrRuleName() {
		return watchlistOrRuleName;
	}

	public void setWatchlistOrRuleName(String watchlistOrRuleName) {
		this.watchlistOrRuleName = watchlistOrRuleName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(String timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	@Override
	public String toString() {
		return "GTAS priority Hit Notification [firstName=" + firstName + ", lastName=" + lastName + ", flightNumber="
				+ flightNumber + ", watchlistOrRuleName=" + watchlistOrRuleName + ", dob=" + dob + ", passportNo="
				+ passportNo + ", timeRemaining=" + timeRemaining + "]";
	}

}
