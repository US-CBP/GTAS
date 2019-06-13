/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Carrier;
import gov.gtas.repository.CarrierRepository;
import gov.gtas.repository.CarrierRepositoryCustom;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class CarrierServiceImpl implements CarrierService {

    @Resource
    private CarrierRepository carrierRespository;
    @Resource
    private CarrierRepositoryCustom carrierRepoCust;
    
    @Override
    @Transactional
    public Carrier create(Carrier carrier) {
        return carrierRespository.save(carrier);
    }

    @Override
    @Transactional
    public Carrier delete(Long id) {
        Carrier carrier = this.findById(id);
        if(carrier != null){
            carrierRespository.delete(carrier);
        }
        return carrier;
    }

    @Override
    @Transactional
    public List<Carrier> findAll() {
        
        return (List<Carrier>)carrierRespository.findAll();
    }

    @Override
    @Transactional
    public Carrier update(Carrier carrier) {
      // validate no duplicate codes
      return carrierRespository.save(carrier);
    }

    @Override
    @Transactional
    public Carrier findById(Long id) {
        return carrierRespository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Carrier restore(Carrier carrier) {
      return carrierRepoCust.restore(carrier);
    }

    @Override
    @Transactional
    public int restoreAll() {
      return carrierRepoCust.restoreAll();
    }

    @Override
    @Transactional
    public Carrier getCarrierByTwoLetterCode(String carrierCode) {
        Carrier carrier = null;
        List<Carrier> carriers = carrierRespository.getCarrierByTwoLetterCode(carrierCode);
        if(carriers != null && carriers.size() > 0)
            carrier = carriers.get(0);
        return carrier;
    }

    @Override
    @Transactional
    public Carrier getCarrierByThreeLetterCode(String carrierCode) {
        Carrier carrier = null;
        List<Carrier> carriers = carrierRespository.getCarrierByThreeLetterCode(carrierCode);
        if(carriers != null && carriers.size() > 0)
            carrier = carriers.get(0);
        return carrier;
    }

}
