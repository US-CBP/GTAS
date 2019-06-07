/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.CountryRestore;
import gov.gtas.repository.CountryRestoreRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class CountryRestoreServiceImpl implements CountryRestoreService {

    @Resource
    private CountryRestoreRepository repo;
    

    @Override
    @Transactional
    public List<CountryRestore> findAll() {
        
        return (List<CountryRestore>)repo.findAll();
    }


    @Override
    @Transactional
    public CountryRestore findById(Long id) {
        
        return repo.findById(id).orElse(null);
    }

}
