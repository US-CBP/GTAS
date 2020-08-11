/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.validators.Validatable;

import java.util.UUID;

public class SeatVo implements Validatable {
	private String number;
	private String cabinClass;
	private Boolean apis = Boolean.valueOf(false);
	private UUID uuid = UUID.randomUUID();

	/** unique id to reference back to a passenger */
	private String travelerReferenceNumber;

	/*
	 * flight details: origin and dest should be sufficient to uniquely identify a
	 * flight within a pnr itinerary.
	 */
	private String origin;
	private String destination;

	public String getNumber() {
		return number;
	}

	public Boolean getApis() {
		return apis;
	}

	public void setApis(Boolean apis) {
		this.apis = apis;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTravelerReferenceNumber() {
		return travelerReferenceNumber;
	}

	public void setTravelerReferenceNumber(String travelerReferenceNumber) {
		this.travelerReferenceNumber = travelerReferenceNumber;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public boolean isValid() {
		return StringUtils.isNotBlank(this.number) && StringUtils.isNotBlank(this.origin)
				&& StringUtils.isNotBlank(this.destination);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public UUID getUuid() {
		return uuid;
	}
	public String getCabinClass() {
		return cabinClass;
	}

	public void setCabinClass(String cabinClass) {
		this.cabinClass = cabinClass;
	}
}
