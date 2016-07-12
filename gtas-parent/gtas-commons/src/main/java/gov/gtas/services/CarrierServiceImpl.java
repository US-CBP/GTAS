/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Carrier;
import gov.gtas.repository.CarrierRepository;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class CarrierServiceImpl implements CarrierService {

    @Resource
    private CarrierRepository carrierRespository;
    
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
        // NO IMPLEMENTATION
        return null;
    }

    @Override
    @Transactional
    public Carrier findById(Long id) {
        
        return carrierRespository.findOne(id);
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
