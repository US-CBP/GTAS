/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.AirportRestore;
import gov.gtas.repository.AirportRestoreRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class AirportRestoreServiceImpl implements AirportRestoreService {

    @Resource
    private AirportRestoreRepository repo;
    

    @Override
    @Transactional
    public List<AirportRestore> findAll() {
        
        return (List<AirportRestore>)repo.findAll();
    }


    @Override
    @Transactional
    public AirportRestore findById(Long id) {
        
        return repo.findById(id).orElse(null);
    }

}
