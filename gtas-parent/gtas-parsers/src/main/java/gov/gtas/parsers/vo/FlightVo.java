/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.Date;
import java.util.UUID;

import gov.gtas.model.BookingDetail;
import gov.gtas.parsers.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.validators.Validatable;

public class FlightVo implements Validatable {

	private UUID uuid = UUID.randomUUID();
	private String carrier;
	private String flightNumber;
	private String origin;
	private String destination;
	/*
	 * THIS IS NOT UTC TIME. LOCAL ETD DATE IS IN WHATEVER LOCAL TIME OF THE AIRPORT
	 * WHERE FLIGHT ORIGINATES This field (stripped to just the date instead of a
	 * date-time) is used in the loader as part of the unique label of a flight.
	 */
	private Date localEtdDate;

	// THIS IS NOT UTC TIME. LOCAL ETA DATE IS IN WHATEVER LOCAL TIME OF THE AIRPORT
	// WHERE FLIGHT IS DESTINED TO
	private Date localEtaDate;
	private String marketingFlightNumber;
	private boolean isCodeShareFlight = false;
	private boolean isMarketingFlight = false;
	private String idTag;

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean isMarketingFlight() {
		return isMarketingFlight;
	}

	public void setMarketingFlight(boolean isMarketingFlight) {
		this.isMarketingFlight = isMarketingFlight;
	}

	public String getMarketingFlightNumber() {
		return marketingFlightNumber;
	}

	public void setMarketingFlightNumber(String mingFlightNumber) {
		this.marketingFlightNumber = mingFlightNumber;
	}

	public boolean isCodeShareFlight() {
		return isCodeShareFlight;
	}

	public void setCodeShareFlight(boolean isCodeShareFlight) {
		this.isCodeShareFlight = isCodeShareFlight;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getIdTag() {
		return idTag;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
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

	public Date getLocalEtdDate() {
		return localEtdDate;
	}

	public void setLocalEtdDate(Date localEtdDate) {
		this.localEtdDate = localEtdDate;
	}

	public Date getLocalEtaDate() {
		return localEtaDate;
	}

	public void setLocalEtaDate(Date localEtaDate) {
		this.localEtaDate = localEtaDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public boolean isValid() {
		return StringUtils.isNotBlank(this.destination) && StringUtils.isNotBlank(this.origin)
				&& StringUtils.isNotBlank(this.flightNumber) && StringUtils.isNotBlank(this.carrier)
				&& this.localEtdDate != null;
	}

	public UUID getUuid() {
		return uuid;
	}

	public boolean equalsThisBD(BookingDetail bookingDetail) {
		return flightNumber != null && flightNumber.equals(bookingDetail.getFlightNumber()) && localEtdDate != null
				&& DateUtils.stripTime(localEtdDate).equals(bookingDetail.getEtdDate()) && origin != null
				&& origin.equals(bookingDetail.getOrigin()) && destination != null
				&& destination.equals(bookingDetail.getDestination());
	}
}
