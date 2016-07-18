/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DispositionData {
	private final Long passengerId;
	private final Long flightId;
	private final Long statusId;
	private final String comments;
	private final String user;

	public DispositionData(@JsonProperty("passengerId") Long passengerId,
			@JsonProperty("flightId") Long flightId,
			@JsonProperty("statusId") Long statusId,
			@JsonProperty("comments") String comments,
			@JsonProperty("user") String user) {
		this.passengerId = passengerId;
		this.flightId = flightId;
		this.statusId = statusId;
		this.comments = comments;
		this.user = user;
	}

	@JsonProperty("passengerId")
	public Long getPassengerId() {
		return passengerId;
	}

	@JsonProperty("flightId")
	public Long getFlightId() {
		return flightId;
	}

	@JsonProperty("statusId")
	public Long getStatusId() {
		return statusId;
	}

	@JsonProperty("comments")
	public String getComments() {
		return comments;
	}

	@JsonProperty("user")
	public String getUser() {
		return user;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.passengerId, this.flightId, this.statusId,
				this.comments);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DispositionData))
			return false;
		final DispositionData other = (DispositionData) obj;
		return Objects.equals(this.passengerId, other.passengerId)
				&& Objects.equals(this.flightId, other.flightId)
				&& Objects.equals(this.statusId, other.statusId)
				&& Objects.equals(this.comments, other.comments);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
