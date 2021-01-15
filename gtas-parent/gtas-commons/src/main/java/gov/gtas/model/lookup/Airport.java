/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.cache.annotation.Cacheable;

import gov.gtas.model.BaseEntityAudit;

@Cacheable
@Entity
@Table(name = "airport", indexes = { @Index(columnList = "iata", name = "airport_iata_index"), @Index(columnList = "updated_at", name = "airport_updated_at_index") })
public class Airport extends BaseEntityAudit {

	private Long originId;
	private String name;
	@Column(length = 3)
	private String iata;

	@Column(length = 4)
	private String icao;

	private String country;

	private String city;

	@Column(precision = 9, scale = 6)
	private BigDecimal latitude;

	@Column(precision = 9, scale = 6)
	private BigDecimal longitude;

	@Column(name = "utc_offset")
	private Integer utcOffset;

	private Boolean archived;

	private String timezone;

	public Airport() {
	}

	public Airport(Long id, Long originId, String name, String iata, String icao, String country, String city,
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
		this.setUpdatedAt(new Date());
	}

	public Airport(Long id, Long originId, String name, String iata, String icao, String country, String city,
			BigDecimal latitude, BigDecimal longitude, Integer utcOffset, String timezone) {
		this(id, originId, name, iata, icao, country, city, latitude, longitude, utcOffset, timezone, false);
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

	public void setArchived(Boolean data) {
		this.archived= data;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.iata, this.icao);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Airport other = (Airport) obj;
		return Objects.equals(this.iata, other.iata) && Objects.equals(this.icao, other.icao);
	}

	@Override
	public String toString() {
		return this.iata;
	}
}
