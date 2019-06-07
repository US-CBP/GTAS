/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.Carrier;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CarrierRepository extends CrudRepository<Carrier, Long>{

  @Query("SELECT c FROM Carrier c WHERE UPPER(c.iata) = UPPER(:carrierCode)")
  public List<Carrier> getCarrierByTwoLetterCode(@Param("carrierCode") String carrierCode);
    
    @Query("SELECT c FROM Carrier c WHERE UPPER(c.icao) = UPPER(:carrierCode)")
    public List<Carrier> getCarrierByThreeLetterCode(@Param("carrierCode") String carrierCode);
    
    default Carrier findOne(Long carrierId) {
    	return findById(carrierId).orElse(null);
    }

}
