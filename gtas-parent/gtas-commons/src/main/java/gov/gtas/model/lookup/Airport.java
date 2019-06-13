/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.cache.annotation.Cacheable;

import gov.gtas.model.BaseEntity;

@Cacheable
@Entity
@Table(name = "airport", indexes = { @Index(columnList = "iata", name = "airport_iata_index") })
public class Airport extends BaseEntity {
    public Airport() { }
    private Long originId;
    private String name;
    
    @Column(length=3)
    private String iata;
    
    @Column(length=4)
    private String icao;
    
    private String country;
    
    private String city;
    
    @Column(precision = 9, scale = 6 )
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6 )
    private BigDecimal longitude;
    
    @Column(name = "utc_offset")
    private Integer utcOffset;
    private String timezone;

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
        return Objects.equals(this.iata, other.iata)
                && Objects.equals(this.icao, other.icao);
    }
    
    @Override
    public String toString() {
        return this.iata;
    }
}
