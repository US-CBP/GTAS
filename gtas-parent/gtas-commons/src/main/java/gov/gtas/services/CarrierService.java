/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Carrier;

import java.util.List;

public interface CarrierService {
    
    public Carrier create(Carrier carrier);
    public Carrier delete(Long id);
    public List<Carrier> findAll();
    public Carrier update(Carrier carrier) ;
    public Carrier findById(Long id);
    public Carrier getCarrierByTwoLetterCode(String carrierCode);
    public Carrier getCarrierByThreeLetterCode(String carrierCode);

}
