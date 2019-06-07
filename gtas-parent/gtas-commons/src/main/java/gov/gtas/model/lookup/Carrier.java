/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

import gov.gtas.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.cache.annotation.Cacheable;

@Cacheable
@Entity
@Table(name = "carrier", indexes = { @Index(columnList = "iata", name = "carrier_iata_index") })
public class Carrier extends BaseEntity {
    public Carrier() { }
    private Long originId;
    private String name;
    
    @Column(length=2)
    private String iata;
    
    @Column(length=3)
    private String icao;

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
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((iata == null) ? 0 : iata.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Carrier other = (Carrier) obj;
        if (iata == null) {
            if (other.iata != null)
                return false;
        } else if (!iata.equals(other.iata))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return this.iata;
    }        
}
