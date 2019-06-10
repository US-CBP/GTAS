/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.CarrierRestore;
import gov.gtas.repository.CarrierRestoreRepository;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class CarrierRestoreServiceImpl implements CarrierRestoreService {

    @Resource
    private CarrierRestoreRepository repo;
    

    @Override
    @Transactional
    public List<CarrierRestore> findAll() {
        
        return (List<CarrierRestore>)repo.findAll();
    }


    @Override
    @Transactional
    public CarrierRestore findById(Long id) {
        
        return repo.findById(id).orElse(null);
    }

}
