/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

import gov.gtas.model.BaseEntity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.cache.annotation.Cacheable;

@Cacheable
@Entity
@Table(name = "countryRestore", indexes = { @Index(columnList = "iso3", name = "countryRestore_iso3_index") })
public class CountryRestore extends BaseEntity {
    public CountryRestore() { }
    @Column(length = 2)
    private String iso2;

    @Column(length = 3)
    private String iso3;
    
    private String name;
    
    @Column(name = "iso_numeric", length = 3)
    private String isoNumeric;

    public String getIso2() {
        return iso2;
    }
    public String getIso3() {
        return iso3;
    }
    public String getName() {
        return name;
    }
    public String getIsoNumeric() {
        return isoNumeric;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.iso2, this.iso3);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CountryRestore other = (CountryRestore) obj;
        return Objects.equals(this.iso2, other.iso2)
                && Objects.equals(this.iso3, other.iso3);
    }
    
    @Override
    public String toString() {
        return this.iso2;
    }
}
