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
@Table(name = "carrierRestore", indexes = { @Index(columnList = "iata", name = "carrierRestore_iata_index") })
public class CarrierRestore extends BaseEntity {
  public CarrierRestore() { }
  private String name;
  
  @Column(length=2)
  private String iata;
  
  @Column(length=3)
  private String icao;

  public String getName() {
      return name;
  }
  public String getIata() {
      return iata;
  }
  public String getIcao() {
    return icao;
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
        CarrierRestore other = (CarrierRestore) obj;
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
