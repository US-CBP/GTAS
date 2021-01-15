/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.lookup;

import java.io.Serializable;
import java.math.BigDecimal;

public class AirportVo  implements Serializable  {

	private Long id;
	private Long originId;
	private String name;
	private String iata;
	private String icao;
	private String country;
	private String city;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private Integer utcOffset;
	private String timezone;
	private Boolean archived;

	public AirportVo(Long id, Long originId, String name, String iata, String icao, String country, String city,
			BigDecimal latitude, BigDecimal longitude, Integer utcOffset, String timezone, Boolean archived) {
		this.id = id;
		this.originId = originId;
		this.name = name;
		this.iata = iata;
		this.icao = icao;
		this.country = country;
		this.city = city;
		this.latitude = latitude;
		this.longitude = longitude;
		this.utcOffset = utcOffset;
		this.timezone = timezone;
		this.archived = archived;
	}

	public AirportVo(Long id, Long originId, String name, String iata, String icao, String country, String city,
								 BigDecimal latitude, BigDecimal longitude, Integer utcOffset, String timezone) {
		this(id, originId, name, iata, icao, country, city, latitude, longitude, utcOffset, timezone, false);
	}

	public AirportVo() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOriginId() {
		return originId;
	}

	public void setOriginId(Long data) {
		this.originId = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String data) {
		this.name = data;
	}

	public String getIata() {
		return iata;
	}

	public void setIata(String data) {
		this.iata = data;
	}

	public String getIcao() {
		return icao;
	}

	public void setIcao(String data) {
		this.icao = data;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String data) {
		this.country = data;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String data) {
		this.city = data;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal data) {
		latitude = data;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal data) {
		longitude = data;
	}

	public Integer getUtcOffset() {
		return utcOffset;
	}

	public void setUtcOffset(Integer data) {
		utcOffset = data;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String data) {
		timezone = data;
	}

	public Boolean getArchived() { return archived; }

	public void setArchived(Boolean archived) { this.archived = archived; }

}
