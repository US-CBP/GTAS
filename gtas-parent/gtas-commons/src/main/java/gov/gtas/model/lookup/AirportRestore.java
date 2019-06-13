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
@Table(name = "airportRestore", indexes = { @Index(columnList = "iata", name = "airportRestore_iata_index") })
public class AirportRestore extends BaseEntity {
    public AirportRestore() { }
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

    public String getName() {
        return name;
    }
    public String getIata() {
        return iata;
    }
    public String getIcao() {
        return icao;
    }
    public String getCountry() {
        return country;
    }
    public String getCity() {
        return city;
    }
    public BigDecimal getLatitude() {
        return latitude;
    }
    public BigDecimal getLongitude() {
        return longitude;
    }
    public Integer getUtcOffset() {
        return utcOffset;
    }
    public String getTimezone() {
        return timezone;
    }
    private String timezone;
    
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
        final AirportRestore other = (AirportRestore) obj;
        return Objects.equals(this.iata, other.iata)
                && Objects.equals(this.icao, other.icao);
    }
    
    @Override
    public String toString() {
        return this.iata;
    }
}
